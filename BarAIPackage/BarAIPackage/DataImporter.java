package BarAIPackage;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.*;



public class DataImporter {

	  public static FileData loadExcelFile(String filePath) {
		  	System.out.println("Loading Data from " + filePath + "...");
		  	
	        List<DataRow> rowDataList = new ArrayList<>();

	        try (FileInputStream fis = new FileInputStream(new File(filePath));
	             Workbook workbook = WorkbookFactory.create(fis)) {

	            Sheet sheet = workbook.getSheetAt(0); // use the first sheet

	            boolean firstRow = true; // skip header row
	            for (Row row : sheet) {
	                if (firstRow) {
	                    firstRow = false;
	                    continue;
	                }

	                List<String> rowValues = new ArrayList<>();
	                for (int c = 0; c < row.getLastCellNum(); c++) {
	                    Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
	                    if (cell == null) {
	                        rowValues.add("");
	                    } else {
	                        switch (cell.getCellType()) {
	                            case STRING:
	                                rowValues.add(cell.getStringCellValue());
	                                break;
	                            case NUMERIC:
	                                if (DateUtil.isCellDateFormatted(cell)) {
	                                    // If it's a date, add as ISO string
	                                    if (cell.getLocalDateTimeCellValue() != null) {
	                                        rowValues.add(cell.getLocalDateTimeCellValue().toString());
	                                    } else {
	                                        rowValues.add(cell.getDateCellValue().toString());
	                                    }
	                                } else {
	                                    rowValues.add(String.valueOf(cell.getNumericCellValue()));
	                                }
	                                break;
	                            case BOOLEAN:
	                                rowValues.add(String.valueOf(cell.getBooleanCellValue()));
	                                break;
	                            case FORMULA:
	                                rowValues.add(cell.getCellFormula());
	                                break;
	                            default:
	                                rowValues.add("");
	                        }
	                    }
	                }

	                DataRow dataRow = new DataRow(rowValues);
	                rowDataList.add(dataRow);
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return new FileData(filePath, rowDataList, ""); // Description can be set later
	    }
  
}
