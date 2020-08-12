package cc.iteck.rm.model.account;

import cc.iteck.rm.model.AbstractDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto extends AbstractDto {

    private static final long serialVersionUID = 3684861548582542137L;

    private String id;
    @NotNull
    private String username;
    @NotNull
    @JsonIgnore
    private String password;
    private String nickname;
    private String realName;
    private String phone;
    private String email;
    private String whoIsWhoId;
    private Boolean isActive;

    private List<RoleDto> roles;
    private List<PermissionDto> permissions;
}