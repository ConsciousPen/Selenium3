package aaa.helpers.billing;

import java.io.*;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import aaa.config.CsaaTestProperties;
import aaa.helpers.ssh.RemoteHelper;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.utils.logging.CustomLogger;

public class DisbursementEngineHelper {

	public static final String DISBURSEMENT_ENGINE_PATH = PropertyProvider.getProperty(CsaaTestProperties.JOB_FOLDER) + "%1$s/inbound/%2$s";

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
		DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("MMddyyyy");
		DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("HHmmss");
		File file;
		String fileName;
		LocalDateTime date = DateTimeUtils.getCurrentDateTime();

		do {
			fileName = date.format(DATE_PATTERN) + "_" + date.format(TIME_PATTERN) + "_" + fileNameLastPart + ".csv";
			file = new File(CustomLogger.getLogDirectory().concat("/DisbursementEngine_Files/"), fileName);
			date = date.plusSeconds(1);
		}
		while (file.exists());
		file.getParentFile().mkdir();

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			String header = MessageFormat.format("H|DEV|DSBCTRL|PASSYS|{0}|ETLNONPROD|71DCF95E-C|{1}|{2}|C48192E5-E|1\n", fileNameLastPart, fileName, date.format(DATE_TIME_PATTERN));
			bw.write(header);
			if (fileNameLastPart.contains("7037")) {
				bw.write(builder.buildRejectData(date.format(DATE_PATTERN)));
			} else if (fileNameLastPart.contains("7035")) {
				bw.write(builder.buildData(date.format(DATE_PATTERN)));
			} else {
				throw new IstfException("no folder specified or folder is not handled");
			}
			bw.write(builder.buildTrail());
			bw.flush();
		} catch (IOException e) {
			throw new IstfException(e);
		}
		return file;
	}

	public static synchronized void copyFileToServer(File file, String folderName) {
		if (file == null) {
			throw new IstfException("Disbursement engine file is NULL");
		}
		RemoteHelper.get().uploadFile(file.getAbsolutePath(), String.format(DISBURSEMENT_ENGINE_PATH, folderName, file.getName()));
	}

	public static class DisbursementEngineFileBuilder {
		String transactionNumber = "";
		String refundMethod = "";
		String paymentType = "";
		String policyNumber = "";
		String productType = "";
		String refundAmount = "";
		String accountLast4 = "";
		String checkNumber = "";
		String accountType = "";
		String cardSubType = "";
		String refundStatus = "";

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
					.append("12345|")
					.append(refundStatus).append("||\n").toString();
		}

		public String buildRejectData(String date) {
			return new StringBuilder("D|")
					.append(transactionNumber).append("|")
					//.append(refundMethod).append("|")
					.append(paymentType).append("|")
					.append(date).append("|")
					.append(date).append("|")
					.append(policyNumber).append("|")
					.append("PAS").append("|")
					.append(productType).append("||")//includes underwriting
					.append(refundAmount).append("|")
					//.append(checkNumber).append("|")
					.append(accountLast4).append("|")
					.append(accountType).append("|")
					.append(cardSubType).append("|")
					.append("12345|err\n").toString();
			//.append(refundStatus).append("||\n").toString();
		}

		public String buildTrail() {
			return "T|1|" + refundAmount + "\n";
		}
	}

	public static List readDisbursementFile(String path){
		List<DisbursementFile> lines = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));

			String line = br.readLine(); // Reading header, Ignoring
			//String line = "";

			while ((line = br.readLine()) != null && !line.isEmpty()) {
				String cvsSplitBy = "\\|";
				String[] fields = line.split(cvsSplitBy);
				if (fields.length < 28) {
					continue;
				}
				String recordType = fields[0];
				String requestReferenceId = fields[1];
				String refundType = fields[2];
				String refundMethod = fields[3];
				String issueDate = fields[4];
				String agreementNumber = fields[5];
				String agreementSourceSystem = fields[6];
				String productType = fields[7];
				String companyId = fields[8];
				String insuredFirstName = fields[9];
				String insuredLastName = fields[10];
				String additionalInsuredFirstName = fields[11];
				String additionalInsuredLastName = fields[12];
				String deceasedNamedInsuredFlag = fields[13];
				String policyState = fields[14];
				String refundAmount = fields[15];
				String payeeName = fields[16];
				String payeeStreetAddress1 = fields[17];
				String payeeStreetAddress2 = fields[18];
				String payeeCity = fields[19];
				String payeeState = fields[20];
				String payeeZip = fields[21];
				String insuredEmailId = fields[22];
				String checkNumber = fields[23];
				String printerIdentificationCode = fields[24];
				String refundReason = fields[25];
				String refundReasonDescription = fields[26];
				String referencePaymentTransactionNumber = fields[27];
				String eRefundEligible = fields[28];

				DisbursementFile records = new DisbursementFile(recordType, requestReferenceId, refundType, refundMethod, issueDate, agreementNumber, agreementSourceSystem,
						productType, companyId, insuredFirstName, insuredLastName, additionalInsuredFirstName, additionalInsuredLastName, deceasedNamedInsuredFlag, policyState, refundAmount,
						payeeName, payeeStreetAddress1, payeeStreetAddress2, payeeCity, payeeState, payeeZip, insuredEmailId, checkNumber, printerIdentificationCode, refundReason,
						refundReasonDescription, referencePaymentTransactionNumber, eRefundEligible);
				lines.add(records);
			}
			br.close();
		} catch (ArrayIndexOutOfBoundsException | IOException exception) {
			throw new IstfException("Error during reading of Disbursement File", exception);
		}

		return lines;
	}

	public static class DisbursementFile {
		private String recordType;
		private String requestReferenceId;
		private String refundType;
		private String refundMethod;
		private String issueDate;
		private String agreementNumber;
		private String agreementSourceSystem;
		private String productType;
		private String companyId;
		private String insuredFirstName;
		private String insuredLastName;
		private String additionalInsuredFirstName;
		private String additionalInsuredLastName;
		private String deceasedNamedInsuredFlag;
		private String policyState;
		private String refundAmount;
		private String payeeName;
		private String payeeStreetAddress1;
		private String payeeStreetAddress2;
		private String payeeCity;
		private String payeeState;
		private String payeeZip;
		private String insuredEmailId;
		private String checkNumber;
		private String printerIdentificationCode;
		private String refundReason;
		private String refundReasonDescription;
		private String referencePaymentTransactionNumber;
		private String eRefundEligible;

		public DisbursementFile(String recordType, String requestReferenceId, String refundType, String refundMethod,
				String issueDate, String agreementNumber, String agreementSourceSystem, String productType, String companyId, String insuredFirstName, String insuredLastName,
				String additionalInsuredFirstName, String additionalInsuredLastName, String deceasedNamedInsuredFlag, String policyState, String refundAmount,
				String payeeName, String payeeStreetAddress1, String payeeStreetAddress2, String payeeCity, String payeeState, String payeeZip, String insuredEmailId,
				String checkNumber, String printerIdentificationCode, String refundReason, String refundReasonDescription, String referencePaymentTransactionNumber, String eRefundEligible) {
			this.recordType = recordType;
			this.requestReferenceId = requestReferenceId;
			this.refundType = refundType;
			this.refundMethod = refundMethod;
			this.issueDate = issueDate;
			this.agreementNumber = agreementNumber;
			this.agreementSourceSystem = agreementSourceSystem;
			this.productType = productType;
			this.companyId = companyId;
			this.insuredFirstName = insuredFirstName;
			this.insuredLastName = insuredLastName;
			this.additionalInsuredFirstName = additionalInsuredFirstName;
			this.additionalInsuredLastName = additionalInsuredLastName;
			this.deceasedNamedInsuredFlag = deceasedNamedInsuredFlag;
			this.policyState = policyState;
			this.refundAmount = refundAmount;
			this.payeeName = payeeName;
			this.payeeStreetAddress1 = payeeStreetAddress1;
			this.payeeStreetAddress2 = payeeStreetAddress2;
			this.payeeCity = payeeCity;
			this.payeeState = payeeState;
			this.payeeZip = payeeZip;
			this.insuredEmailId = insuredEmailId;
			this.checkNumber = checkNumber;
			this.printerIdentificationCode = printerIdentificationCode;
			this.refundReason = refundReason;
			this.refundReasonDescription = refundReasonDescription;
			this.referencePaymentTransactionNumber = referencePaymentTransactionNumber;
			this.eRefundEligible = eRefundEligible;

		}

		public String getRecordType() {
			return recordType;
		}

		public String getRequestRefereceId() {
			return requestReferenceId;
		}

		public String getRefundType() {
			return refundType;
		}

		public String getRefundMethod() {
			return refundMethod;
		}

		public String getIssueDate() {
			return issueDate;
		}

		public String getAgreementNumber() {
			return agreementNumber;
		}

		public String getAgreementSourceSystem() {
			return agreementSourceSystem;
		}

		public String getProductType() {
			return productType;
		}

		public String getCompanyId() {
			return companyId;
		}

		public String getInsuredFirstName() {
			return insuredFirstName;
		}

		public String getInsuredLastName() {
			return insuredLastName;
		}

		public String getRequestReferenceId() {
			return requestReferenceId;
		}

		public String getAdditionalInsuredFirstName() {
			return additionalInsuredFirstName;
		}

		public String getAdditionalInsuredLastName() {
			return additionalInsuredLastName;
		}

		public String getDeceasedNamedInsuredFlag() {
			return deceasedNamedInsuredFlag;
		}

		public String getPolicyState() {
			return policyState;
		}

		public String getRefundAmount() {
			return refundAmount;
		}

		public String getPayeeName() {
			return payeeName;
		}

		public String getPayeeStreetAddress1() {
			return payeeStreetAddress1;
		}

		public String getPayeeStreetAddress2() {
			return payeeStreetAddress2;
		}

		public String getPayeeCity() {
			return payeeCity;
		}

		public String getPayeeState() {
			return payeeState;
		}

		public String getPayeeZip() {
			return payeeZip;
		}

		public String getInsuredEmailId() {
			return insuredEmailId;
		}

		public String getCheckNumber() {
			return checkNumber;
		}

		public String getPrinterIdentificationCode() {
			return printerIdentificationCode;
		}

		public String getRefundReason() {
			return refundReason;
		}

		public String getRefundReasonDescription() {
			return refundReasonDescription;
		}

		public String getReferencePaymentTransactionNumber() {
			return referencePaymentTransactionNumber;
		}

		public String geteRefundEligible() {
			return eRefundEligible;
		}

		@Override
		public String toString() {
			return "records [recordType=" + recordType + ", requestRefereceId=" + requestReferenceId
					+ ", refundType=" + refundType + ", refundMethod=" + refundMethod + ","
					+ " issueDate=" + issueDate + ", agreementNumber=" + agreementNumber + ", agreementSourceSystem=" + agreementSourceSystem
					+ ", productType=" + productType + ", companyId=" + companyId + ", productType=" + productType
					+ ", companyId=" + companyId + ", insuredFirstName=" + insuredFirstName + ", insuredLastName=" + insuredLastName
					+ ", additionalInsuredFirstName=" + additionalInsuredFirstName + ", additionalInsuredLastName=" + additionalInsuredLastName + ", deceasedNamedInsuredFlag="
					+ deceasedNamedInsuredFlag
					+ ", policyState=" + policyState + ", refundAmount=" + refundAmount + ", payeeName=" + payeeName
					+ ", payeeStreetAddress1=" + payeeStreetAddress1 + ", payeeStreetAddress2=" + payeeStreetAddress2 + ", payeeCity=" + payeeCity
					+ ", payeeState=" + payeeState + ", payeeZip=" + payeeZip + ", insuredEmailId=" + insuredEmailId
					+ ", checkNumber=" + checkNumber + ", printerIdentificationCode=" + printerIdentificationCode + ", refundReason=" + refundReason
					+ ", refundReasonDescription=" + refundReasonDescription + ", referencePaymentTransactionNumber=" + referencePaymentTransactionNumber + ", eRefundEligible=" + eRefundEligible
					+ "]";
		}
	}

	public static List readDisbursementVoidFile(String path) throws IOException {
		List<DisbursementVoidFile> lines = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(path));

		String line = br.readLine(); // Reading header, Ignoring
		//String line = "";
		try {
			while ((line = br.readLine()) != null && !line.isEmpty()) {
				String cvsSplitBy = "\\|";
				String[] fields = line.split(cvsSplitBy);
				String recordType = fields[0];
				String requestReferenceId = fields[1];
				String pcReferenceId = fields[2];
				String refundDate = fields[3];
				String issueDate = fields[4];
				String refundType = fields[5];//VOID
				String agreementNumber = fields[6];
				String agreementSourceSystem = fields[7];
				String productType = fields[8];
				String companyId = fields[9];
				String refundAmount = fields[10];
				String dummy = fields[11];//dummy?
				String refundReason = fields[12];

				DisbursementVoidFile recordsVoid = new DisbursementVoidFile(recordType, requestReferenceId, pcReferenceId, refundDate, issueDate, refundType, agreementNumber, agreementSourceSystem, productType, companyId, refundAmount, dummy, refundReason);
				lines.add(recordsVoid);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		br.close();
		return lines;
	}

	public static class DisbursementVoidFile {
		private String recordType;
		private String requestReferenceId;
		private String pcReferenceId;
		private String refundDate;
		private String issueDate;
		private String refundType;
		private String agreementNumber;
		private String agreementSourceSystem;
		private String productType;
		private String companyId;
		private String refundAmount;
		private String dummy;
		private String refundReason;

		public DisbursementVoidFile(String recordType, String requestReferenceId, String pcReferenceId,String refundDate,
				String issueDate, String refundType,String agreementNumber, String agreementSourceSystem, String productType, String companyId,
				String refundAmount, String dummy,
				String refundReason) {
			this.recordType = recordType;
			this.requestReferenceId = requestReferenceId;
			this.pcReferenceId = pcReferenceId;
			this.refundDate = refundDate;
			this.issueDate = issueDate;

			this.refundType = refundType;
			this.agreementNumber = agreementNumber;
			this.agreementSourceSystem = agreementSourceSystem;
			this.productType = productType;
			this.companyId = companyId;

			this.refundAmount = refundAmount;
			this.dummy = dummy;
			this.refundReason = refundReason;
		}

		public String getRecordType() {
			return recordType;
		}

		public String getRequestReferenceId() {
			return requestReferenceId;
		}

		public String getPcReferenceId() {
			return pcReferenceId;
		}

		public String getRefundType() {
			return refundType;
		}

		public String getIssueDate() {
			return issueDate;
		}

		public String getRefundDate() {
			return refundDate;
		}

		public String getAgreementNumber() {
			return agreementNumber;
		}

		public String getAgreementSourceSystem() {
			return agreementSourceSystem;
		}

		public String getProductType() {
			return productType;
		}

		public String getCompanyId() {
			return companyId;
		}

		public String getDummy() {
			return dummy;
		}

		public String getRefundAmount() {
			return refundAmount;
		}

		public String getRefundReason() {
			return refundReason;
		}


		@Override
		public String toString() {
			return "records [recordType=" + recordType + ", requestRefereceId=" + requestReferenceId + ", pcRefereceId=" + pcReferenceId
					+ ", refundType=" + refundType
					+ " issueDate=" + issueDate + ", agreementNumber=" + agreementNumber + ", agreementSourceSystem=" + agreementSourceSystem
					+ ", productType=" + productType + ", companyId=" + companyId + ", productType=" + productType
					+ ", companyId=" + companyId
					+ ", dummy=" + dummy + ", refundAmount=" + refundAmount
					+ ", refundReason=" + refundReason
					+ "]";
		}
	}
}
