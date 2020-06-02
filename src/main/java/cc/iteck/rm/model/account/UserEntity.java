package cc.iteck.rm.model.account;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import cc.iteck.rm.model.AbstractEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("rm_user")
public class UserEntity extends AbstractEntity {

    private String username;
    private String password;
    private String nickname;
    private String realName;
    private String phone;
    private String email;
    @TableField("WHO_IS_WHO_ID")
    private String whoIsWhoId;
    private Boolean isActive;
}
