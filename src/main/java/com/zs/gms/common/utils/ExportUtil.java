package com.zs.gms.common.utils;

import com.zs.gms.common.entity.Export;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ExportUtil {

    public static void export(HttpServletResponse response,Export exportObj){
        if(!validate(exportObj)){
            return;
        }
       switch (exportObj.getType()){
           case xls:
               smlExport(response,exportObj);
               break;
           case xlsx:
               bigExport(response,exportObj);
               break;
       }
    }

    private static void smlExport(HttpServletResponse response,Export exportObj){
        HSSFWorkbook workbook = createBook(exportObj);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + exportObj.getFileName());
        try {
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出excel文件异常",e);
        }
    }

    private static void  bigExport(HttpServletResponse response,Export exportObj){
        SXSSFWorkbook bigBook = createBigBook(exportObj);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + exportObj.getFileName());
        try {
            bigBook.write(response.getOutputStream());
            response.flushBuffer();
            bigBook.dispose();
        } catch (Exception e) {
            log.error("导出excel文件异常",e);
        }
    }

    private static HSSFWorkbook createBook(Export exportObj){
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(exportObj.getSheetName());
        int line=0;
        HSSFRow row = sheet.createRow(line);
        List<String>  values = new ArrayList<>(exportObj.getHeaders().values());
        Set<String> keys = exportObj.getHeaders().keySet();
        for (int i = 0; i < values.size(); i++) {
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(values.get(i));
            cell.setCellValue(text);
        }
        line++;
        for (Map<String, Object> objectMap : exportObj.getExportData()) {
            int index=0;
            HSSFRow dataRow = sheet.createRow(line);
            for (String key : keys) {
                HSSFCell cell = dataRow.createCell(index);
                String value="";
                if(objectMap.containsKey(key)){
                    value=String.valueOf(objectMap.get(key));
                }
                cell.setCellValue(value);
                index++;
            }
            line++;
        }
        return workbook;
    }

    private static SXSSFWorkbook createBigBook(Export exportObj){
        SXSSFWorkbook workbook = new SXSSFWorkbook (100);
        int sheetNum=0;
        int pageSize=100000;
        int size = exportObj.getExportData().size();
        for (int j = 0; j <Math.ceil(size / (double)pageSize); j++) {
            workbook.createSheet(exportObj.getSheetName()+sheetNum);
            SXSSFSheet sheet = workbook.getSheetAt(sheetNum);
            int line=0;
            SXSSFRow row = sheet.createRow(line);
            List<String>  values = new ArrayList<>(exportObj.getHeaders().values());
            Set<String> keys = exportObj.getHeaders().keySet();
            for (int i = 0; i < values.size(); i++) {
                SXSSFCell cell = row.createCell(i);
                cell.setCellValue(values.get(i));
            }
            line++;
            List<Map<String, Object>> subList;
            if((sheetNum + 1) * pageSize>size){
                subList = exportObj.getExportData().subList(sheetNum * pageSize,size);
            }else{
                subList = exportObj.getExportData().subList(sheetNum * pageSize, (sheetNum + 1) * pageSize);
            }
            for (Map<String, Object> objectMap : subList) {
                int index=0;
                SXSSFRow dataRow = sheet.createRow(line);
                for (String key : keys) {
                    SXSSFCell cell = dataRow.createCell(index);
                    String value="";
                    if(objectMap.containsKey(key)){
                        value=String.valueOf(objectMap.get(key));
                    }
                    cell.setCellValue(value);
                    index++;
                }
                line++;
            }
            sheetNum++;
        }
        return workbook;
    }

    private static boolean validate(Export exportObj){
        if(PropertyUtil.isAnyFiledNull(exportObj)){
            log.error("导出实体校验失败，存在空字段");
            return false;
        }
        return true;
    }
}
