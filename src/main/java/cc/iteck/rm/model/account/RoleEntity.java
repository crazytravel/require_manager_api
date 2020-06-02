package cc.iteck.rm.model.account;

import com.baomidou.mybatisplus.annotation.TableName;
import cc.iteck.rm.model.AbstractEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("dc_role")
public class RoleEntity extends AbstractEntity {

    private String code;
    private String name;
    private String description;

}
