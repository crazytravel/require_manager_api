package cc.iteck.rm.model.account;

import cc.iteck.rm.model.AbstractDto;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolePermissionDto extends AbstractDto {
    private String roleId;
    private String permissionId;
}
