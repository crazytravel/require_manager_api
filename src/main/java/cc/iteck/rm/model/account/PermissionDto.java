package cc.iteck.rm.model.account;


import cc.iteck.rm.model.AbstractDto;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDto extends AbstractDto {

    private static final long serialVersionUID = 8370223436521051871L;

    private String code;
    private String name;
    private String description;
}
