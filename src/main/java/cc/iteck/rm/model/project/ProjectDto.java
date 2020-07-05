package cc.iteck.rm.model.project;

import cc.iteck.rm.model.AbstractDto;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto extends AbstractDto {
    private String code;
    private String name;
    private String description;
    private String ownerUserId;
    private Boolean active;
}
