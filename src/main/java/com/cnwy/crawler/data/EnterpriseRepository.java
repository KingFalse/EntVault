package com.cnwy.crawler.data;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long>, JpaSpecificationExecutor<Enterprise> {

    /**
     * 根据域名查询企业列表
     *
     * @param website
     * @return
     */
    List<Enterprise> findAllByWebsiteContains(String website);

    /**
     * 根据统一社会信用代码查询对应企业
     *
     * @param unifiedSocialCreditCode
     * @return
     */
    Enterprise findByunifiedSocialCreditCode(String unifiedSocialCreditCode);

    /**
     * 根据注册号查询对应企业
     *
     * @param regNo
     * @return
     */
    Enterprise findByRegNo(String regNo);

    /**
     * 根据企业名称查询是否存在
     * @param name
     * @return
     */
    Boolean existsByName(String name);

    /**
     * 根据企业名称模糊搜索:企业名称%
     *
     * @param name
     * @return
     */
    Enterprise findByName(String name);

    List<Enterprise> findByNameLike(String nameLikeOnLeft);
}
