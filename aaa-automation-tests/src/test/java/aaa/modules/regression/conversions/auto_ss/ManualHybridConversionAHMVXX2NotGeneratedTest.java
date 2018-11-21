package aaa.modules.regression.conversions.auto_ss;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.PRE_RENEWAL;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

/**
 * @author Tatsiana Saltsevich
 * @name Manual Hybrid Conversion verify AHMVXX2 not generated ("D-T-AU-SS-CL-956-CNV")
 * @scenario
 * 1. (DD0 (R-40)) Create OR account
 * 2. Initiate renewal entry in Customer 'Take action' dropdown, for example, policy number is 123456789, source system SIS, policy effective date today + 40
 * 3. Press OK
 * 4. Initiate renewal entry again with the same data in step 2
 * 5. Press OK
 * 6. Open conversion renewal image in Data gathering mode
 * 7. Enter membership number such that status is other than 'Active' (3111111111111121)
 * 8. Navigate to Driver tab
 * 9. Add driver available for rating (FirstName/LastName = 'MvrChargeable Activity')
 * 10. Navigate to Rating Detail Reports tab and order all reports manually. Override Insurance score, that it is 651
 * 11. Navigate to P&C tab and Calculate Premium
 * 12. Navigate to DAR tab, order reports
 * 13. Navigate to Drivers tab, change MVR chargeable driver to Excluded
 * 14. Navigate to P&C tab and Calculate Premium
 * 15. Navigate to Bind tab and Save & Exit renewal
 * 16. Override errors if necessary
 * 17. Run the batch jobs (AAAPreRenewalNoticeAsyncJob, Run aaaDocGen Job) -> Batch jobs is run successfully. Check AAADOCGENENTITY table for PRE RENEWAL eventname/package
 * 18. (DD0+5 (R-35)) 1. Run the batch job (Renewal_Offer_Generation_Part2, Run aaaDocGen Job)
 * 19. Retrieve the policy and validate the Renewal Image Status for converted policy - Status should be "Proposed"
 * 20. Navigate to Policy Consolidated View
 * 21. Navigate to E-Folder -
 *     #V11 - Pre-Renewal notice form #AAPRN1OR (for Oregon) is NOT generated and NOT found in e-folder
 *     #V12 - Membership validation letter (AHMVXX2) is NOT generated and included in the renewal offer/packet.
 *     #V13 - 'Consumer Information Notice' (AHAUXX) is NOT generated (can be generated, depends on multiple conditions in 880-244CL US)
 * 22. (DD0+20 (R-20)) Run the job (aaaRenewalNoticeBillAsyncJob, aaaDocGen to generate the bill)
 * 23. Navigate to the Billing tab - Installment bill is generated under Bills and Statement section of the Billing tab
 *     Type = "Bill", Date = Installment due date.
 * 24. (DD0+40 (R)) Make the renewal term payment - Payment is successfully made
 * 25. (R+1) Run the batch job (PolicyStatusUpdateJob, policyLapsedRenewalProcessAsyncJob)
 * 26. Navigate to Policy Consolidated View - Policy Status = Active
 * 27. (2DD3-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill - Installment bill is generated under Bills and Statement section of the Billing tab
 *     Type = "Bill", Date = Installment due date.
 * 28. (2DD3) Make Payment for the generated bill - Payment is towards the installment is made successfully.
 * 29. (2DD6-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill - Installment bill is generated under Bills and Statement section of the Billing tab
 *     Type = "Bill", Date = Installment due date.
 * 30. (2DD6) Make Payment for the generated bill -> Payment is towards the installment is made successfully.
 * 31. (2DD9-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill -> Installment bill is generated under Bills and Statement section of the Billing tab
 *     Type = "Bill", Date = Installment due date.
 * 32. (2R-96) Run the following jobs: Renewal_Offer_Generation_Part2 -> Job run is successful
 * 33. (2DD9) Make Payment for the generated bill -> Payment is towards the installment is made successfully
 * 34. (2R-63) Run the following jobs: Renewal_Offer_Generation_Part1
 * 35. Run aaaMembershipRenewalBatchOrderAsyncJob
 * 36. Open active Policy. Click Renewal link and in Inquiry mode Navigate to Rating Details Reports tab. Validate membership report
 *     -> Membership is in Cancelled  status and report is reordered (Order Date = 2R-63)
 * 37. Run job Renewal_Offer_Generation_Part2 -> Membership validation fails at this renewal for Driver 2.
 *     Reports page - > Membership report  status ->  "Inactive" or other than "Active", order date is updated
 *     #V7. System does not generate Membership Validation letter AHMVXX2 to be sent to the insured. AHDEXX (eValue discount being removed) is generated
 * 38. (2R-45) Run the following jobs: Renewal_Offer_Generation_Part2
 * 39. Retrieve the policy and validate the Renewal Image Status for converted policy -> Status of renewal image is premium calculated
 * 40. (2R-35) Run the following jobs: Renewal_Offer_Generation_Part2, Run aaaDocGen Job -> Job run is successful
 * 41. Retrieve the policy and validate the Renewal Image Status for converted policy -> Status of renewal image is Proposed
 *
 */

