package cc.iteck.rm.model.account;

import com.baomidou.mybatisplus.annotation.TableName;
import cc.iteck.rm.model.AbstractEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("dc_user_role")
public class UserRoleEntity extends AbstractEntity {

    private String userId;
    private String roleId;

}
