package com.cnwy.crawler.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 股东表,记录企业的股东信息
 * 一对多
 */
@Data
@Entity
public class Shareholder extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String enterpriseID;//企业ID
    private String shareholderEnterpriseID;//股东企业ID

    @Column(nullable = false)
    private LocalDateTime createTime;//入库时间
    @Column(nullable = false)
    private LocalDateTime updateTime;//更新时间

}
