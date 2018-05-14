package aaa.utils.excel.io;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelCell;
import aaa.utils.excel.io.entity.area.sheet.ExcelSheet;
import toolkit.exceptions.IstfException;

/**
 * Convenient utility to manipulate with Excel files built on top of Apache POI's library.
 * Commonly used for getting/editing cell values within excel tables but besides that has variety useful features
 */
public class ExcelManager {
	protected static Logger log = LoggerFactory.getLogger(ExcelManager.class);

	private final File file;
	private boolean isOpened;
	private List<CellType<?>> allowableCellTypes;
	private Workbook workbook;
	private List<ExcelSheet> sheets;
	private FormulaEvaluator evaluator;

	public ExcelManager(File file) {
		this(file, ExcelCell.getBaseTypes());
	}

	public ExcelManager(File file, List<CellType<?>> allowableCellTypes) {
		this.isOpened = false;
		this.file = file;
		this.allowableCellTypes = allowableCellTypes.stream().distinct().collect(Collectors.toList());
	}

	public boolean isOpened() {
		return this.isOpened;
	}

	public File getFile() {
		return this.file;
	}

	public List<CellType<?>> getCellTypes() {
		return Collections.unmodifiableList(this.allowableCellTypes);
	}

	@SuppressWarnings("resource")
	public List<ExcelSheet> getSheets() {
		if (this.sheets == null) {
			this.sheets = new ArrayList<>(getWorkbook().getNumberOfSheets());
			for (Sheet sheet : getWorkbook()) {
				int sheetNumber = getWorkbook().getSheetIndex(sheet.getSheetName()) + 1;
				this.sheets.add(new ExcelSheet(sheet, sheetNumber, this, getCellTypes()));
			}
		}
		return Collections.unmodifiableList(this.sheets);
	}

	public FormulaEvaluator getFormulaEvaluator() {
		if (this.evaluator == null) {
			this.evaluator = getWorkbook().getCreationHelper().createFormulaEvaluator();
		}
		return evaluator;
	}

	public List<String> getSheetsNames() {
		return getSheets().stream().map(ExcelSheet::getSheetName).collect(Collectors.toList());
	}

	public List<Integer> getSheetsIndexes() {
		return getSheets().stream().map(ExcelSheet::getSheetIndex).collect(Collectors.toList());
	}

	public int getSheetsNumber() {
		return getSheets().size();
	}

	public Workbook getWorkbook() {
		if (!isOpened()) {
			assertThat(file).as("File \"%s\" does not exist", file.getAbsolutePath()).exists();
			String errorMessage = "Workbook creation from has been failed while opening file " + file.getAbsolutePath() + ". %s";
			try (InputStream targetStream = new FileInputStream(file)) {
				try {
					this.workbook = WorkbookFactory.create(targetStream);
				} catch (IOException | InvalidFormatException e) {
					throw new IstfException(String.format(errorMessage, e));
				}
			} catch (IOException e) {
				throw new IstfException(String.format(errorMessage, e));
			}
			this.isOpened = true;
		}
		return this.workbook;
	}

	@Override
	public String toString() {
		return "ExcelManager{" +
				"isOpened=" + isOpened() +
				", file=" + getFile() +
				", sheetsNumner=" + getSheetsNumber() +
				", sheetsNames=" + getSheetsNames() +
				", allowableCellTypes=" + getCellTypes() +
				'}';
	}

	public boolean hasSheet(String sheetName) {
		return getSheetsNames().contains(sheetName);
	}

	public boolean hasSheet(int sheetIndex) {
		return getSheetsIndexes().contains(sheetIndex);
	}

	public ExcelSheet getSheet(int sheetIndex) {
		for (ExcelSheet sheet : getSheets()) {
			if (sheet.getSheetIndex() == sheetIndex) {
				return sheet;
			}
		}
		throw new IstfException(String.format("There is no sheet with %1$s index in \"%2$s\" file", sheetIndex, getFile()));
	}

	public ExcelSheet getSheet(String sheetName) {
		for (ExcelSheet sheet : getSheets()) {
			if (sheet.getSheetName().equals(sheetName)) {
				return sheet;
			}
		}
		throw new IstfException(String.format("There is no sheet with \"%1$s\" name in \"%2$s\" file", sheetName, getFile()));
	}

	public ExcelManager registerCellType(List<CellType<?>> cellTypes) {
		this.allowableCellTypes.addAll(cellTypes);
		this.allowableCellTypes = this.allowableCellTypes.stream().distinct().collect(Collectors.toList());
		getSheets().forEach(s -> s.registerCellType(cellTypes));
		return this;
	}

	public ExcelManager save() {
		return save(getFile());
	}

	@SuppressWarnings("resource")
	public ExcelManager save(File destinationFile) {
		assertThat(file).as("File \"%s\" does not exist", destinationFile.getAbsolutePath()).exists();
		Workbook wb = getWorkbook();
		try (FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
			wb.write(outputStream);
		} catch (IOException e) {
			close();
			throw new IstfException(String.format("Writing to excel file \"%s\" has been failed", destinationFile.getAbsolutePath()), e);
		}
		return this;
	}

	@SuppressWarnings("resource")
	public ExcelManager close() {
		if (!isOpened()) {
			log.warn("Excel workbook on \"{}\" file is already closed", getFile());
			return this;
		}
		try {
			getWorkbook().close();
			this.isOpened = false;
		} catch (IOException e) {
			throw new IstfException(String.format("Closing of excel workbook in \"%s\" file has been failed", getFile()), e);
		}
		return this;
	}

	public ExcelManager saveAndClose() {
		return saveAndClose(getFile());
	}

	public ExcelManager saveAndClose(File destinationFile) {
		save(destinationFile);
		return close();
	}
}
