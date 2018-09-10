package aaa.helpers.billing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import aaa.config.CsaaTestProperties;
import aaa.helpers.ssh.RemoteHelper;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.utils.logging.CustomLogger;

public class PaymentCentralHelper {

	private static final String FILE_LAST_PART = "PMT_E_PMTCTRL_PASSYS_7003_D";
	private static final String PAYMENT_CENTRAL_FILE_PATH = PropertyProvider.getProperty(CsaaTestProperties.JOB_FOLDER) + FILE_LAST_PART + "/inbound/%1$s";
	private static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("MMddyyyy");
	private static final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("HHmmss");

	/**
	 * This method is used to prepare file for aaaPaymentCentralRejectFeedAsyncJob
	 */
	public synchronized File createFile(String policyNumber, String agmtAmount, String paymentReferenceId) {

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
			PaymentCentralRejectionsFileBuilder paymentCentralRejectionsFileBuilder = new PaymentCentralRejectionsFileBuilder();
			bw.write(paymentCentralRejectionsFileBuilder.buildData(policyNumber, agmtAmount, paymentReferenceId, date));
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

	public class PaymentCentralRejectionsFileBuilder {
		String transactionType = "";//REF
		String pcTransactionId = ""; //or TransactionId
		String originalTransactionId = "";
		String paymentSourceSystem = "";//PCBCKOFF
		String companyId = "";
		String productType = "";
		String policyPrefix = "";
		String policyNumber = "";
		String agmtAmount = "";
		String rejectionCode = "";
		String fileDate = "";
		String fileTime = "";
		String nsfFlag = "";

		String buildData(String policyNumber, String agmtAmount, String paymentReferenceId, LocalDateTime date) {
			return new StringBuilder("DTL|")
					.append(transactionType).append("REV|")
					.append(paymentReferenceId).append("|")
					.append(paymentReferenceId).append("|")
					.append(paymentSourceSystem).append("PCBCKOFF|")
					.append(companyId).append("WUIC|")
					.append(productType).append("PA|")
					.append(policyNumber.substring(0, 2)).append("|")
					.append(policyNumber).append("|")
					.append(agmtAmount).append("|")
					.append(rejectionCode).append("NSF|")
					.append(date.format(DATE_PATTERN)).append("|")
					.append(date.format(TIME_PATTERN)).append("|")
					.append(nsfFlag).append("N\n").toString();
		}

		String buildTrail(String fileName, String fileDateTime, String agmtAmount) {
			return "TRL|" + fileName + "|" + fileDateTime + "|whoknowwhat|1|0001|" + agmtAmount + "\n";
		}
	}

}
