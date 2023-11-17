package com.cnwy.crawler.services;

import com.alibaba.fastjson2.JSON;
import com.cnwy.crawler.data.BeianRepository;
import com.cnwy.crawler.data.Enterprise;
import com.cnwy.crawler.data.EnterpriseRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ImportService {

    private final BeianRepository beianRepository;

    final
    EnterpriseRepository enterpriseRepository;

    public ImportService(BeianRepository beianRepository, EnterpriseRepository enterpriseRepository) {
        this.beianRepository = beianRepository;
        this.enterpriseRepository = enterpriseRepository;
    }

    public void importExcel(InputStream inputStream) {
        try {
            Workbook wb = new XSSFWorkbook(inputStream);
            Sheet sheet = wb.getSheetAt(0);
            Row row;
            Cell cell;
            String title = "";
            List<Enterprise> es = new ArrayList<>();
            // 遍历所有行
            for (short i = 2; i < sheet.getLastRowNum() + 1; i++) {
                row = sheet.getRow(i);
                Hyperlink hyperlink = row.getCell(0).getHyperlink();
                String qccID = hyperlink.getAddress().split(".html")[0].replace("https://www.qcc.com/firm/", "");

                Enterprise enterprise = null;
                Optional<Enterprise> byQccID = enterpriseRepository.findByQccID(qccID);
                if (byQccID.isPresent()) {
                    enterprise = byQccID.get();
                    enterprise.setUpdateTime(LocalDateTime.now());
                }else {
                    log.info("发现一条新数据:"+qccID);
                    enterprise = new Enterprise();
                    enterprise.setCreateTime(LocalDateTime.now());
                    enterprise.setUpdateTime(LocalDateTime.now());
                }


                String name = row.getCell(0).getStringCellValue();
                enterprise.setName(name);
                enterprise.setDescription(row.getCell(32).getStringCellValue().strip());
                enterprise.setWebsite(row.getCell(30).getStringCellValue().strip());
                enterprise.setTelephone(row.getCell(7).getStringCellValue().strip() + "," + row.getCell(8).getStringCellValue().strip().replace(";", ""));
                enterprise.setStatus(row.getCell(1).getStringCellValue().strip());
                enterprise.setUnifiedSocialCreditCode(row.getCell(5).getStringCellValue().strip());
                enterprise.setRegNo(row.getCell(16).getStringCellValue().strip());
                enterprise.setEmail(row.getCell(9).getStringCellValue().strip() + "," + row.getCell(10).getStringCellValue().strip().replace(";", ""));
                enterprise.setOrgType(row.getCell(20).getStringCellValue().strip());
                enterprise.setProvince(row.getCell(11).getStringCellValue().strip());
                enterprise.setCity(row.getCell(12).getStringCellValue().strip());
                enterprise.setDistrict(row.getCell(13).getStringCellValue().strip());
                enterprise.setAddress(row.getCell(6).getStringCellValue().strip());
                enterprise.setHistoricalNames(row.getCell(28).getStringCellValue().strip());
                enterprise.setQccID(qccID);
                enterprise.setHitReason("企查查搜索分类:机关单位");
                enterprise.setHitPlatform("企查查");
                log.info("excel读取一条:"+JSON.toJSONString(enterprise));
                es.add(enterprise);
            }
            wb.close();

            enterpriseRepository.saveAll(es);
        } catch (Exception e) {
            log.error("导入excel文件 {}, 出现异常!", e);
        }

    }

}
