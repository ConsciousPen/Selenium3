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

		//mainApp().open();
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

		List <DisbursementFile> recordsStrnage = readCSV(LOCAL_FOLDER_PATH + fileName);
		recordsStrnage.get(1);
		DisbursementFile neededString = null;
		for(DisbursementFile s:recordsStrnage){
			if(s.getAgreementNumber().equals("VASS926232113")){
				neededString = s;
			}
		}
		neededString.getAgreementNumber();
		//DisbursementEngineHelper.readFile(DisbursementEngineHelper.DisbursementEngineFileBuilder.class, fileName);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}


	public static List readCSV(String path) throws FileNotFoundException, IOException {
		List<DisbursementFile> lines = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(path));

		String line = br.readLine(); // Reading header, Ignoring
//String line = "";
		try {
			do {
				String cvsSplitBy = "\\|";
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
			while ((line = br.readLine()) != null && !line.isEmpty());
		} catch (ArrayIndexOutOfBoundsException E){ }
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

		@Override
		public String toString() {
			return "records [recordType=" + recordType + ", requestRefereceId=" + requestReferenceId
					+ ", refundType=" + refundType + ", refundMethod=" + refundMethod +","
					+ " issueDate=" + issueDate +", agreementNumber=" + agreementNumber +", agreementSourceSystem=" + agreementSourceSystem
					+ ", productType=" + productType +", companyId=" + companyId + ", productType=" + productType
					+ ", companyId=" + companyId + ", insuredFirstName=" + insuredFirstName +", insuredLastName=" + insuredLastName +"]";
		}
	}


}
