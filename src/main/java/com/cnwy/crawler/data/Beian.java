package com.cnwy.crawler.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Beian extends AbstractEntity {

    @NotEmpty
    @Column(nullable = false, unique = true)
    private String domain;//根域名
    private String company;//根域名
    private String regNo;//注册号码
    private String icpNo;//网站备案/许可证号
    private String unifiedSocialCreditCode;//统一社会信用代码
    private String nature;//主办单位性质
    @Column(nullable = false)
    private LocalDateTime createTime;//入库时间
    @Column(nullable = false)
    private LocalDateTime updateTime;//更新时间

}
