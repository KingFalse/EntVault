package com.cnwy.crawler.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 企业实体
 * 企业上下级关系存在多对多关系,单独表存储
 */
@Data
@Entity
public class Enterprise extends AbstractEntity {

    @NotEmpty
    @Column(nullable = false, unique = true)
    private String name;//企业名称
    private String alias;//企业简称
    @Column(length = 2000)
    private String description;//企业简介
    private String website;//企业官网
    private String telephone;//联系电话
    private String status;//经营状态,如:营业,暂停营业,注销
    private String type;//平台企业类型标签
    private String unifiedSocialCreditCode;//统一社会信用代码,辅助企业唯一判断字段,因为部分机关单位无此字段
    private String regNo;//注册号码,辅助企业唯一判断字段
    private String email;//邮箱
    private String orgType;//企业类型,多个逗号隔开,如:有限责任公司,国有企业,有限责任公司(国有独资)
    private String province;//省
    private String city;//市
    private String district;//区
    private String address;//详细地址
    private String historicalNames;//历史名称,多个用逗号隔开
    private String tycID;//天眼查ID
    private String qccID;//企查查ID
    private String hitReason;//入选原因(企查查标记国企,天眼查标记国企,族谱标记国企,人为标记国企等)
    private String hitPlatform;//如选平台,如:天眼查,企查查
    private String tags;//平台给定标签,多个逗号隔开,如:国企,外资,外资企业
    @Column(nullable = false)
    private LocalDateTime createTime;//入库时间
    @Column(nullable = false)
    private LocalDateTime updateTime;//更新时间

}
