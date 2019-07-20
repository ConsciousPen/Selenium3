package aaa.modules.bct.batch.miscellaneous;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.eisa.utils.batchjob.ws.model.WSJobSummary;
import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;

public class SaveJobResult {
	private static Logger log = LoggerFactory.getLogger(SaveJobResult.class);

	private static final String[] columns = {"jobName", "executionTime", "totalItems", "totalSuccess", "totalFailure", "startTime", "endTime"};
	private static final String SHEET_NAME = "BCT_BatchJobs";
	private static final String PATH_RESOURCES_GENERATED = PropertyProvider.getProperty(CsaaTestProperties.USER_DIR_PROP).replace("\\", "/") + "/target/test-output/generated/";//"src/test/resources/generated/".replace("/", File.separator);
	private static String FILE_NAME = null;
	private static String FULL_PATH_TO_THE_FILE = PATH_RESOURCES_GENERATED;

	static {
		FILE_NAME = "BatchJobs_" + new SimpleDateFormat("MM_dd_yyyy_HHmmss").format(new Date());
		FULL_PATH_TO_THE_FILE = FULL_PATH_TO_THE_FILE + FILE_NAME + ".xlsx";
		prepareFolders();

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet(SHEET_NAME);
		createHeader(workbook, sheet);
		autoSizeColumns(sheet);

		File xls = new File(FULL_PATH_TO_THE_FILE);
		try (FileOutputStream fileOut = new FileOutputStream(xls)) {
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertThat(xls).exists().isFile().canRead();
		log.info("File was generated: {}\n\n", PATH_RESOURCES_GENERATED);
	}

	/**
	 * Add row to existing file
	 * @param latestJobRun
	 * @throws IOException
	 */
	public static synchronized void saveToXls(WSJobSummary latestJobRun) throws IOException {
		File xls = new File(FULL_PATH_TO_THE_FILE);
		try (FileInputStream inputStream = new FileInputStream(xls)) {
			try (Workbook workbook = new XSSFWorkbook(inputStream)) {

				Sheet firstSheet = workbook.getSheet(SHEET_NAME);

				Row row = firstSheet.createRow(getNextRowNumber(firstSheet));
				row.createCell(0).setCellValue(latestJobRun.getJobName());
				row.createCell(1).setCellValue(Double.parseDouble(String.valueOf(getJobExecutionTime(latestJobRun)))/60);
				row.createCell(2).setCellValue(latestJobRun.getTotalItems());
				row.createCell(3).setCellValue(latestJobRun.getTotalSuccess());
				row.createCell(4).setCellValue(latestJobRun.getTotalFailure());
				row.createCell(5).setCellValue(latestJobRun.getStartTime());
				row.createCell(6).setCellValue(latestJobRun.getEndTime());

				autoSizeColumns(firstSheet);
				try (FileOutputStream fileOut = new FileOutputStream(xls)) {
					workbook.write(fileOut);
				}
				workbook.close();
			}
		}
	}

	public static long getJobExecutionTime(WSJobSummary latestJobRun) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		formatter.setLenient(false);

		Date startTime = null;
		Date endTime = null;
		try {
			startTime = formatter.parse(latestJobRun.getStartTime());
			endTime = formatter.parse(latestJobRun.getEndTime());

		} catch (ParseException e) {
			e.printStackTrace();
		}
		long diff = endTime.getTime() - startTime.getTime();
		return diff > 0 ? TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS) : 1;
	}

	public static int getNextRowNumber(Sheet sheet) {
		int rows = 0;
		Iterator<Row> rowIterator = sheet.rowIterator();
		while (rowIterator.hasNext()) {
			log.info(String.valueOf(++rows));
			Row row = (Row) rowIterator.next();
		}
		return rows;
	}

	/**
	 *
	 Create a CellStyle with the font
	 Create a Row
	 Create header cells
	 Create Cell Style for formatting Date
	 Resize all columns to fit the content size
	 */
	public static void createHeader(Workbook workbook, Sheet sheet) {
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.BLACK.getIndex());

		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		Row headerRow = sheet.createRow(0);

		for (int i = 0; i < columns.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columns[i]);
			cell.setCellStyle(headerCellStyle);
		}
	}

	/**
	 * Clean and create destination folder if exist
	 */
	private static void prepareFolders() {
		try {
			FileUtils.deleteDirectory(Paths.get(PATH_RESOURCES_GENERATED).toFile());
			log.info("Path {} deleted", PATH_RESOURCES_GENERATED);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Files.createDirectories(Paths.get(PATH_RESOURCES_GENERATED));
			log.info("Path {} created", PATH_RESOURCES_GENERATED);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot create directories " + PATH_RESOURCES_GENERATED, e);
		}
	}

	private static void autoSizeColumns(Sheet sheet) {
		for (int i = 0; i < columns.length; i++) {
			sheet.autoSizeColumn(i);
		}
	}

}