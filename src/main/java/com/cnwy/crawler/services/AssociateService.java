package com.cnwy.crawler.services;

import com.alibaba.fastjson2.JSON;
import com.cnwy.crawler.data.*;
import com.cnwy.crawler.util.DomainUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AssociateService {

    final
    EnterpriseRepository enterpriseRepository;
    final
    BeianService beianService;

    final
    AssociateGWRepository associateGWRepository;

    public AssociateService(EnterpriseRepository enterpriseRepository, BeianService beianService, AssociateGWRepository associateGWRepository) {
        this.enterpriseRepository = enterpriseRepository;
        this.beianService = beianService;
        this.associateGWRepository = associateGWRepository;
    }

    /**
     * 将输入的企业链接与企业库进行关联
     *
     * @param link
     * @return 绑定的企业, 如果返回null表示自动绑定失败
     * @throws IOException
     */
    @Nullable
    public AssociateGW doAssociate(String link) throws IOException {
        // TODO: 2023/11/14 排除其他绑定逻辑,是官网的才往下走
        AssociateGW associateGW = null;
        // TODO: 2023/11/13 提取二级域名
        String subDomain = DomainUtil.getDomainFromURL(link);
        // TODO: 2023/11/13 提取根域名
        String rootDomain = DomainUtil.getRootDomainFromURL(link);
        // TODO: 2023/11/13 二级域名搜索企业库 ,如果唯一则绑定
        List<Enterprise> enterprises = enterpriseRepository.findAllByWebsiteContains(subDomain);
        if (enterprises.size() == 1) {
            Enterprise enterprise = enterprises.get(0);
            log.info("根据二级域名查询企业库,获得唯一符合企业,开始绑定link = {}   与    企业库ID = {}", link, enterprise.getId());
            log.info("绑定结束");
            associateGW = new AssociateGW(enterprise.getId(), "根据二级域名查询企业库,获得唯一符合企业", subDomain, LocalDateTime.now(), LocalDateTime.now());
            return associateGWRepository.save(associateGW);
        }
        // TODO: 2023/11/13 查询根域名备案 ,获取企业信息, 反查企业库,如果唯一则绑定
        Beian beian = beianService.getFromRootDomain(rootDomain);
        if (beian == null) {
            log.error("未能查询到域名:{} 的备案信息,请开发同学检查备案抓取页面是否有变!");
            // TODO: 2023/11/14 域名备案查询错误,请检查网页是否有变
            return associateGW;
        }

        // TODO: 2023/11/13 根据返回的备案信息按照统一社会信用代码,注册号,企业名称 的优先级开始进行查询绑定
        if (beian.getUnifiedSocialCreditCode().length() > 5) {
            Enterprise ent = enterpriseRepository.findByunifiedSocialCreditCode(beian.getUnifiedSocialCreditCode());
            if (ent != null) {
                log.info("根据根域名备案统一社会信用代码查询企业库,获得唯一符合企业,开始绑定link = {}   与    企业库ID = {}", link, ent.getId());
                log.info("绑定结束");
                associateGW = new AssociateGW(ent.getId(), "根据根域名备案统一社会信用代码查询企业库,获得唯一符合企业", subDomain, LocalDateTime.now(), LocalDateTime.now());
                return associateGWRepository.save(associateGW);
            }
        }
        if (beian.getRegNo().length() > 5) {
            Enterprise ent = enterpriseRepository.findByRegNo(beian.getRegNo());
            if (ent != null) {
                log.info("根据域名备案注册号查询企业库,获得唯一符合企业,开始绑定link = {}   与    企业库ID = {}", link, ent.getId());
                log.info("绑定结束");
                associateGW = new AssociateGW(ent.getId(), "根据域名备案注册号查询企业库,获得唯一符合企业", subDomain, LocalDateTime.now(), LocalDateTime.now());
                return associateGWRepository.save(associateGW);
            }
        }

        if (beian.getCompany().length() > 5) {
            Enterprise ent = enterpriseRepository.findByName(beian.getCompany());
            if (ent != null) {
                log.info("根据企业名称查询企业库,获得唯一符合企业,开始绑定link = {}   与    企业库ID = {}", link, ent.getId());
                log.info("绑定结束");
                associateGW = new AssociateGW(ent.getId(), "根据企业名称查询企业库,获得唯一符合企业", subDomain, LocalDateTime.now(), LocalDateTime.now());
                return associateGWRepository.save(associateGW);
            }
            // TODO: 2023/11/14 目前发现部分政府单位企业名称错乱,需要进行修正,例如:邯郸市肥乡区人民政府办公室（区商务局、投资促进局、政府外事办公室、地方金融监督管理局（金融工作办公室））
            // 样本: https://www.qcc.com/firm/g866a7ef22b74883842fd8575001d1d5.html
            if (beian.getNature().equals("政府机关") && beian.getCompany().contains("（")) {
                log.info("开始进行政府机关名称截取匹配");
                String temp = beian.getCompany().split("（")[0];
                List<Enterprise> byNameLike = enterpriseRepository.findByNameLike(temp + "%");
                if (byNameLike.size() == 1) {
                    ent = byNameLike.get(0);
                    log.info("针对政府机关备案名称处理后,根据企业名称查询企业库,获得唯一符合企业,开始绑定link = {}   与    企业库ID = {}", link, ent.getId());
                    log.info("绑定结束");
                    associateGW = new AssociateGW(ent.getId(), "针对政府机关备案名称处理后,根据企业名称查询企业库,获得唯一符合企业", subDomain, LocalDateTime.now(), LocalDateTime.now());
                    return associateGWRepository.save(associateGW);
                }
            }
            // TODO: 2023/11/14 如果到此还未绑定完成,则执行其他绑定规则
            log.error("链接:{} 自动绑定失败,请联系开发同学完善绑定机制或者手动绑定!", link);
        }
        return associateGW;
    }

}
