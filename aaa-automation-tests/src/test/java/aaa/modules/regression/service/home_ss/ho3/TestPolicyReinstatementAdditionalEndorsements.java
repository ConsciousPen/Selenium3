package aaa.modules.regression.service.home_ss.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.*;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.datetime.DateTimeUtils;

/**
 * @author tsaltsevich
 * @name TestPolicyReinstatementAdditionalEndorsements ("CL-CP-100")
 *
 * Test Case Description:
 * 1. Policy cancelled due to UW request (Material misrepresentation)
 * 2. Reinstate the policy without lapse and check Cancellation Notice Withdrawn (AHCWXX) document is generated
 * 3. Policy cancelled due Insured's Request - Autopay withdrawal request for the balance/earned premium amount
 * 4. Reinstate the policy with lapse and check Reinstatement Notice Lapse (AH62XX 0316 ) document is generated
 * 5. Renew the policy without lapse
 *
 **/

public class TestPolicyReinstatementAdditionalEndorsements extends HomeSSHO3BaseTest {
	@Parameters({"state"})
	@StateList(states = Constants.States.UT)
	@Test(groups = {Groups.TIMEPOINT, Groups.REGRESSION, Groups.MEDIUM})
	public void testPolicyReinstatementAdditionalEndorsements(@Optional("UT") String state) {
		ErrorTab errorTab = new ErrorTab();
		List<LocalDateTime> installmentDueDates;
		LocalDateTime billGenDate;
		LocalDateTime billDueDate;
		LocalDateTime policyEffectiveDate;
		String policyNum;
		ReportsTab reportsTab = new ReportsTab();
		PremiumsAndCoveragesQuoteTab premiumQuoteTab = policy.getDefaultView().getTab(PremiumsAndCoveragesQuoteTab.class);
		EndorsementTab endorsementTab = new EndorsementTab();

		mainApp().open();
		createCustomerIndividual();
		//1.(DD0)
		log.info("TEST: Create policy with Recurring Payment, ACH Payment and HS 09 88 Additional Insured – Special Event information");
		policyNum = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData")));
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		log.info("TEST: #L Policy status is Policy Active");
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		SearchPage.openBilling(policyNum);
		installmentDueDates = BillingHelper.getInstallmentDueDates();

		//2.(DD1)
		LocalDateTime dd1 = installmentDueDates.get(0).plusMonths(1);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(dd1));
		mainApp().open();
		log.info("TEST: Retrieve the Policy and Initiate an endorsement");
		SearchPage.openPolicy(policyNum);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
		log.info("TEST: Add the HS 09 04 Structural Alteration Coverage endorsement");
		policy.getDefaultView().fill(getTestSpecificTD("TestData_FinishEndorsement"));
		errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_HO_UWApp_HS0988_endorsement);
		errorTab.override();
		policy.getDefaultView().getTab(BindTab.class).submitTab();

		//3.(DD1+5)
		TimeSetterUtil.getInstance().nextPhase(dd1.plusDays(5));
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		log.info("TEST: Cancel Notice for Policy #" + policyNum + ". Cancellation Reason = Material Misrepresentation (Note: UW initiated cancellation)");
		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData_MaterialMisrepresentation"));
		log.info("TEST: #L Cancel Notice Flag is set on policy");
		assertThat(PolicySummaryPage.labelCancelNotice).isPresent();
		log.info("TEST: Run aaDOCgen batch job");
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		log.info("TEST: #V Cancellation Notice AH61XX is archived in fast lane and available under E-Folder--> Cancellation");
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH61XX);
		log.info("TEST: Delete Cancel Notice for Policy #" + policyNum);
		policy.deleteCancelNotice().perform(new SimpleDataProvider());
		log.info("TEST: Cancel Notice flag is removed from policy");
		assertThat(PolicySummaryPage.labelCancelNotice).isPresent(false);

		//4.(DD2-20)
		LocalDateTime dd2 = installmentDueDates.get(1);
		billGenDate = getTimePoints().getBillGenerationDate(dd2);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		log.info("TEST: aaaBillingInvoiceAsyncTaskJob to generate the Installment Bill (If payment plan= Eleven Pay Standard)");
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		log.info("TEST: #L Installment bill is generated under Bills and Statement section of the Billing tab Type = Bill, Date = Installment due date.");
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(dd2, billGenDate, policyEffectiveDate, BillingHelper.DZERO);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();
		SearchPage.openPolicy(policyNum);
		log.info("TEST: Cancel Policy #" + policyNum + ". Cancellation Reason = Insured Request Cancel, Cancellation Effective Date= Current Date +20");
		LocalDateTime cancellationDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(20);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData")
				.adjust(TestData.makeKeyPath("CancellationActionTab", "Cancellation effective date"), cancellationDate.format(DateTimeUtils.MM_DD_YYYY)));
		log.info("TEST: #L Status= Pending Cancellation");
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CANCELLATION_PENDING);

		//5.(DD2-19)
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dd2).plusDays(1));
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		log.info("TEST: Endorse policy");
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		policy.getDefaultView().fill(getTestSpecificTD("TestData_Empty_Endorsement"));
		log.info("TEST: #L Status of the Policy is Pending Out of sequence Completion");
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
		log.info("TEST: Select 'Roll on Changes' from Slect action drop down. Press 'Roll on' link");
		policy.rollOn().perform(true, true);
		log.info("TEST: #L Status of the Policy is Cancellation Pending");
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CANCELLATION_PENDING);

		//6.(DD2)
		billDueDate = getTimePoints().getBillDueDate(dd2);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		log.info("TEST: Run the Following Batch jobs: a. changeCancellationPendingPoliciesStatus b. Run the aaaRecurringPaymentsProcessingJob");
		JobUtils.executeJob(Jobs.aaaRecurringPaymentsProcessingJob);
		JobUtils.executeJob(Jobs.changeCancellationPendingPoliciesStatus);
		mainApp().open();
		log.info("TEST: #L Bill & Statements (cancellation) CHECK");
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.CANCELLATION)
				.setDueDate(cancellationDate).verifyPresent();
		log.info("TEST: #V Auto pay withdrawal request for the balance/earned premium amount triggered on the cancellation effective date");
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDueDate)
				.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RECURRING_PAYMENT)
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.ISSUED).verifyPresent();
		log.info("TEST: #L Status = Cancelled");
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		//7.(DD3-20)
		LocalDateTime dd3 = dd2.plusMonths(1);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dd3));
		mainApp().open();
		log.info("TEST: retrieve the policy and select 'Reinstatement', Reinstate Date=CED = DD2 (Note: Reinstating without Lapse)");
		SearchPage.openPolicy(policyNum);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData")
				.adjust(TestData.makeKeyPath("ReinstatementActionTab", "Reinstate Date"),
						getTimePoints().getBillDueDate(dd2).format(DateTimeUtils.MM_DD_YYYY)));
		log.info("TEST: #L Policy is reinstated WITHOUT lapse and status= Active");
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: #V - Cancellation Notice Withdrawn AHCWXX document is archived");
		log.info("TEST: in Fast lane and available in the Policy E-folder under Cancellation & Rescission and Reinstatement folder");
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AHCWXX);
		log.info("TEST: select 'Cancellation' from move to drop down");
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		log.info("TEST: Run policyStatusUpdateJob");
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		log.info("TEST: #L Status = Cancelled");
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		//8.(DD3)
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(dd3));
		mainApp().open();
		log.info("TEST:  Кetrieve the policy and Reinstate, 'Reinstate Date' = Current Date");
		SearchPage.openPolicy(policyNum);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
		log.info("TEST: #L Policy is reinstated with lapse and status = Active");
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: #V Reinstatement Notice Lapse AH62XX 0316 is archived in Fastlane and available in the Policy E-folder.");
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH62XX);

		//9.(R-73)
		LocalDateTime renewalDate = installmentDueDates.get(0).plusYears(1);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(renewalDate));
		log.info("TEST: Run the Following Batch Jobs: Renewal_Offer_Generation_Part1, Renewal_Offer_Generation_Part2");
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		HttpStub.executeAllBatches();
		log.info("TEST: #V Renewal process has been initiated");
		log.info("TEST: #L Current term policy status is 'Policy Active'");
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: #V The renewal link is available only for users with 'Early Policy Renewal Inquiry'");
		PolicySummaryPage.buttonRenewals.click();
		log.info("TEST: #V The Policy status on to the Renewal image is updated to 'Gathering info'");
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.DATA_GATHERING).verify(1);
		log.info("TEST: On the Renewal screen start 'Inquiry'");
		policy.policyInquiry().start();
		log.info("TEST: Navigate to the Endorsement sub tab under P&C");
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());
		log.info("TEST: #V Structural alteration HS 09 04 and Special event endorsement HS 09 88 are not available in the 'selected and included endorsements' section");
		Map<String, String> HS_04_20 = new HashMap<>();
		HS_04_20.put("Form ID", "HS 04 20");
		HS_04_20.put("Name", "Specified Increased Insurance Limit For Coverage A – Dwelling");
		Map<String, String> HS_04_90 = new HashMap<>();
		HS_04_90.put("Form ID", "HS 04 90");
		HS_04_90.put("Name", "Personal Property Replacement Cost Loss Settlement");
		assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(HS_04_20)).isPresent();
		assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(HS_04_90)).isPresent();

		//10.(R-63)
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewReportsDate(renewalDate));
		log.info("TEST: Run the Following Batch Job - aaaMembershipRenewalBatchOrderAsyncJob");
		JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
		HttpStub.executeAllBatches();
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.click();
		policy.policyInquiry().start();
		log.info("TEST: #V On the reports page validate that the Membership report has been ordered and membership status is 'Active'");
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.REPORTS.get());
		assertThat(reportsTab.tblAAAMembershipReport.getRow(1).getCell(HomeSSMetaData.ReportsTab.AaaMembershipReportRow.STATUS.getLabel()).getValue())
				.isEqualToIgnoringCase("Active");

		//11.(R-48)
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCheckUWRules(renewalDate));
		log.info("TEST: Run the Following Batch Job - aaaMembershipRenewalBatchOrderAsyncJob");
		JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
		HttpStub.executeAllBatches();
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.click();
		policy.policyInquiry().start();
		log.info("TEST: On the reports page validate that the Membership report has NOT been ordered again");
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.REPORTS.get());
		assertThat(reportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT)
				.getTable().getRow(1).getCell(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.ORDER_DATE.getLabel())
				.getValue()).isNotEqualToIgnoringWhitespace(getTimePoints().getRenewReportsDate(getTimePoints()
				.getRenewCheckUWRules(renewalDate)).format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));

		//12.(R-45)
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(renewalDate));
		log.info("TEST: Run the Batch Job Renewal_Offer_Generation_Part2");
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		HttpStub.executeAllBatches();
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.click();
		log.info("TEST: #V The Policy status on to the Renewal image is updated to 'Premium Calculated'");
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		policy.policyInquiry().start();
		log.info("TEST: Navigate to the quote sub tab under P&C and validate that the policy is rated and Discounts section");
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		log.info("TEST: #V Renewal premium is calculated and displayed under 'Premium Summary' section");
		Dollar actualPremium = new Dollar(PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getColumn(2).getValue().get(0));
		assertThat(actualPremium).isNotNull();
		log.info("TEST: #V System should provide AAA Membership discount.");
		assertThat(premiumQuoteTab.isDiscountApplied("AAA Membership")).isTrue();
		log.info("TEST: Click on 'View Rating Details' link and validate the discounts section");
		premiumQuoteTab.openViewRatingDetails();
		assertThat(PropertyQuoteTab.RatingDetailsView.discounts.getValueByKey("Member persistancy")).isNotNull();
		PropertyQuoteTab.RatingDetailsView.close();
		log.info("TEST: Exit the Inquiry mode of the renewal image and Initiate an endorsement on the current term");
		SearchPage.openPolicy(policyNum);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		log.info("TEST: Override the premium by a flat dollar or % amount. System refreshes the P&C screen with the overriden value(s) (rounded to nearest whole dollar amount)");
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		PropertyQuoteTab.btnCalculatePremium.click();
		premiumQuoteTab.fillTab(getTestSpecificTD("TestData_Override"), true);
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().submitTab();
		log.info("TEST: Click on the Renewal link available on the policy consolidated page. Start Inquiry");
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		log.info("TEST: Override premium");
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumQuoteTab.calculatePremium();
		Dollar totalPremium = PropertyQuoteTab.getPolicyTermPremium();
		PropertyQuoteTab.btnOverridePremium.click();
		log.info("TEST: #V Validate that the system does not apply flat dollar or % amount override (ie. System does NOT copy the Current term Overridden Premium onto the Renewal image)");
		assertThat(PremiumsAndCoveragesQuoteTab.getOverridenPremiumFlatAmount().isZero()).isTrue();
		assertThat(PremiumsAndCoveragesQuoteTab.getOverridenPremiumPercentageAmount()).isEqualTo(0);
		assertThat(PremiumsAndCoveragesQuoteTab.getFinalTermPremium()).isEqualTo(totalPremium);
		PremiumsAndCoveragesQuoteTab.dialogOverridePremium.reject();

		//13.(R-40)
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(renewalDate).plusDays(5));
		//Click on the Renewal link and validate the status of the Renewal image on the renewal screen.
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		//On the Renewal screen, select "Data Gathering" from the dropdown and click on "Go" to be able to edit the renewal image.
		policy.dataGather().start();
		//Navigate to the Endorsement subtab under P&C
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
		log.info("TEST: Add the HS 04 59 Assisted Living Care Coverage endorsement");
		//Bind the Endorsement
		policy.getDefaultView().fill(getTestSpecificTD("TestData_AddEndorsementAndBind"));

		//14.(R-35)
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalDate));
		log.info("TEST: Run the Following Batch  Jobs: Renewal_Offer_Generation_Part2, aaaDocGenBatchJob");
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		//Click on the Renewal link and validate the status of the Renewal image on the renewal screen.
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.click();
		log.info("TEST: #V The Policy status on to the Renewal image is updated to 'Proposed'");
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		//Navigate back to the Policy consolidated screen and validate the renewal e-folder under fast lane
		//#V: The Endorsement Forms and Renewal Dec page should be archived in Fast lane and available in the Renewal EFolder");
		log.info("TEST: HS 04 59 Assisted Living Care Coverage endorsement should be generated");
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.HS_04_59);

		//15.(R-20)
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalDate));
		log.info("TEST: Run the Batch Job aaaRenewalNoticeBillAsyncJob");
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		//3. Navigate to the Billing Tab.
		log.info("TEST: #V System updates the Bills & Statements Section with following information Renewal Term");
		//a. Date - Renewal effective date
		//b. Type - 'Bill'
		//c. Statement # - <Statement number>
		//d. Minimum Due - value reflected as Minimum amount due on renewal premium notice = Prior TermBalance (if any) + Fees (if any) + Renewal Amount Due
		//e. Past Due - Zero
		//f. Total Due - Total Renewal Term Premium + Fees (If any)
		new BillingBillsAndStatementsVerifier()
				.setType(BillingConstants.BillsAndStatementsType.BILL)
				.setDueDate(renewalDate)
				.setMinDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())
						.add(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(2).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())))
				.setPastDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAST_DUE).getValue()))
				.setTotalDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue())
						.add(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(2).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue())))
				.verifyPresent();
		log.info("TEST: #V System inserts a record for Installment Fee/Recurring Fee in Payments & Other Transaction section of Billing screen");
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillGenerationDate(renewalDate))
				.setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.EFT_INSTALLMENT_FEE)
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED).verifyPresent();
		//Navigate back to the Policy consolidated view and validate the Renewal E-folder under Fast lane.
		log.info("TEST: #V Renewal Bill AHRBXX 03 16 is archived in Fastlane and available in the Policy E-folder under Renewal");
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AHRBXX);

		//16.(R-10)
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getOffcycleBillGenerationDate(renewalDate));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar totalDueRenewalTerm = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue());
		Dollar totalDueCurrentTerm = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(2).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue());
		//Click on the "Accept Payment" link and apply payment for the renewal bill along with $100 paid additionally. (ie. Min due of renewal bill + $100)
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(renewalDate, BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE)).add(100);
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		//#L Billing Account Policies tab:
		// a. Renewal Term
		// • Total Due is decreased by the credit Amount (Total Due = Billable amount - Total Paid)
		// • Total Paid is increased by the credit Amount (Total Paid = default payment amount -100$ - min due (Current term))
		// • Prepaid = (100)
		Dollar manualPaymentValue = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions
				.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, "Manual Payment")
				.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT)
				.getValue());
		log.info("TEST: #V System accepts the payment successfully and updates the status of the renewal quote to 'Policy Pending'");
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_PENDING)
				.setTotalDue(totalDueRenewalTerm.add(totalDueCurrentTerm).add(manualPaymentValue))
				.setTotalPaid(manualPaymentValue.add(totalDueCurrentTerm).abs())
				.setPrepaid(new Dollar(100).negate())
				.verifyPresent();
		// Current Term
		// • Total Due = $0
		// • Total Paid = Billable amount
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier()
				.setTotalDue(new Dollar(0))
				.setTotalPaid(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(2).getCell(BillingConstants.BillingAccountPoliciesTable.BILLABLE_AMOUNT).getValue()))
						.verifyPresent();

		//17.(R+1)
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(renewalDate));
		log.info("TEST: Run the Batch Jobs: PolicyStatusUpdateJob, policyLapsedRenewalProcessAsyncJob");
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		log.info("TEST: #V Renewed Policy Status ='Policy Active'");
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);
		log.info("TEST: #L Policy Status ='Policy Expired'");
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_EXPIRED).verify(2);
		log.info("TEST: #V Renewal Term installment schedule is updated with the credit amount based on the 'overpaid amount' option ( next installment/installments - $100 overpaid) selected for the policy");
		BillingSummaryPage.hidePriorTerms();
		new BillingInstallmentsScheduleVerifier()
				.setScheduleDueAmount(new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(3)
						.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT)
						.getValue()).divide(2).subtract(new Dollar(100)))
				.verify(2);
	}
}