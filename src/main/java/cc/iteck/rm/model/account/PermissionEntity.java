package cc.iteck.rm.model.account;

import com.baomidou.mybatisplus.annotation.TableName;
import cc.iteck.rm.model.AbstractEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("rm_permission")
public class PermissionEntity extends AbstractEntity {

    private String code;
    private String name;
    private String description;
}
