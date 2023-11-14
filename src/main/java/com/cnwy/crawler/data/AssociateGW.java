package com.cnwy.crawler.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 官网绑定记录对象
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class AssociateGW extends AbstractEntity {

    @Column(nullable = false)
    private Long enterpriseID;//企业ID
    private String reason;//绑定原因
    private String domain;//官网二级域名
    @Column(nullable = false)
    private LocalDateTime createTime;//入库时间
    @Column(nullable = false)
    private LocalDateTime updateTime;//更新时间

}
