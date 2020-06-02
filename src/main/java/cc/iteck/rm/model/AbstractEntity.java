package cc.iteck.rm.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
@KeySequence(value = "SEQ_STRING_KEY")
public abstract class AbstractEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField(value = "created_at")
    private final LocalDateTime createdAt = LocalDateTime.now(ZoneOffset.UTC);

    @TableField(value = "modified_at")
    private final LocalDateTime modifiedAt = LocalDateTime.now(ZoneOffset.UTC);
}
