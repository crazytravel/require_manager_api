package cc.iteck.rm.model.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderTaskForm {
    private String sourceStageId;
    private String destinationStageId;
    private Integer sourceIndex;
    private Integer destinationIndex;
}
