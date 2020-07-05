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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public void deleteStage(String id) {
        StageEntity currentStage = stageMapper.selectById(id);
        StageEntity preStage = stageMapper.selectOne(Wrappers.<StageEntity>lambdaQuery().eq(StageEntity::getNextId, id));
        if (preStage != null) {
            preStage.setNextId(currentStage.getNextId());
            stageMapper.updateById(preStage);
        }
        stageMapper.deleteById(id);
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

    @Override
    public List<StageDto> findStagesByProjectId(String projectId) {
        List<StageEntity> stageEntities = stageMapper.selectList(Wrappers.<StageEntity>lambdaQuery()
                .eq(StageEntity::getProjectId, projectId).orderByAsc(StageEntity::getCreatedAt));
        return stageEntities.stream().map(stageEntity -> {
            StageDto stageDto = StageDto.builder().build();
            BeanUtils.copyProperties(stageEntity, stageDto);
            return stageDto;
        }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public StageDto moveUp(String id) {
        StageEntity currentStage = stageMapper.selectById(id);
        StageEntity preStage = stageMapper.selectOne(Wrappers.<StageEntity>lambdaQuery().eq(StageEntity::getNextId, id));
        StageEntity prePreStage = stageMapper.selectOne(Wrappers.<StageEntity>lambdaQuery().eq(StageEntity::getNextId, preStage.getId()));

        preStage.setNextId(currentStage.getNextId());
        currentStage.setNextId(preStage.getId());
        if (prePreStage != null) {
            prePreStage.setNextId(id);
            stageMapper.updateById(prePreStage);
        }
        stageMapper.updateById(currentStage);
        stageMapper.updateById(preStage);
        return findStage(id);
    }

    @Transactional
    @Override
    public StageDto moveDown(String id) {
        StageEntity currentStage = stageMapper.selectById(id);
        StageEntity preStage = stageMapper.selectOne(Wrappers.<StageEntity>lambdaQuery().eq(StageEntity::getNextId, id));
        StageEntity nextStage = stageMapper.selectById(currentStage.getNextId());
        String currentNextId = currentStage.getNextId();
        String nextStageNextId = nextStage.getNextId();
        currentStage.setNextId(nextStageNextId);
        nextStage.setNextId(id);
        if (preStage != null) {
            preStage.setNextId(currentNextId);
            stageMapper.updateById(preStage);
        }
        stageMapper.updateById(currentStage);
        stageMapper.updateById(nextStage);
        return findStage(id);
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
