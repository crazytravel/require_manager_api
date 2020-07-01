package cc.iteck.rm.controller.v1;

import cc.iteck.rm.model.stage.StageDto;
import cc.iteck.rm.service.StageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/stages")
public class StageController {

    private final StageService stageService;

    public StageController(StageService stageService) {
        this.stageService = stageService;
    }


    @GetMapping
    public ResponseEntity<List<StageDto>> listStages() {
        List<StageDto> stages = stageService.findAllStages();
        return ResponseEntity.ok(stages);
    }

    @PostMapping
    public ResponseEntity<StageDto> createStage(@RequestBody StageDto stageDto) {
        StageDto stage = stageService.createNewStage(stageDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/" + stage.getId()).build().toUri();
        return ResponseEntity.created(location).body(stage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StageDto> getStage(@PathVariable String id) {
        StageDto stage = stageService.findStage(id);
        return ResponseEntity.ok(stage);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StageDto> updateStage(@RequestBody StageDto stageDto) {
        StageDto stage = stageService.updateStage(stageDto);
        return ResponseEntity.ok(stage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStage(@PathVariable String id) {
        stageService.deleteStage(id);
        return ResponseEntity.noContent().build();
    }

}
