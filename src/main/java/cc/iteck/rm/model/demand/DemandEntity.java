package cc.iteck.rm.model.demand;

import cc.iteck.rm.model.AbstractEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("rm_demand")
public class DemandEntity extends AbstractEntity {

    private String name;
}
