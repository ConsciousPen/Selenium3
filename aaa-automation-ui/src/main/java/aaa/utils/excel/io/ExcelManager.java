package aaa.utils.excel.io;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelCell;
import aaa.utils.excel.io.entity.area.sheet.ExcelSheet;
import toolkit.exceptions.IstfException;

public class ExcelManager {
	protected static Logger log = LoggerFactory.getLogger(ExcelManager.class);

	private boolean isOpened;
	private File file;
	private Set<CellType<?>> allowableCellTypes;
	private Workbook workbook;
	private Map<Integer, ExcelSheet> sheets;

	public ExcelManager(File file, CellType<?>... allowableCellTypes) {
		this.isOpened = false;
		this.file = file;
		this.allowableCellTypes = ArrayUtils.isNotEmpty(allowableCellTypes) ? new HashSet<>(Arrays.asList(allowableCellTypes)) : ExcelCell.getBaseTypes();
	}

	public boolean isOpened() {
		return this.isOpened;
	}

	public File getFile() {
		return this.file;
	}

	public Set<CellType<?>> getCellTypes() {
		return new HashSet<>(this.allowableCellTypes);
	}

	public List<ExcelSheet> getSheets() {
		return new ArrayList<>(getSheetsMap().values());
	}

	public List<String> getSheetsNames() {
		return getSheets().stream().map(ExcelSheet::getSheetName).collect(Collectors.toList());
	}

	public List<Integer> getSheetsIndexes() {
		return new ArrayList<>(getSheetsMap().keySet());
	}

	public int getSheetsNumber() {
		return getSheetsMap().size();
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
		assertThat(hasSheet(sheetIndex)).as("There is no sheet with %1$s number in \"%2$s\" file", sheetIndex, getFile()).isTrue();
		return getSheetsMap().get(sheetIndex);
	}

	public ExcelSheet getSheet(String sheetName) {
		assertThat(hasSheet(sheetName)).as("There is no sheet with \"%1$s\" name in \"%2$s\" file", sheetName, getFile()).isTrue();
		return getSheets().stream().filter(s -> s.getSheetName().equals(sheetName)).findFirst().get();
	}

	public ExcelManager registerCellType(CellType<?>... cellTypes) {
		this.allowableCellTypes.addAll(Arrays.asList(cellTypes));
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

	@SuppressWarnings("resource")
	private Map<Integer, ExcelSheet> getSheetsMap() {
		if (this.sheets == null) {
			this.sheets = new LinkedHashMap<>();
			for (Sheet sheet : getWorkbook()) {
				int sheetNumber = getWorkbook().getSheetIndex(sheet.getSheetName()) + 1;
				this.sheets.put(sheetNumber, new ExcelSheet(sheet, sheetNumber, this, getCellTypes()));
			}
		}
		return this.sheets;
	}
}