@SuppressWarnings("ProblematicWhitespace")
public class ManualHybridConversionAHMVXX2NotGeneratedTest extends PolicyBaseTest {
	private ErrorTab errorTab = new ErrorTab();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@StateList(states = Constants.States.OR)
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Conversions.AUTO_SS)
	public void manualHybridConversionTest(@Optional("OR") String state) {
		LocalDateTime effectiveDate = TimeSetterUtil.getInstance().getPhaseStartTime();
		LocalDateTime renewalDate = effectiveDate.plusDays(40);
		LocalDateTime secondRenewalDate = renewalDate.plusYears(1);
		TestData policyTd = getConversionPolicyDefaultTD().adjust(DriverTab.class.getSimpleName(), getTestSpecificTD("TestData").getTestDataList("DriverTab"))
				.adjust(RatingDetailReportsTab.class.getSimpleName(), getTestSpecificTD("RatingDetailReportsTab"))
				.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), "AAAProductOwned"), getTestSpecificTD("AAAProductOwned")).resolveLinks();
		TestData renewalTd = getManualConversionInitiationTd();
		String previousPolicyNum = renewalTd.getValue(CustomerMetaData
				.InitiateRenewalEntryActionTab.class.getSimpleName(), CustomerMetaData
				.InitiateRenewalEntryActionTab.PREVIOUS_POLICY_NUMBER.getLabel());
		//Set fixed Previous Policy Number to test data
		renewalTd = renewalTd
				.adjust(TestData.makeKeyPath(CustomerMetaData
						.InitiateRenewalEntryActionTab.class.getSimpleName(), CustomerMetaData
						.InitiateRenewalEntryActionTab.PREVIOUS_POLICY_NUMBER.getLabel()), previousPolicyNum);
		mainApp().open();
		String customerNum = createCustomerIndividual();
		//2. Initiate renewal entry, policy effective date today + 40
		initiateRenewal(renewalTd, renewalDate);
		//4. Initiate renewal entry again with the same data in step 2
		SearchPage.openCustomer(customerNum);
		initiateRenewal(renewalTd, renewalDate);
		//5. Error MES-IRE-08 is shown
		ErrorTab errorTab = new ErrorTab();

		assertThat(errorTab.tableTabFormErrors.getRow(1).getCell("Description")
				.getValue()).contains(ErrorEnum.Errors.ERROR_AAA_MES_IRE_08.getMessage());
		//6. Open conversion renewal image in Data gathering mode
		SearchPage.openCustomer(customerNum);
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd(), renewalDate);
		//7. Enter membership number such that status is other than 'Active' (3111111111111121)
		policy.getDefaultView().fillUpTo(policyTd, DocumentsAndBindTab.class, false);
		//9. Add driver available for rating (FirstName/LastName = 'MvrChargeable Activity')
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());
		policy.getDefaultView().getTab(DriverTab.class).fillTab(getTestSpecificTD("Driver_Mvr_Excluded"));
		policy.getDefaultView().getTab(DriverTab.class).submitTab();
		//10. Navigate to Rating Detail Reports tab and order all reports manually.
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//11. Navigate to P&C tab and Calculate Premium
		//12. Navigate to DAR tab, order reports
		//13. Navigate to Drivers tab, change MVR chargeable driver to Excluded
		//14. Navigate to P&C tab and Calculate Premium
		//15. Navigate to Bind tab and Save & Exit renewal
		policy.getDefaultView().fill(getTestSpecificTD("TestData_FinishPolicy"));
		//16. Override errors if necessary
		overrideErrors();
		Tab.buttonBack.click();
		String policyNum = PolicySummaryPage.getPolicyNumber();
		//17. Run the batch jobs (AAAPreRenewalNoticeAsyncJob, Run aaaDocGen Job) -> Check AAADOCGENENTITY table for PRE RENEWAL eventname/package
		JobUtils.executeJob(Jobs.aaaPreRenewalNoticeAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

		assertThat(DocGenHelper.getDocumentsList(policyNum, PRE_RENEWAL).size()).isEqualTo(1);
		//18 (DD0+5 (R-35)) Run the batch job (Renewal_Offer_Generation_Part2, Run aaaDocGen Job)
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		//19 Renewal Image Status should be "Proposed"
		mainApp().open();
		SearchPage.openPolicy(policyNum);

		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		//21 V11 - Pre-Renewal notice form # AAPRN1OR (for Oregon) is NOT generated and NOT found in e-folder
		DocGenHelper.verifyDocumentsGenerated(false, true, policyNum, DocGenEnum.Documents.AAPRN1OR);
		//V12 - Membership validation letter (AHMVXX2) is NOT generated and included in the renewal offer/packet.
		DocGenHelper.verifyDocumentsGenerated(false, true, policyNum, DocGenEnum.Documents.AHMVXX2);
		//V13 - 'Consumer Information Notice' (AHAUXX) is NOT generated (can be generated, depends on multiple conditions in 880-244CL US) ???
		DocGenHelper.verifyDocumentsGenerated(false, true, policyNum, DocGenEnum.Documents.AHAUXX);
		//22. (DD0+20 (R-20)) Run the job (aaaRenewalNoticeBillAsyncJob, aaaDocGen to generate the bill)
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalDate));
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		//24. (DD0+40 (R)) Make the renewal term payment - Payment is successfully made
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(renewalDate));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(renewalDate, BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		//25. (R+1) Run the batch job (PolicyStatusUpdateJob, policyLapsedRenewalProcessAsyncJob)
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(renewalDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		//26. Navigate to Policy Consolidated View - Policy Status = Active
		mainApp().open();
		SearchPage.openPolicy(policyNum);

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		//27. (2DD3-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill - Installment bill is generated under Bills and Statement section of the Billing tab
		//Type = "Bill", Date = Installment due date.
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalDate.plusMonths(3)));
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		assertThat(BillingSummaryPage.tableBillsStatements.getRow(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE,
				BillingHelper.getInstallmentDueDates().get(0).format(DateTimeUtils.MM_DD_YYYY))
				.getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue()).isEqualTo("Bill");
		//28. (2DD3) Make Payment for the generated bill - Payment is towards the installment is made successfully.
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(renewalDate.plusMonths(3)));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		//29. (2DD6-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill.
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalDate.plusMonths(6)));
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		assertThat(BillingSummaryPage.tableBillsStatements.getRow(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE,
				BillingHelper.getInstallmentDueDates().get(0).format(DateTimeUtils.MM_DD_YYYY))
				.getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue()).isEqualTo("Bill");
		//30. (2DD6) Make Payment for the generated bill
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(renewalDate.plusMonths(6)));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		//31. (2DD9-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill.
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalDate.plusMonths(9)));
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		assertThat(BillingSummaryPage.tableBillsStatements.getRow(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE,
				BillingHelper.getInstallmentDueDates().get(0).format(DateTimeUtils.MM_DD_YYYY))
				.getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue()).isEqualTo("Bill");
		//32. (2R-96) Renewal_Offer_Generation_Part2
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(secondRenewalDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		//33. (2DD9) Make Payment for the generated bill
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(renewalDate.plusMonths(9)));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		//34, 35. (2R-63)  Run the following jobs: Renewal_Offer_Generation_Part1, aaaMembershipRenewalBatchOrderAsyncJob
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewReportsDate(secondRenewalDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
		//36. Open active Policy. Click Renewal link and in Inquiry mode Navigate to Rating Details Reports tab. Validate membership report
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.click();
		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
		RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();

		//-> Membership is in Cancelled  status and report is reordered (Order Date = 2R-63)
		assertThat(ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT)
				.getTable().getRow(1).getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.STATUS.getLabel())
				.getValue()).isEqualTo("Cancelled");
		assertThat(ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT)
				.getTable().getRow(1).getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.ORDER_DATE.getLabel())
				.getValue()).isEqualTo(getTimePoints().getRenewReportsDate(secondRenewalDate).format(DateTimeUtils.MM_DD_YYYY));
		//37. Run job Renewal_Offer_Generation_Part2
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		// Membership validation fails at this renewal for Driver 2.
		// Reports page - > Membership report  status ->  "Inactive" or other than "Active", order date is updated
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.click();
		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());

		//-> Membership is in Cancelled  status and report is reordered (Order Date = 2R-63)
		assertThat(ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT)
				.getTable().getRow(1).getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.STATUS.getLabel())
				.getValue()).isEqualTo("Cancelled");
		assertThat(ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT)
				.getTable().getRow(1).getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.ORDER_DATE.getLabel())
				.getValue()).isEqualTo(getTimePoints().getRenewReportsDate(secondRenewalDate).format(DateTimeUtils.MM_DD_YYYY));
		//Verification is in the end of test (needs time for report to be generated)
		//38. (2R-45) Run the following job: Renewal_Offer_Generation_Part2
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(secondRenewalDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		//39. Retrieve the policy and validate the Renewal Image Status for converted policy. Status of renewal image is premium calculated
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.click();

		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		//40 (2R-35) Run the following jobs: Renewal_Offer_Generation_Part2, Run aaaDocGen Job
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(secondRenewalDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		//41  Status of renewal image is Proposed
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.click();

		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		//System does not generate Membership Validation letter AHMVXX2 to be sent to the insured
		DocGenHelper.verifyDocumentsGenerated(false, true, policyNum, DocGenEnum.Documents.AHMVXX2);
		//AHDEXX is generated
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AHDEXX);
	}

	private void initiateRenewal(TestData td, LocalDateTime dateTime) {
		customer.initiateRenewalEntry().start();
		customer.initiateRenewalEntry().getView().fill(td.adjust(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(),
				CustomerMetaData.InitiateRenewalEntryActionTab.RENEWAL_EFFECTIVE_DATE.getLabel()), dateTime.format(DateTimeUtils.MM_DD_YYYY)));
	}

	private void overrideErrors() {
		Tab documentsAndBindTab = policy.getDefaultView().getTab(DocumentsAndBindTab.class);
		errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_200008);
		errorTab.override();
		documentsAndBindTab.submitTab();
		errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_200008);
		errorTab.override();
		documentsAndBindTab.submitTab();
		errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_200034);
		errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_200111);
		errorTab.override();
		documentsAndBindTab.submitTab();
		if (errorTab.tableErrors.isPresent()) {
			errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_MVR_order_validation_SS);
			errorTab.override();
			documentsAndBindTab.submitTab();
		}
	}
}
