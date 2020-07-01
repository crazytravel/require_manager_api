package cc.iteck.rm.model.stage;

import cc.iteck.rm.model.AbstractDto;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageDto extends AbstractDto {
    private String name;
    private String projectId;
}
