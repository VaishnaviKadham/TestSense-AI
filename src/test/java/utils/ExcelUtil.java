package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class ExcelUtil {

    private static String getFilePath() {
        return ConfigReader.get("excelPath");
    }

    // ✅ WRITE DATA TO EXCEL
    public static void writeData(String sheetName, int rowNum, int colNum, String value) {

        try {
            String path = getFilePath();

            System.out.println("Opening Excel file for writing: " + path);

            File file = new File(path);
            Workbook workbook;

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
            } else {
                workbook = new XSSFWorkbook();
            }

            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            }

            Row row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }

            Cell cell = row.getCell(colNum);
            if (cell == null) {
                cell = row.createCell(colNum);
            }

            System.out.println("Writing value: " + value);
            cell.setCellValue(value);

            FileOutputStream fos = new FileOutputStream(path);
            workbook.write(fos);

            workbook.close();
            fos.close();

            System.out.println("Data written successfully");

        } catch (Exception e) {
            System.out.println("Error writing Excel: " + e.getMessage());
        }
    }

    // ✅ READ SINGLE CELL DATA
    public static String readData(String sheetName, int rowNum, int colNum) {

        String value = "";

        try {
            String path = getFilePath();

            System.out.println("Opening Excel file for reading: " + path);

            FileInputStream fis = new FileInputStream(path);
            Workbook workbook = new XSSFWorkbook(fis);

            Sheet sheet = workbook.getSheet(sheetName);

            Row row = sheet.getRow(rowNum);
            Cell cell = row.getCell(colNum);

            value = cell.toString();

            workbook.close();

            System.out.println("Read value: " + value);

        } catch (Exception e) {
            System.out.println("Error reading Excel: " + e.getMessage());
        }

        return value;
    }

    // ✅ CONVERT EXCEL → 2D MATRIX
    public static String[][] getSheetData(String filePath, String sheetName) {

        String[][] data = null;

        try {
            System.out.println("Opening Excel file: " + filePath);

            FileInputStream fis = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(fis);

            Sheet sheet = workbook.getSheet(sheetName);

            int rowCount = sheet.getPhysicalNumberOfRows();
            int colCount = sheet.getRow(0).getPhysicalNumberOfCells();

            data = new String[rowCount][colCount];

            System.out.println("Converting Excel data into matrix");

            for (int i = 0; i < rowCount; i++) {

                Row row = sheet.getRow(i);

                for (int j = 0; j < colCount; j++) {

                    Cell cell = row.getCell(j);
                    data[i][j] = (cell == null) ? "" : cell.toString();

                    System.out.println("Data[" + i + "][" + j + "] = " + data[i][j]);
                }
            }

            workbook.close();

        } catch (Exception e) {
            System.out.println("Error reading Excel: " + e.getMessage());
        }

        return data;
    }
}