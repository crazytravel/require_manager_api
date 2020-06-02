package cc.iteck.rm.model.account;

import cc.iteck.rm.model.AbstractDto;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleDto extends AbstractDto {
    private String userId;
    private String roleId;
}
