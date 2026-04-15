package commonutils;

import java.io.*;
import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelRead {
	public static String path;
	public static FileInputStream fis = null;
	public static FileOutputStream fileOut = null;
	private static XSSFWorkbook workbook = null;
	private static XSSFSheet sheet = null;
	private static XSSFRow row = null;
	private static XSSFCell cell = null;
	static DataFormatter formatter = new DataFormatter(); // used in formatting cell data to Normal data types

	/**
	 * Reads a cell value as String from the specified Excel file, sheet, row, and column.
	 *
	 * @param xlPath     Path to the Excel file.
	 * @param sheetName  Name of the sheet.
	 * @param rowIndex   Row index (0-based).
	 * @param cellIndex  Column index (0-based).
	 * @return Cell value as String, or empty string if cell is empty or not found.
	 */
	public static String readExcelFile(String xlPath, String sheetName, int rowIndex, int cellIndex) {
		String value = "";

		try (FileInputStream fis = new FileInputStream(xlPath);
			 Workbook workbook = WorkbookFactory.create(fis)) {

			Sheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				System.err.println("Sheet '" + sheetName + "' not found in file: " + xlPath);
				return "";
			}

			Row row = sheet.getRow(rowIndex);
			if (row == null) {
				System.err.println("Row " + rowIndex + " not found in sheet: " + sheetName);
				return "";
			}

			Cell cell = row.getCell(cellIndex);
			if (cell == null) {
				System.err.println("Cell " + cellIndex + " not found in row: " + rowIndex);
				return "";
			}

			// Read and return cell value as String (regardless of type)
			value = cell.toString().trim();

		} catch (IOException e) {
			System.err.println("Error reading Excel file: " + e.getMessage());
			e.printStackTrace();
		}

		return value;
	}


	/**
	 * Get cell data as String from specified sheet, column, and row.
	 *
	 * @param xlPath    Path to the Excel file.
	 * @param sheetName Name of the sheet.
	 * @param colNum    Column number (0-based index).
	 * @param rowNum    Row number (1-based index, as per Excel standard for users).
	 * @return Cell value as String, or empty string if not found.
	 */
	public static String getCellData(String xlPath, String sheetName, int colNum, int rowNum) {
		if (rowNum <= 0) {
			System.err.println("Row number must be greater than 0.");
			return "";
		}

		try (FileInputStream fileInputStream = new FileInputStream(new File(xlPath));
			 Workbook workbook = new XSSFWorkbook(fileInputStream)) {

			Sheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				System.err.println("Sheet '" + sheetName + "' not found.");
				return "";
			}

			Row row = sheet.getRow(rowNum - 1); // Adjusting for 0-based index
			if (row == null) {
				System.err.println("Row " + rowNum + " not found.");
				return "";
			}

			Cell cell = row.getCell(colNum);
			if (cell == null) {
				System.err.println("Cell at column " + colNum + " is empty.");
				return "";
			}

			switch (cell.getCellType()) {
				case STRING:
					return cell.getStringCellValue();
				case NUMERIC:
					return String.valueOf(cell.getNumericCellValue());
				case BOOLEAN:
					return String.valueOf(cell.getBooleanCellValue());
				case FORMULA:
					return cell.getCellFormula();
				default:
					return "";
			}

		} catch (IOException e) {
			System.err.println("Failed to read cell data: " + e.getMessage());
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Deletes a specific row from the given sheet.
	 *
	 * @param xlPath    Path to the Excel file.
	 * @param sheetName Name of the sheet.
	 * @param rowNo     Row number to be deleted (0-based index).
	 * @return true if deletion is successful, false otherwise.
	 * @throws IOException if reading or writing file fails.
	 */
	public static boolean deleteRow(String xlPath, String sheetName, int rowNo) throws IOException {
		boolean isDeleted = false;

		try (FileInputStream fis = new FileInputStream(xlPath);
			 XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			Sheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				System.err.println("Sheet '" + sheetName + "' not found.");
				return false;
			}

			int lastRowNum = sheet.getLastRowNum();

			if (rowNo >= 0 && rowNo < lastRowNum) {
				// Shift rows up to delete
				sheet.shiftRows(rowNo + 1, lastRowNum, -1);
				isDeleted = true;
			} else if (rowNo == lastRowNum) {
				// If it's the last row, simply remove it
				Row removingRow = sheet.getRow(rowNo);
				if (removingRow != null) {
					sheet.removeRow(removingRow);
					isDeleted = true;
				}
			} else {
				System.err.println("Invalid row number: " + rowNo + ". Last row index: " + lastRowNum);
				return false;
			}

			// Save changes back to the file
			try (FileOutputStream outFile = new FileOutputStream(xlPath)) {
				workbook.write(outFile);
			}

			if (isDeleted) {
				System.out.println("Row " + rowNo + " successfully deleted from sheet '" + sheetName + "'.");
			}

		} catch (Exception e) {
			System.err.println("Failed to delete row: " + e.getMessage());
			throw e; // rethrow to calling method
		}

		return isDeleted;
	}

	// Get row count
    public static int getRowCount(String sheetName) {
        int index = workbook.getSheetIndex(sheetName);
        if (index == -1)
            return 0;
        else {
            sheet = workbook.getSheetAt(index);
            int number = sheet.getLastRowNum() + 1;
            return number;
        }

    }
    public static int getRowCount(String xlPath,String sheetName) {

        try (FileInputStream fis = new FileInputStream(xlPath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = null;
            int index = workbook.getSheetIndex(sheetName);
            if (index == -1)
                return 0;
            else {
                sheet = workbook.getSheetAt(index);
                int number = sheet.getLastRowNum() + 1;
                return number;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
	 * Reads all rows and columns from the specified Excel sheet and returns the data as a 2D Object array.
	 *
	 * @param xlPath    The file path of the Excel workbook.
	 * @param sheetName The name of the sheet to read data from.
	 * @return A 2D Object array containing the data from the sheet.
	 * @throws IOException If the file is not found or cannot be read.
	 */
	public static Object[][] getPayload(String xlPath, String sheetName) throws IOException {
		DataFormatter formatter = new DataFormatter();

		try (FileInputStream fis = new FileInputStream(xlPath);
			 XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			Sheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in " + xlPath);
			}

			int totalRows = sheet.getPhysicalNumberOfRows();
			if (totalRows < 2) { // No data rows present
				throw new IllegalArgumentException("Sheet '" + sheetName + "' contains no data.");
			}

			Row headerRow = sheet.getRow(0);
			int totalColumns = headerRow.getLastCellNum();

			Object[][] payload = new Object[totalRows - 1][totalColumns]; // Exclude header row

			// Iterate over each data row (skip header row)
			for (int i = 1; i < totalRows; i++) {
				Row currentRow = sheet.getRow(i);
				for (int j = 0; j < totalColumns; j++) {
					if (currentRow == null) {
						payload[i - 1][j] = ""; // Empty row
					} else {
						Cell cell = currentRow.getCell(j);
						payload[i - 1][j] = (cell == null) ? "" : formatter.formatCellValue(cell);
					}
				}
			}

			return payload;
		}
	}

	// Utility method to fetch all rows as Hashtable<String, String> from Excel
	public static Object[][] getPayloadAsHashTable(String excelPath, String sheetName) throws IOException {
		try (FileInputStream fis = new FileInputStream(excelPath);
			 XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			XSSFSheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in Excel file: " + excelPath);
			}

			int totalRows = sheet.getLastRowNum();
			int totalColumns = sheet.getRow(0).getLastCellNum();

			Object[][] payload = new Object[totalRows][1];

			List<String> headers = new ArrayList<>();
			for (int col = 0; col < totalColumns; col++) {
				headers.add(formatter.formatCellValue(sheet.getRow(0).getCell(col)));
			}

			for (int rowIndex = 1; rowIndex <= totalRows; rowIndex++) {
				Hashtable<String, String> rowData = new Hashtable<>();
				XSSFRow row = sheet.getRow(rowIndex);

				for (int colIndex = 0; colIndex < totalColumns; colIndex++) {
					String cellValue = (row.getCell(colIndex) != null) ?
							formatter.formatCellValue(row.getCell(colIndex)) : "";
					rowData.put(headers.get(colIndex), cellValue);
				}

				payload[rowIndex - 1][0] = rowData;
			}

			return payload;

		} catch (IOException e) {
			throw new IOException("Failed to read Excel file at path: " + excelPath, e);
		}
	}

	/**
	 * Reads all data from a specific column in an Excel sheet.
	 *
	 * @param filePath  Full path to the Excel file (e.g., "src/test/resources/testdata/data.xlsx").
	 * @param sheetName Name of the sheet (e.g., "scid_data").
	 * @param columnIndex Zero-based column index to read data from.
	 * @return List<String> containing all non-empty values from the specified column.
	 */
	public static List<String> getCellData(String filePath, String sheetName, int columnIndex) {
		List<String> columnData = new ArrayList<>();
		FileInputStream fis = null;
		Workbook workbook = null;

		try {
			fis = new FileInputStream(filePath);
			workbook = new XSSFWorkbook(fis);
			Sheet sheet = workbook.getSheet(sheetName);

			if (sheet == null) {
				throw new RuntimeException("Sheet " + sheetName + " not found in " + filePath);
			}

			Iterator<Row> rowIterator = sheet.iterator();

			if (rowIterator.hasNext()) {
				rowIterator.next();
			}

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Cell cell = row.getCell(columnIndex);

				if (cell != null) {
					cell.setCellType(CellType.STRING); // Ensure data is read as String
					String cellValue = cell.getStringCellValue().trim();

					// Add non-empty values
					if (!cellValue.isEmpty()) {
						columnData.add(cellValue);
					}
				}
			}

		} catch (IOException e) {
			throw new RuntimeException("Failed to read Excel file: " + filePath, e);
		} finally {
			try {
				if (workbook != null) workbook.close();
				if (fis != null) fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return columnData;
	}
}