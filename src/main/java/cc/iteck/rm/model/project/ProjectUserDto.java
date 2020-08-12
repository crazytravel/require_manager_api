package cc.iteck.rm.model.project;

import cc.iteck.rm.model.AbstractDto;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectUserDto extends AbstractDto {

    private String projectId;
    private String userId;
    private Boolean owner;
}
