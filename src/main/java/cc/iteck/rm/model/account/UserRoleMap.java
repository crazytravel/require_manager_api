package cc.iteck.rm.model.account;

import lombok.Data;

@Data
public class UserRoleMap {

    private String username;
    private String password;
    private String nickname;
    private String realName;
    private String phone;
    private String email;
    private Boolean isActive;

    private String roleCode;
    private String roleName;
    private String roleDescription;
}
