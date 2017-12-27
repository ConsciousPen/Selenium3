package aaa.modules.regression.billing_and_payments.home_ca.ho3.functional;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.billing.DisbursementEngineHelper;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.AdvancedAllocationsActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.cft.csv.model.FinancialPSFTGLObject;
import aaa.modules.cft.csv.model.Footer;
import aaa.modules.cft.csv.model.Header;
import aaa.modules.cft.csv.model.Record;
import aaa.modules.regression.billing_and_payments.auto_ss.functional.preconditions.TestRefundProcessPreConditions;
import aaa.modules.regression.billing_and_payments.template.PolicyBilling;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestRefundProcess extends PolicyBilling implements TestRefundProcessPreConditions {

	private static final String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
	private static final String REMOTE_FOLDER_PATH = "/home/mp2/pas/sit/DSB_E_PASSYS_DSBCTRL_7025_D/outbound/";
	private static final String LOCAL_FOLDER_PATH = "src/test/resources/stubs/";
	private TestData tdBilling = testDataManager.billingAccount;
	private TestData tdRefund = tdBilling.getTestData("Refund", "TestData_Check");
	private BillingAccount billingAccount = new BillingAccount();
	private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
	private AdvancedAllocationsActionTab advancedAllocationsActionTab = new AdvancedAllocationsActionTab();
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private BindTab bindTab = new BindTab();
	private ApplicantTab applicantTab = new ApplicantTab();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Test(description = "Precondition for TestRefundProcess tests")
	public void precondJobAdding() {
		adminApp().open();
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_REFUND_GENERATION_ASYNC_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_REFUND_DISBURSEMENT_ASYNC_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_REFUND_DISBURSEMENT_RECEIVE_INFO_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_REFUNDS_DISBURSMENT_REJECTIONS_ASYNC_JOB);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-7039", "PAS-7196"})
	public void pas7039_newDataElements(@Optional("VA") String state) throws SftpException, JSchException, IOException {

	/*	mainApp().open();
		//SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, "VAH3926232128");
		createCustomerIndividual();
		String policyNumber = createPolicy();
		log.info("policyNumber: {}", policyNumber);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.APPLICANT.get());
		applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), MultiAssetList.class).getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.RELATIONSHIP_TO_PRIMARY_NAMED_INSURED.getLabel(), ComboBox.class).setValue("Deceased");

		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
		bindTab.submitTab();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccount.refund().manualRefundPerform("Check","100");

		//TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(1));

		CustomAssert.enableSoftMode();
		RemoteHelper.clearFolder("/AAA/JobFolders/DSB_E_PASSYS_DSBCTRL_7025_D/outbound");
		String beforeJobFileDateTime = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
		String afterJobFileDateTime = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		log.info(beforeJobFileDateTime);
		log.info(afterJobFileDateTime);*/

		mainApp().open();
		//SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, "VAH3933661307");

/*		String monitorInfo = "AAAA";
		String monitorAddress = "AAAA";
		SSHController sshControllerRemote = new SSHController(
				monitorAddress,
				PropertyProvider.getProperty("test.ssh.user"),
				PropertyProvider.getProperty("test.ssh.password"));
		//RemoteHelper.downloadFileWithWait("/AAA/JobFolders/DSB_E_PASSYS_DSBCTRL_7025_D/outbound/20171222_184201_DSB_E_PASSYS_DSBCTRL_7025_D.csv", "src/test/resources/stubs/", 5000);
		//sshControllerRemote.downloadFolder(new File("/d/AAA/JobFolders/DSB_E_PASSYS_DSBCTRL_7025_D/outbound/"), new File("src/test/resources/stubs/"));
		//sshControllerRemote.getFolderContent(new FilR("/d/AAA/JobFolders/DSB_E_PASSYS_DSBCTRL_7025_D/outbound/"));*/

		//TODO doesn't work in VDMs
/*		RemoteHelper.waitForFilesAppearance(REMOTE_FOLDER_PATH, 10, "VAH3933661307");
		String neededFilePath = RemoteHelper.waitForFilesAppearance(REMOTE_FOLDER_PATH, "csv", 10, "VAH3933661307").get(0);
		String fileName = neededFilePath.replace(REMOTE_FOLDER_PATH, "");
		String fileContent = RemoteHelper.getFileContent(neededFilePath);

		RemoteHelper.downloadFile(neededFilePath, LOCAL_FOLDER_PATH + fileName);*/

		String fileName = "20171222_180434_DSB_E_PASSYS_DSBCTRL_7025_D.csv";

		List records = readCSV(LOCAL_FOLDER_PATH + fileName);
		records.get(1);
		String[] aaa = fileReaderX(LOCAL_FOLDER_PATH + fileName);

		aaa.toString();
		DisbursementEngineHelper.readFile(DisbursementEngineHelper.DisbursementEngineFileBuilder.class, fileName);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	public static String[] fileReaderX(String filePath) {

		String csvFile = filePath;
		String line = "";
		String cvsSplitBy = "|";

		String[] contentLine = new String[0];
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

			while ((line = br.readLine()) != null) {

				// use comma as separator
				contentLine = line.split(cvsSplitBy);

				System.out.println("return line" + contentLine.toString());

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return contentLine;
	}

	public static List readCSV(String path) throws FileNotFoundException, IOException {
		List lines = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(path));

		String line = br.readLine(); // Reading header, Ignoring

		while ((line = br.readLine()) != null && !line.isEmpty()) {
			String cvsSplitBy = "|";
			String[] fields = line.split(cvsSplitBy);
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
			DisbursementFile records = new DisbursementFile(recordType, requestReferenceId, refundType, refundMethod, issueDate, agreementNumber, agreementSourceSystem,
					productType, companyId, insuredFirstName, insuredLastName);
			lines.add(records);
		}
		br.close();
		return lines;
	}

	private static class DisbursementFile {
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

		public DisbursementFile(String recordType, String requestReferenceId, String refundType, String refundMethod,
				String issueDate, String agreementNumber, String agreementSourceSystem, String productType, String companyId, String insuredFirstName, String insuredLastName) {
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

		}

		public String recordType() {
			return recordType;
		}

		public String requestRefereceId() {
			return requestReferenceId;
		}

		public String refundType() {
			return refundType;
		}

		public String refundMethod() {
			return refundMethod;
		}

		public String issueDate() {
			return issueDate;
		}

		public String agreementNumber() {
			return agreementNumber;
		}

		public String agreementSourceSystem() {
			return agreementSourceSystem;
		}

		public String productType() {
			return productType;
		}

		public String companyId() {
			return companyId;
		}

		public String insuredFirstName() {
			return insuredFirstName;
		}

		public String insuredLastName() {
			return insuredLastName;
		}

		@Override
		public String toString() {
			return "records [recordType=" + recordType + ", requestRefereceId=" + requestReferenceId
					+ ", refundType=" + refundType + "]";
		}
	}

	private List<FinancialPSFTGLObject> transformToObject(String fileContent) throws IOException {
		// if we fill know approach used in dev application following hardcoded indexes related approach can be changed to used in app
		List<FinancialPSFTGLObject> objectsFromCSV;
		try (CSVParser parser = CSVParser.parse(fileContent, CSVFormat.DEFAULT)) {
			objectsFromCSV = new ArrayList<>();
			FinancialPSFTGLObject object = null;
			for (CSVRecord record : parser.getRecords()) {
				//Each header has length == 22, footer ==56 and record == 123
				switch (record.get(0).length()) {
					case 22: {
						//parse header here
						object = new FinancialPSFTGLObject();
						Header entryHeader = new Header();
						entryHeader.setCode(record.get(0).substring(0, 11).trim());
						entryHeader.setDate(record.get(0).substring(11, record.get(0).length() - 3).trim());
						entryHeader.setNotKnownAttribute(record.get(0).substring(record.get(0).length() - 3, record.get(0).length()).trim());
						object.setHeader(entryHeader);
						break;
					}
					case 56: {
						//parse footer here
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
						//parse record body here
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
						//ignore
					}
				}
			}
		}
		return objectsFromCSV;
	}

}
