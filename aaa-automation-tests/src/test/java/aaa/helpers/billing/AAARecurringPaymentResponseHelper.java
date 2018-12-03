package aaa.helpers.billing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang.RandomStringUtils;
import aaa.config.CsaaTestProperties;
import aaa.helpers.ssh.RemoteHelper;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.utils.logging.CustomLogger;

public class AAARecurringPaymentResponseHelper {

	private static final String FILE_LAST_PART = "PMT_E_PMTCTRL_PASSYS_7002_D";
	private static final String PAYMENT_CENTRAL_FILE_PATH = PropertyProvider.getProperty(CsaaTestProperties.JOB_FOLDER) + FILE_LAST_PART + "/inbound/%1$s";
	private static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("MMddyyyy");
	private static final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("HHmmss");

	/**
	 * This method is used to prepare payment file for aaaRecurringPaymentsResponseProcessAsyncJob
	 */
	public synchronized File createFile(String policyNumber, String agmtAmount, String paymentId, String paymentStatus) {

		File file;
		String fileName;
		LocalDateTime date = DateTimeUtils.getCurrentDateTime();

		do {
			fileName = date.format(DATE_PATTERN) + "_" + date.format(TIME_PATTERN) + "_" + FILE_LAST_PART + ".dat";
			file = new File(CustomLogger.getLogDirectory().concat("/DisbursementEngine_Files/"), fileName);
			date = date.plusSeconds(1);
		}
		while (file.exists());
		file.getParentFile().mkdir();

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			String header = MessageFormat.format("HDR|DEV|PMTCTRL|" + FILE_LAST_PART + "|ETLNONPROD|71DCF95E-C|{1}|{2}|C48192E5-E|1\n", FILE_LAST_PART, fileName, date.format(DATE_TIME_PATTERN));
			bw.write(header);
			AAARecurringPaymentResponseFileBuilder paymentCentralRejectionsFileBuilder = new AAARecurringPaymentResponseFileBuilder();
			bw.write(paymentCentralRejectionsFileBuilder.buildData(policyNumber, agmtAmount, paymentId, date, paymentStatus));
			bw.write(paymentCentralRejectionsFileBuilder.buildTrail(fileName, date.format(DATE_TIME_PATTERN), agmtAmount));
			bw.flush();
		} catch (IOException e) {
			throw new IstfException(e);
		}
		return file;
	}

	public static synchronized void copyFileToServer(File file) {
		if (file == null) {
			throw new IstfException("Disbursement engine file is NULL");
		}
		RemoteHelper.get().uploadFile(file.getAbsolutePath(), String.format(PAYMENT_CENTRAL_FILE_PATH, file.getName()));
	}

	public class AAARecurringPaymentResponseFileBuilder {

		String referenceId = "";    //1
		String dateTime = "";   //2
		String pcTransactionStatus = "";//3
		String paymentId = "";  //6
		String agmtAmount1 = "";    //7
		String rejectionReason = "";    //9
		String policyNumber = "";   //17
		String bunchOfRandomFields = "";

		String buildData(String policyNumber, String agmtAmount, String paymentId, LocalDateTime date, String paymentStatus) {
			if (!"SUCC".equals(paymentStatus)) {
				rejectionReason = "P:502-LOST STOLEN";
			}
			return new StringBuilder("DTL|")
					.append(referenceId).append(RandomStringUtils.randomNumeric(7) + "|")
					.append(date.format(DATE_TIME_PATTERN)).append("|")
					.append(paymentStatus).append("|||")
					.append(paymentId).append("|")
					.append(agmtAmount).append("||")
					.append(rejectionReason).append("||||||||")
					.append(policyNumber).append("||||||||||||||||||\n").toString();
		}

		String buildTrail(String fileName, String fileDateTime, String agmtAmount) {
			return "TRL|" + fileName + "|" + fileDateTime + "|whoknowwhat|1|0001|" + agmtAmount + "\n";
		}
	}

}
