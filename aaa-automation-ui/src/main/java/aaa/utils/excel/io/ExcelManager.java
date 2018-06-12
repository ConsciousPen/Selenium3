package aaa.utils.excel.io;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.EmptyFileException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;
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
public class ExcelManager implements Closeable {
	protected static Logger log = LoggerFactory.getLogger(ExcelManager.class);
	
	private final File sourceFile;
	private Workbook workbook;
	private boolean isOpened;
	private List<CellType<?>> allowableCellTypes;
	private List<ExcelSheet> sheets;
	private FormulaEvaluator evaluator;
	
	public ExcelManager(File sourceFile) {
		this(sourceFile, ExcelCell.getBaseTypes());
	}
	
	public ExcelManager(InputStream inputStream) {
		this(inputStream, ExcelCell.getBaseTypes());
	}
	
	public ExcelManager(File sourceFile, List<CellType<?>> allowableCellTypes) {
		this.sourceFile = sourceFile;
		this.workbook = getWorkbook(sourceFile);
		this.isOpened = true;
		this.allowableCellTypes = allowableCellTypes.stream().distinct().collect(Collectors.toList());
	}
	
	public ExcelManager(InputStream inputStream, List<CellType<?>> allowableCellTypes) {
		this.sourceFile = null;
		this.workbook = getWorkbook(inputStream);
		IOUtils.closeQuietly(inputStream);
		this.isOpened = true;
		this.allowableCellTypes = allowableCellTypes.stream().distinct().collect(Collectors.toList());
	}
	
	public boolean isOpened() {
		return this.isOpened;
	}
	
	public File getSourceFile() {
		return this.sourceFile;
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
			if (!initializedFromFile()) {
				throw new IstfException("Unable to reopen workbook which was initialized with InputStream and closed");
			}
			this.workbook = getWorkbook(getSourceFile());
			this.isOpened = true;
		}
		return this.workbook;
	}
	
	@Override
	public String toString() {
		return "ExcelManager{" +
				"isOpened=" + isOpened() +
				", initializedFrom=" + getWorkbookInitializationSource() +
				", sheetsNumner=" + getSheetsNumber() +
				", sheetsNames=" + getSheetsNames() +
				", allowableCellTypes=" + getCellTypes() +
				'}';
	}
	
	@Override
	public void close() {
		if (isOpened()) {
			try {
				getWorkbook().close();
				this.isOpened = false;
			} catch (IOException e) {
				throw new IstfException(String.format("Closing of excel workbook initialized from %s has been failed", getWorkbookInitializationSource()), e);
			}
		}
	}
	
	public boolean initializedFromFile() {
		return this.sourceFile != null;
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
		throw new IstfException(String.format("There is no sheet with %s index", sheetIndex));
	}
	
	public ExcelSheet getSheet(String sheetName) {
		for (ExcelSheet sheet : getSheets()) {
			if (sheet.getSheetName().equals(sheetName)) {
				return sheet;
			}
		}
		throw new IstfException(String.format("There is no sheet with \"%s\" name", sheetName));
	}
	
	public ExcelSheet getSheetContains(String sheetNamePattern) {
		for (ExcelSheet sheet : getSheets()) {
			if (sheet.getSheetName().contains(sheetNamePattern)) {
				return sheet;
			}
		}
		throw new IstfException(String.format("There is no sheet which contains \"%s\" name", sheetNamePattern));
	}
	
	public ExcelManager registerCellType(List<CellType<?>> cellTypes) {
		this.allowableCellTypes.addAll(cellTypes);
		this.allowableCellTypes = this.allowableCellTypes.stream().distinct().collect(Collectors.toList());
		getSheets().forEach(s -> s.registerCellType(cellTypes));
		return this;
	}
	
	public ExcelManager save() {
		if (!initializedFromFile()) {
			log.warn("ExcelManager was initialized from InputStream therefore source file does not exist and saving to it has been skipped");
			return this;
		}
		return save(getSourceFile());
	}
	
	@SuppressWarnings("resource")
	public ExcelManager save(File destinationFile) {
		File writeToFile;
		boolean overwriteFile = false;
		
		if (initializedFromFile() && getSourceFile().equals(destinationFile)) {
			int dotIndex = destinationFile.getAbsolutePath().lastIndexOf('.');
			writeToFile = new File(destinationFile.getAbsolutePath().substring(0, dotIndex) + "_TEMP" + destinationFile.getAbsolutePath().substring(dotIndex));
			overwriteFile = true;
		} else {
			writeToFile = destinationFile;
		}
		
		Workbook wb = getWorkbook();
		try (FileOutputStream outputStream = new FileOutputStream(writeToFile)) {
			wb.write(outputStream);
		} catch (IOException e) {
			close();
			throw new IstfException(String.format("Writing to excel file \"%s\" has been failed", writeToFile.getAbsolutePath()), e);
		}
		
		if (overwriteFile) {
			close();
			if (!destinationFile.delete()) {
				log.warn("Can't delede original file \"{}\"", destinationFile.getAbsolutePath());
			}
			if (!writeToFile.renameTo(destinationFile)) {
				log.warn("Can't rename temp file \"{}\" to \"{}\"", writeToFile.getAbsolutePath(), destinationFile.getAbsolutePath());
			}
		}
		
		return this;
	}
	
	public ExcelManager saveAndClose() {
		if (!initializedFromFile()) {
			log.warn("ExcelManager was initialized from InputStream therefore source file does not exist and saving to it has been skipped");
			return this;
		}
		return saveAndClose(getSourceFile());
	}
	
	public ExcelManager saveAndClose(File destinationFile) {
		save(destinationFile);
		if (isOpened()) {
			close();
		}
		return this;
	}
	
	private Workbook getWorkbook(InputStream inputStream) {
		try {
			return WorkbookFactory.create(inputStream);
		} catch (EmptyFileException | InvalidFormatException | IOException e) {
			throw new IstfException("Workbook creation from InputStream has been failed.", e);
		}
	}
	
	private Workbook getWorkbook(File file) {
		try {
			return WorkbookFactory.create(file);
		} catch (EmptyFileException | InvalidFormatException | IOException e) {
			throw new IstfException(String.format("Workbook creation from file \"%s\" has been failed.", file.getAbsolutePath()), e);
		}
	}
	
	private String getWorkbookInitializationSource() {
		return initializedFromFile() ? "file with path \"" + getSourceFile().getAbsolutePath() + "\"" : "InputStream";
	}
}
