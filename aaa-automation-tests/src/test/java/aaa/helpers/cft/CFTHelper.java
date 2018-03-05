package aaa.helpers.cft;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import aaa.modules.BaseTest;
import aaa.modules.cft.csv.model.FinancialPSFTGLObject;
import aaa.modules.cft.csv.model.Footer;
import aaa.modules.cft.csv.model.Header;
import aaa.modules.cft.csv.model.Record;
import toolkit.utils.SSHController;

public class CFTHelper extends BaseTest {

	public static void checkDirectory(File directory) throws IOException {
		if (directory.mkdirs()) {
			log.info("\"{}\" folder was created", directory.getAbsolutePath());
		} else {
			FileUtils.cleanDirectory(directory);
		}
	}

	public static List<FinancialPSFTGLObject> transformToObject(String fileContent) throws IOException {
		// if we fill know approach used in dev application following hardcoded indexes related approach can be changed to used in app
		List<FinancialPSFTGLObject> objectsFromCSV;
		try (CSVParser parser = CSVParser.parse(fileContent, CSVFormat.DEFAULT)) {
			objectsFromCSV = new ArrayList<>();
			FinancialPSFTGLObject object = null;
			for (CSVRecord record : parser.getRecords()) {
				// Each header has length == 22, footer ==56 and record == 123
				switch (record.get(0).length()) {
					case 22 : {
						// parse header here
						object = new FinancialPSFTGLObject();
						Header entryHeader = new Header();
						entryHeader.setCode(record.get(0).substring(0, 11).trim());
						entryHeader.setDate(record.get(0).substring(11, record.get(0).length() - 3).trim());
						entryHeader.setNotKnownAttribute(record.get(0).substring(record.get(0).length() - 3, record.get(0).length()).trim());
						object.setHeader(entryHeader);
						break;
					}
					case 56 : {
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
					case 123 : {
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
					default : {
						// ignore
					}
				}
			}
		}
		return objectsFromCSV;
	}

	public static void roundValuesToTwo(Map<String, Double> stringDoubleMap) {
		// Rounding values to 2
		for (Map.Entry<String, Double> stringDoubleEntry : stringDoubleMap.entrySet()) {
			stringDoubleMap.put(stringDoubleEntry.getKey(), BigDecimal.valueOf(stringDoubleEntry.getValue())
				.setScale(2, RoundingMode.HALF_UP)
				.doubleValue());
		}
	}

	public static void setBorderToCellStyle(XSSFCellStyle style) {
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.MEDIUM);
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
}
