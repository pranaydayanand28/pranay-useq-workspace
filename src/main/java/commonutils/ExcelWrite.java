package commonutils;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Utility class for writing data into an Excel file.
 */
public class ExcelWrite {

    /**
     * Writes the given value into a specific cell of the Excel file.
     *
     * @param xlPath    Path to the Excel file.
     * @param sheetName Name of the sheet where data will be written.
     * @param rowIndex  Row index (0-based).
     * @param cellIndex Column index (0-based).
     * @param value     Value to write into the cell.
     * @throws IOException                   If file operations fail.
     * @throws EncryptedDocumentException    If the document is encrypted.
     */
    public static void writeExcelFile(String xlPath, String sheetName, int rowIndex, int cellIndex, String value)
            throws IOException, EncryptedDocumentException {

        // Load workbook and sheet using try-with-resources for safe resource management
        try (FileInputStream fis = new FileInputStream(xlPath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in file: " + xlPath);
            }

            // Get or create row
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }

            // Create or overwrite the cell value
            row.createCell(cellIndex).setCellValue(value);

            // Write back to file
            try (FileOutputStream output = new FileOutputStream(xlPath)) {
                workbook.write(output);
                System.out.println("Successfully written value to Excel at Sheet: '" + sheetName +
                        "', Row: " + rowIndex + ", Cell: " + cellIndex);
            }

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + xlPath);
            throw e;
        } catch (IOException | EncryptedDocumentException e) {
            System.err.println("Failed to write to Excel file: " + e.getMessage());
            throw e;
        }
    }
}