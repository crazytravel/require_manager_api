package cc.iteck.rm.service.impl;

import cc.iteck.rm.exception.ResourceNotFoundException;
import cc.iteck.rm.exception.ResourceOperateFailedException;
import cc.iteck.rm.mapper.StageMapper;
import cc.iteck.rm.model.stage.StageDto;
import cc.iteck.rm.model.stage.StageEntity;
import cc.iteck.rm.service.StageService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StageServiceImpl implements StageService {

    private final static String TAIL_STAGE_ID = "-1";
    private final StageMapper stageMapper;

    public StageServiceImpl(StageMapper stageMapper) {
        this.stageMapper = stageMapper;
    }


    @Override
    public List<StageDto> findAllStages() {
        List<StageEntity> stages = stageMapper.selectList(Wrappers.emptyWrapper());
        return stages.stream().map(stageEntity -> {
            StageDto stage = StageDto.builder().build();
            BeanUtils.copyProperties(stageEntity, stage);
            return stage;
        }).collect(Collectors.toList());
    }

    @Override
    public StageDto createNewStage(StageDto stageDto) {
        StageEntity stageEntity = StageEntity.builder().build();
        BeanUtils.copyProperties(stageDto, stageEntity);
        try {
            stageMapper.insert(stageEntity);
        } catch (Exception e) {
            throw new ResourceOperateFailedException("create stage failed, error: ", e);
        }
        BeanUtils.copyProperties(stageEntity, stageDto);
        return stageDto;
    }

    @Override
    public StageDto findStage(String id) {
        StageEntity stageEntity = stageMapper.selectById(id);
        if (stageEntity == null) {
            throw new ResourceNotFoundException("cannot found stage by id: " + id);
        }
        StageDto stageDto = StageDto.builder().build();
        BeanUtils.copyProperties(stageEntity, stageDto);
        return stageDto;
    }

    @Override
    public StageDto updateStage(StageDto stageDto) {
        StageEntity stageEntity = stageMapper.selectById(stageDto.getId());
        if (stageEntity == null) {
            throw new ResourceNotFoundException("cannot found stage by id: " + stageDto.getId());
        }
        BeanUtils.copyProperties(stageDto, stageEntity);
        try {
            stageMapper.updateById(stageEntity);
        } catch (Exception e) {
            throw new ResourceOperateFailedException("create stage failed, error: ", e);
        }
        BeanUtils.copyProperties(stageEntity, stageDto);
        return stageDto;
    }

    @Override
    public void deleteStage(String id) {
        try {
            stageMapper.deleteById(id);
        } catch (Exception e) {
            throw new ResourceOperateFailedException("delete project failed by id: " + id, e);
        }
    }

    @Override
    public List<StageDto> findSortedStagesByProjectId(String projectId) {
        List<StageEntity> stageEntities = stageMapper.selectList(Wrappers.lambdaQuery(StageEntity.class)
                .eq(StageEntity::getProjectId, projectId));
        if (stageEntities == null || stageEntities.isEmpty()) {
            return new ArrayList<>();
        }
        StageEntity tailNode = stageEntities.stream().filter(stageEntity ->
                TAIL_STAGE_ID.equals(stageEntity.getNextId())).findFirst().orElseThrow();
        StageDto dto = StageDto.builder().build();
        BeanUtils.copyProperties(tailNode, dto);
        List<StageDto> sortedStages = new ArrayList<>();
        sortedStages.add(dto);
        sort(stageEntities, dto.getId(), sortedStages);
        Collections.reverse(sortedStages);
        return sortedStages;
    }

    private void sort(List<StageEntity> stages, String currentId, List<StageDto> sortedStages) {
        if (sortedStages.size() == stages.size()) {
            return;
        }
        stages.forEach(entity -> {
            String nextId = entity.getNextId();
            if (currentId.equals(nextId)) {
                StageDto dto = StageDto.builder().build();
                BeanUtils.copyProperties(entity, dto);
                sortedStages.add(dto);
                nextId = entity.getId();
                sort(stages, nextId, sortedStages);
            }
        });
    }
}
