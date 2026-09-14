package io.github.zcy427.rbac.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

// 数据库实体公共字段
@Getter
@Setter
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}
