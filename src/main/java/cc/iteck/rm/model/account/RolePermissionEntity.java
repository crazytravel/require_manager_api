package cc.iteck.rm.model.account;

import com.baomidou.mybatisplus.annotation.TableName;
import cc.iteck.rm.model.AbstractEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("dc_role_permission")
public class RolePermissionEntity extends AbstractEntity {

    private String roleId;
    private String permissionId;
}
