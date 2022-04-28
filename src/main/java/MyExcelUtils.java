import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class MyExcelUtils {
    XSSFWorkbook workbook;
    XSSFSheet sheet;
    String excelPath;
    String sheetName;

    public MyExcelUtils(String excelPath, String sheetName) {
        this.excelPath = excelPath;
        this.sheetName = sheetName;
        try {
            workbook = new XSSFWorkbook(excelPath);
            sheet = workbook.getSheet(sheetName);
        } catch (Exception exp) {
            System.out.println(exp.getCause().toString());
            System.out.println(exp.getMessage());
            exp.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Your exel: " + excelPath + " sheet: " + sheetName;
    }

    public String getCellData(int rowNum, int colNum) throws IOException {
        DataFormatter formatter = new DataFormatter(Locale.ENGLISH);
        Object value = formatter.formatCellValue(sheet.getRow(rowNum).getCell(colNum));
        return value.toString();
    }

    public String getValue(int rowNum, int colNum) throws IOException {
        return sheet.getRow(rowNum).getCell(colNum).toString();
    }
    public void setData(int rowNum, int cellNumStart, String[] tabData) { //W jednym wierszu
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        data.put("0", Arrays.stream(tabData).toArray());
        Set<String> keyset = data.keySet();
        for (String key : keyset) {
            Row row = sheet.getRow(rowNum);
            if (row == null)
                row = sheet.createRow(rowNum++);
            Object[] objArr = data.get(key);
            int cellnum = cellNumStart;
            for (Object obj : objArr) {
                Cell cell = row.getCell(cellnum++, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellValue(obj.toString());
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(new File("./data/ManhattanDistance.xlsx"));
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setData(int rowNum, int cellNum, double data, String excelName) { //Pojedyncza dana
        Row row = sheet.getRow(rowNum);
        if (row == null)
            row = sheet.createRow(rowNum);
        Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cell.setCellValue(data);

        try {
            FileOutputStream out = new FileOutputStream(new File("./data/"+ excelName +".xlsx"));
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}