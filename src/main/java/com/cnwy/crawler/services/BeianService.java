package com.cnwy.crawler.services;

import com.alibaba.fastjson2.JSON;
import com.cnwy.crawler.data.Beian;
import com.cnwy.crawler.data.BeianRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
public class BeianService {

    private final BeianRepository beianRepository;

    public BeianService(BeianRepository beianRepository) {
        this.beianRepository = beianRepository;
    }


    /**
     * 查询给定根域名的备案信息,如果数据库中没有则去爬取
     *
     * @param rootDomain
     * @return
     * @throws IOException
     */
    public Beian getFromRootDomain(String rootDomain) throws IOException {
        Beian beian = beianRepository.findByDomain(rootDomain);
        if (beian != null) {
            return beian;
        }

        for (int i = 0; i < 20; i++) {
            log.info("开始第{}次抓取域名:{} 备案信息", i, rootDomain);
            Document document = Jsoup.connect("https://icp.chinaz.com/" + rootDomain)
                    .proxy("j379.kdltps.com", 15818)
                    .get();
            beian = new Beian();
            beian.setDomain(document.selectFirst("#keyword").val().strip());
            beian.setCompany(document.selectFirst("#companyName").text().strip());
            beian.setRegNo(document.selectFirst("#qiYeResult_table > tr:nth-child(2) > td:nth-child(6)").text().strip());
            beian.setUnifiedSocialCreditCode(document.selectFirst("#qiYeResult_table > tr:nth-child(3) > td:nth-child(4)").text().strip());
            beian.setNature(document.selectFirst("#first > li:nth-child(2) > p > strong").text().strip());
            beian.setCreateTime(LocalDateTime.now());
            beian.setUpdateTime(LocalDateTime.now());
            beian = beianRepository.save(beian);
            log.info("域名:{} 备案信息抓取成功!{}", rootDomain, JSON.toJSONString(beian));
            break;
        }
        if (beian == null) {
            log.error("域名:{} 备案信息抓取失败!请开发同学检查!", rootDomain);
        }
        return beian;
    }


}
