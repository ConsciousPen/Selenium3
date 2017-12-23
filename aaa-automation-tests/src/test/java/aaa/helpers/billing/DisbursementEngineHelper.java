package aaa.helpers.billing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import aaa.helpers.ssh.RemoteHelper;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.utils.logging.CustomLogger;

public class DisbursementEngineHelper {

	public static String DISBURSEMENT_ENGINE_PATH = "/AAA/JobFolders/%1$s/inbound/%2$s";

	/**
	 * This method is used for prepare disbursement engine file with data specified by input parameters.
	 *
	 * transactionNumber - transaction number
	 * refundMethod  - 'R' for automated refund , 'M' for manual refund
	 * paymentType - 'CHCK' - check, 'EFT' - eft, 'CRDC' - credit/debit card
	 * policyNumber - policy number
	 * productType - product type Auto = PA, Home = HO, PUP = PU
	 * refundAmount - refund amount
	 * accountLast4 - last 4 numbers
	 * checkNumber - check number
	 * accountType - MASTR, VISA, AMEX, DISC, CHKG, SAVG
	 * cardSubType - Debit, Credit
	 * refundStatus - SUCC - success, ERR - for error
	 */
	public static synchronized File createFile(DisbursementEngineFileBuilder builder, String fileNameLastPart) {
		final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("MMddyyyy");
		final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("HHmmss");
		File file;
		String fileName;
		LocalDateTime date = DateTimeUtils.getCurrentDateTime();

		do {
			fileName = date.format(DATE_PATTERN) + "_" + date.format(TIME_PATTERN) + "_" + fileNameLastPart + ".csv";
			file = new File(CustomLogger.getLogDirectory().concat("/DisbursementEngine_Files/"), fileName);
			date = date.plusSeconds(1);
		} while (file.exists());
		file.getParentFile().mkdir();

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			String header = MessageFormat.format("H|DEV|DSBCTRL|PASSYS|{0}|ETLNONPROD|71DCF95E-C|{1}|{2}|C48192E5-E|1\n", fileNameLastPart, fileName, date.format(DATE_TIME_PATTERN));
			bw.write(header);
			bw.write(builder.buildData(date.format(DATE_PATTERN)));
			bw.write(builder.buildTrail());
			bw.flush();
		} catch (IOException e) {
			throw new IstfException(e);
		}
		return file;
	}

/*	public static synchronized File readFile(DisbursementEngineFileBuilder builder, String fileNameLastPart) {
		final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("MMddyyyy");
		final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("HHmmss");
		File file;
		String fileName;
		LocalDateTime date = DateTimeUtils.getCurrentDateTime();

		do {
			fileName = date.format(DATE_PATTERN) + "_" + date.format(TIME_PATTERN) + "_" + fileNameLastPart + ".csv";
			file = new File(CustomLogger.getLogDirectory().concat("/DisbursementEngine_Files/"), fileName);
			date = date.plusSeconds(1);
		} while (file.exists());
		file.getParentFile().mkdir();

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String header = MessageFormat.format("H|DEV|DSBCTRL|PASSYS|{0}|ETLNONPROD|71DCF95E-C|{1}|{2}|C48192E5-E|1\n", fileNameLastPart, fileName, date.format(DATE_TIME_PATTERN));
			br.read();
			br.read(builder.buildData(date.format(DATE_PATTERN)));
			br.read(builder.buildTrail());
			br.read();
		} catch (IOException e) {
			throw new IstfException(e);
		}
		return file;
	}*/

	public static synchronized void copyFileToServer(File file, String folderName) {
		if (file == null)
			throw new IstfException("Disbursement engine file is NULL");
		RemoteHelper.uploadFile(file.getAbsolutePath(), String.format(DISBURSEMENT_ENGINE_PATH, folderName, file.getName()));
	}

	public static class DisbursementEngineFileBuilder {
		String transactionNumber="";
		String refundMethod="";
		String paymentType="";
		String policyNumber="";
		String productType="";
		String refundAmount="";
		String accountLast4="";
		String checkNumber="";
		String accountType="";
		String cardSubType="";
		String refundStatus="";

		public DisbursementEngineFileBuilder setTransactionNumber(String transactionNumber) {
			this.transactionNumber = transactionNumber;
			return this;
		}

		public DisbursementEngineFileBuilder setRefundMethod(String refundMethod) {
			this.refundMethod = refundMethod;
			return this;
		}

		public DisbursementEngineFileBuilder setPaymentType(String paymentType) {
			this.paymentType = paymentType;
			return this;
		}

		public DisbursementEngineFileBuilder setPolicyNumber(String policyNumber) {
			this.policyNumber = policyNumber;
			return this;
		}

		public DisbursementEngineFileBuilder setProductType(String productType) {
			this.productType = productType;
			return this;
		}

		public DisbursementEngineFileBuilder setRefundAmount(String refundAmount) {
			this.refundAmount = refundAmount;
			return this;
		}

		public DisbursementEngineFileBuilder setAccountLast4(String accountLast4) {
			this.accountLast4 = accountLast4;
			return this;
		}

		public DisbursementEngineFileBuilder setCheckNumber(String checkNumber) {
			this.checkNumber = checkNumber;
			return this;
		}

		public DisbursementEngineFileBuilder setAccountType(String accountType) {
			this.accountType = accountType;
			return this;
		}

		public DisbursementEngineFileBuilder setCardSubType(String cardSubType) {
			this.cardSubType = cardSubType;
			return this;
		}

		public DisbursementEngineFileBuilder setRefundStatus(String refundStatus) {
			this.refundStatus = refundStatus;
			return this;
		}

		public String buildData(String date) {
			return new StringBuilder("D|")
					.append(transactionNumber).append("|")
					.append(refundMethod).append("|")
					.append(paymentType).append("|")
					.append(date).append("|")
					.append(policyNumber).append("|")
					.append("PAS").append("|")
					.append(productType).append("||")
					.append(refundAmount).append("|")
					.append(checkNumber).append("|")
					.append(accountLast4).append("|")
					.append(accountType).append("|")
					.append(cardSubType).append("||")
					.append(refundStatus).append("|||\n").toString();
		}

		public String buildTrail() {
			return "T|1|" + refundAmount + "\n";
		}
	}
}
