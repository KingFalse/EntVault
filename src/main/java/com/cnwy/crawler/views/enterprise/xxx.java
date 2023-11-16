package com.cnwy.crawler.views.enterprise;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class xxx {
    /*
     * 遍历所有行的所有列
     */
    public static void main(String[] args) throws IOException {
        Workbook wb = new XSSFWorkbook(Files.newInputStream(Path.of("/Users/doll/Downloads/【企查查】查企业-高级搜索(1116_104024986).xlsx")));
        Sheet sheet = wb.getSheetAt(0);
        Row row;
        Cell cell;
        String title = "";
        // 遍历所有行
        for (short i = 2; i < sheet.getLastRowNum()+1; i++) {
            row = sheet.getRow(i);
            Hyperlink hyperlink = row.getCell(0).getHyperlink();
            System.err.println(hyperlink.getAddress());
            // 第一行作为标题
            if (i == 0) {
                title = "";
                for (short j = 0; j < row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    if (cell != null) {
                        title += cell.getStringCellValue() + "\t";
                    } else {
                        title += "\t";
                    }
                }
                System.out.println(title);
            } else {
                // 其它行
                String str = "";
                for (short j = 0; j < row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    if (cell != null) {
                        str += cell.getStringCellValue() + "\t";
                    } else {
                        str += "\t";
                    }
                }
                System.out.println(str);
            }
        }
        wb.close();
    }

}
