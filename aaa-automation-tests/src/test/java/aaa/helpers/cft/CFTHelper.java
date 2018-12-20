package aaa.helpers.cft;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import aaa.modules.BaseTest;
import aaa.modules.cft.csv.model.FinancialPSFTGLObject;
import aaa.modules.cft.csv.model.Footer;
import aaa.modules.cft.csv.model.Header;
import aaa.modules.cft.csv.model.Record;
import toolkit.db.DBService;
import toolkit.utils.SSHController;

public class CFTHelper extends BaseTest {

	public static void checkDirectory(File directory) throws IOException {
		if (directory.mkdirs()) {
			log.info("\"{}\" folder was created", directory.getAbsolutePath());
		} else {
			FileUtils.cleanDirectory(directory);
		}
	}

	public static void checkFile(String dirPath, String fileName) {
		File directory = new File(dirPath);
		if (directory.mkdirs()) {
			log.info("\"{}\" folder was created", directory.getAbsolutePath());
		} else {
			FileUtils.deleteQuietly(new File(dirPath + fileName));
		}
	}

	public static Map<String, Double> getExcelValues(String downloadDir, String suffix) {
		Map<String, Double> accountsMapSummaryFromOR = new HashMap<>();
		for (File reportFile : new File(downloadDir).listFiles()) {
			if (!reportFile.getName().contains(suffix)) {
				continue;
			}
			int totalBalanceCell = reportFile.getName().contains("Policy") ? 14 : 15;

			try {
				XSSFSheet sheet = new XSSFWorkbook(reportFile).getSheetAt(0);
				for (Row row : sheet) {
					if (row.equals(null)) {
						continue;
					}
					String cellValue = row.getCell(3).getStringCellValue();
					if (StringUtils.isEmpty(cellValue) || !cellValue.matches("\\d+")) {
						continue;
					}
					if (accountsMapSummaryFromOR.containsKey(row.getCell(3).getStringCellValue())) {
						double amount = accountsMapSummaryFromOR.get(row.getCell(3).getStringCellValue())
								+ row.getCell(totalBalanceCell).getNumericCellValue();
						accountsMapSummaryFromOR.put(row.getCell(3).getStringCellValue(), amount);
					} else {
						accountsMapSummaryFromOR.put(row.getCell(3).getStringCellValue(), row.getCell(totalBalanceCell).getNumericCellValue());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		return accountsMapSummaryFromOR;
	}

	public static Map<String, Double> getDataBaseValues(String sql) {
		Map<String, Double> accountsMapSummaryFromDB = new HashMap<>();
		String transactionDate = TimeSetterUtil.getInstance().getStartTime().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String query = String.format(sql, transactionDate);
		List<Map<String, String>> dbResult = DBService.get().getRows(query);
		for (Map<String, String> dbEntry : dbResult) {
			accountsMapSummaryFromDB.put(dbEntry.get("LEDGERACCOUNTNO"), Double.parseDouble(dbEntry.get("AMOUNT")));
		}
		return accountsMapSummaryFromDB;
	}

	public static Map<String, Double> getFeedFilesValues(String downloadDir, String suffix) {
		Map<String, Double> accountsMapSummaryFromFeedFile = new HashMap<>();

		List<FinancialPSFTGLObject> allEntries = new ArrayList<>();
		for (File file : new File(downloadDir).listFiles()) {
			if (file.getName().contains(suffix)) {
				try {
					allEntries.addAll(transformToObject(FileUtils.readFileToString(file, Charset.defaultCharset())));
				} catch (IOException e) {
					log.error("Unable to get objects from file \"{}\"", file.getAbsolutePath());
				}
			}
		}
		for (FinancialPSFTGLObject entry : allEntries) {
			for (Record record : entry.getRecords()) {
				if (accountsMapSummaryFromFeedFile.containsKey(record.getBillingAccountNumber())) {
					double amount = accountsMapSummaryFromFeedFile.get(record.getBillingAccountNumber()) + record.getAmountDoubleValue();
					accountsMapSummaryFromFeedFile.put(record.getBillingAccountNumber(), amount);
				} else {
					accountsMapSummaryFromFeedFile.put(record.getBillingAccountNumber(), record.getAmountDoubleValue());
				}
			}
		}
		return accountsMapSummaryFromFeedFile;
	}

	public static void roundValuesToTwo(Map<String, Double> stringDoubleMap) {
		// Rounding values to 2
		for (Map.Entry<String, Double> stringDoubleEntry : stringDoubleMap.entrySet()) {
			stringDoubleMap.put(stringDoubleEntry.getKey(), BigDecimal.valueOf(stringDoubleEntry.getValue())
					.setScale(2, RoundingMode.HALF_UP)
					.doubleValue());
		}
	}

	public static int downloadComplete(File dir, String suffix) {
		int count = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				boolean result = name.toLowerCase().endsWith(suffix);
				return result;
			}
		}).length;
		return count;
	}

	public static int remoteDownloadComplete(SSHController sshControllerRemote, File dir) throws SftpException, JSchException {
		return sshControllerRemote.getFilesList(dir).size();
	}

	public static void setBorderToCellStyle(XSSFCellStyle style) {
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.MEDIUM);
	}

	private static List<FinancialPSFTGLObject> transformToObject(String fileContent) throws IOException {
		// if we fill know approach used in dev application following hardcoded indexes related approach can be changed to used in app
		List<FinancialPSFTGLObject> objectsFromCSV;
		try (CSVParser parser = CSVParser.parse(fileContent, CSVFormat.DEFAULT)) {
			objectsFromCSV = new ArrayList<>();
			FinancialPSFTGLObject object = null;
			for (CSVRecord record : parser.getRecords()) {
				// Each header has length == 22, footer ==56 and record == 123
				switch (record.get(0).length()) {
					case 22: {
						// parse header here
						object = new FinancialPSFTGLObject();
						Header entryHeader = new Header();
						entryHeader.setCode(record.get(0).substring(0, 11).trim());
						entryHeader.setDate(record.get(0).substring(11, record.get(0).length() - 3).trim());
						entryHeader.setNotKnownAttribute(record.get(0).substring(record.get(0).length() - 3, record.get(0).length()).trim());
						object.setHeader(entryHeader);
						break;
					}
					case 56: {
						// parse footer here
						Footer footer = new Footer();
						footer.setCode(record.get(0).substring(0, 11).trim());
						footer.setOverallExpSum(record.get(0).substring(11, 30).trim());
						footer.setOverallSum(record.get(0).substring(30, 46).trim());
						footer.setAmountOfRecords(record.get(0).substring(46, record.get(0).length()).trim());
						object.setFooter(footer);
						objectsFromCSV.add(object);
						break;
					}
					case 123: {
						// parse record body here
						Record entryRecord = new Record();
						entryRecord.setCode(record.get(0).substring(0, 11).trim());
						entryRecord.setBillingAccountNumber(record.get(0).substring(11, 21).trim());
						entryRecord.setProductCode(record.get(0).substring(21, 31).trim());
						entryRecord.setStateInfo(record.get(0).substring(31, 43).trim());
						entryRecord.setAmount(record.get(0).substring(43, 57).trim());
						entryRecord.setAction(record.get(0).substring(57, 87).trim());
						entryRecord.setActionDescription(record.get(0).substring(87, 117).trim());
						entryRecord.setPlusMinus(record.get(0).substring(117, record.get(0).length()).trim());
						object.getRecords().add(entryRecord);
						break;
					}
					default: {
						// ignore
					}
				}
			}
		}
		return objectsFromCSV;
	}
}
