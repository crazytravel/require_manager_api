package cc.iteck.rm.service;

import cc.iteck.rm.model.stage.StageDto;

import java.util.List;

public interface StageService {
    List<StageDto> findAllStages();

    StageDto createNewStage(StageDto stageDto);

    StageDto findStage(String id);

    StageDto updateStage(StageDto stageDto);

    void deleteStage(String id);

    List<StageDto> findStagesByProjectId(String projectId);
}
