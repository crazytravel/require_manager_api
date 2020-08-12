package cc.iteck.rm.model.project;

import cc.iteck.rm.model.AbstractEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("rm_project_user")
public class ProjectUserEntity extends AbstractEntity {

    private String projectId;
    private String userId;
    private Boolean owner;
}
