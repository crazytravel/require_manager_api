package cc.iteck.rm.model.account;


import cc.iteck.rm.model.AbstractDto;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDto extends AbstractDto implements GrantedAuthority {

    private static final long serialVersionUID = 8370223436521051871L;

    private String code;
    private String name;
    private String description;

    @Override
    public String getAuthority() {
        return code;
    }
}
