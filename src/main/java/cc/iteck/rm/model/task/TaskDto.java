package cc.iteck.rm.model.task;

import cc.iteck.rm.model.AbstractDto;
import cc.iteck.rm.model.account.UserDto;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto extends AbstractDto {

    private static final long serialVersionUID = 1419527644248591421L;

    private String content;
    private String projectId;
    private String stageId;
    private String status;
    private String nextId;
    private String userId;
}
