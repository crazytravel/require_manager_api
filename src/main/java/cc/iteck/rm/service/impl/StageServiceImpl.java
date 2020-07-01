package cc.iteck.rm.service.impl;

import cc.iteck.rm.mapper.StageMapper;
import cc.iteck.rm.model.stage.StageDto;
import cc.iteck.rm.model.stage.StageEntity;
import cc.iteck.rm.service.StageService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StageServiceImpl implements StageService {

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
        stageMapper.insert(stageEntity);
        BeanUtils.copyProperties(stageEntity, stageDto);
        return stageDto;
    }

    @Override
    public StageDto findStage(String id) {
        StageEntity stageEntity = stageMapper.selectById(id);
        StageDto stageDto = StageDto.builder().build();
        BeanUtils.copyProperties(stageEntity, stageDto);
        return stageDto;
    }

    @Override
    public StageDto updateStage(StageDto stageDto) {
        StageEntity stageEntity = stageMapper.selectById(stageDto.getId());
        BeanUtils.copyProperties(stageDto, stageEntity);
        stageMapper.updateById(stageEntity);
        BeanUtils.copyProperties(stageEntity, stageDto);
        return stageDto;
    }

    @Override
    public void deleteStage(String id) {
        stageMapper.deleteById(id);
    }
}
