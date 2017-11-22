package aaa.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.utils.ExcelUtils;
import toolkit.exceptions.IstfException;

public class AAAExcelUtils extends ExcelUtils {
	protected static Logger log = LoggerFactory.getLogger(AAAExcelUtils.class);

	public static Row getHeaderRow(Sheet sheet, Set<String> headerColumnNames) {
		return getHeaderRow(sheet, headerColumnNames, true);
	}

	public static Row getHeaderRow(Sheet sheet, Set<String> headerColumnNames, boolean getLastOccurrence) {
		Map<String, String> headerQuery = new HashMap<>(headerColumnNames.size());
		headerColumnNames.forEach(s -> headerQuery.put(s, s));
		Row header = getRow(sheet, headerQuery);
		if (getLastOccurrence) {
			while (header.getRowNum() < sheet.getLastRowNum()) {
				Row fromRow = sheet.getRow(header.getRowNum() + 1);
				try {
					header = getRow(sheet, headerQuery);
				} catch (IstfException ignored) {
					break;
				}
			}

		}

		return header;
	}

	public static Map<String, String> getRowValues(Row row) {
		return getRowValues(row, null);
	}

	public static Map<String, String> getRowValues(Row row, Row headerRow) {
		if (row == null) {
			throw new IstfException("Unable to get values for null row");
		}
		return getRowValues(row, headerRow, row.getFirstCellNum());
	}

	public static Map<String, String> getRowValues(Row row, Row headerRow, int fromColumnNumber) {
		if (row == null) {
			throw new IstfException("Unable to get values for null row");
		}
		return getRowValues(row, headerRow, fromColumnNumber, row.getLastCellNum());
	}

	public static Map<String, String> getRowValues(Row row, int fromColumnNumber, int toColumnNumber) {
		return getRowValues(row, null, fromColumnNumber, toColumnNumber);
	}

	public static Map<String, String> getRowValues(Row row, Row headerRow, int fromColumnNumber, int toColumnNumber) {
		if (row == null) {
			throw new IstfException("Unable to get values for null row");
		}

		Map<String, String> rowValues = new HashMap<>();
		Sheet sheet = row.getSheet();

		if (headerRow == null) {
			headerRow = getFirstRow(sheet);
		}

		for (int cNumber = fromColumnNumber; cNumber <= toColumnNumber; cNumber++) {
			String columnHeader = getCellValue(headerRow, cNumber);
			if (columnHeader.isEmpty()) {
				log.warn(String.format("Cell value is empty in header row #%1$s and column number #%2$s.", headerRow.getRowNum(), cNumber));
			}

			if (rowValues.containsKey(columnHeader)) {
				log.warn(String.format("Cell value \"%1$s\" in header row #%2$s and column number #%3$s is not unique. Old cell value for this header will be overwritten.", columnHeader, headerRow
						.getRowNum(), cNumber));
			}
			rowValues.put(columnHeader, getCellValue(row, cNumber));
		}

		return rowValues;
	}
}
