package cc.iteck.rm.model.account;


import cc.iteck.rm.model.AbstractDto;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto extends AbstractDto {

    private static final long serialVersionUID = -8672780811835443251L;
    
    private String id;
    private String code;
    private String name;
    private String description;

    private List<PermissionDto> permissionDto;
}
