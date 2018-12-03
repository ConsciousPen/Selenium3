package aaa.modules.e2e.templates;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;

public class Scenario13 extends ScenarioBaseTest {
	
	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;
	protected String[] endorsementReasonDataKeys;
	
	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	protected LocalDateTime pligaOrMvleFeeLastTransactionDate;
	
	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 11; 
	
	protected List<LocalDateTime> installmentDueDatesAfterEndorsement;
	protected int installmentsCountAfterEndorsement = 7; 
	
	List<LocalDateTime> installmentDueDatesForRenewal;
	protected int installmentsCountForRenewal = 2; 
	
	protected String policyTerm;
	protected Integer totalVehiclesNumber;
	
	protected void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();		
		mainApp().open();
		
		createCustomerIndividual();	
		if (getPolicyType().equals(PolicyType.PUP)) {
			policyCreationTD = new PrefillTab().adjustWithRealPolicies(policyCreationTD, getPrimaryPoliciesForPup());
		}
		policyNum = createPolicy(policyCreationTD);
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		policyTerm = getPolicyTerm(policyCreationTD);
		totalVehiclesNumber = getVehiclesNumber(policyCreationTD);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		//CustomAssert.assertEquals("Billing Installments count for Semi-Annual payment plan", installmentsCount, installmentDueDates.size());
		CustomAssertions.assertThat(installmentDueDates.size()).as("Billing Installments count for Monthly (Eleven Pay) payment plan").isEqualTo(installmentsCount);

		verifyPligaOrMvleFee(TimeSetterUtil.getInstance().getPhaseStartTime(), policyTerm, totalVehiclesNumber);
	}

	protected void generateFirstBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(1), softly);
	}

	protected void payFirstBill() {
		payAndCheckBill(installmentDueDates.get(1));
	}

	protected void deletePendingEndorsement() {
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar totalDueBeforeEndorsement = new Dollar(BillingSummaryPage.getTotalDue());
		BillingSummaryPage.openPolicy(policyEffectiveDate);

		//TestData endorsementTD = getTestSpecificTD("TestData_Endorsement1").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		//policy.endorse().perform(endorsementTD);
		//policy.getDefaultView().fillUpTo(endorsementTD, DocumentsAndBindTab.class);
		//DocumentsAndBindTab.buttonSaveAndExit.click();

		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.endorse().performAndExit(endorsementTD);

		PolicySummaryPage.buttonPendedEndorsement.isEnabled();
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.deletePendedTransaction().perform(new SimpleDataProvider());
		//PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		assertThat(PolicySummaryPage.buttonPendedEndorsement).isEnabled(false);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.getTotalDue().verify.equals(totalDueBeforeEndorsement);

		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(DateTimeUtils.getCurrentDateTime())
				.setPolicy(policyNum).setType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM)
			.setSubtypeReason(reason).verifyPresent(false);
	}

	protected void generateSecondBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(2), softly);
	}

	protected void paySecondBill() {
		payAndCheckBill(installmentDueDates.get(2));
	}

	protected void generateThirdBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(3), softly);
	}

	protected void payThirdBill() {
		payAndCheckBill(installmentDueDates.get(3));
	}

	protected void generateFourthBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(4), softly);
	}

	protected void payFourthBill() {
		payAndCheckBill(installmentDueDates.get(4));
	}

	protected void generateFifthBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(5), softly);
	}

	protected void removeAutoPay() {
		mainApp().open();
		SearchPage.openBilling(policyNum);
		billingAccount.update().perform(tdBilling.getTestData("Update", "TestData_RemoveAutopay"));
		billingAccount.update().start();
		//new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).verify.value(false);
		CustomAssertions.assertThat(new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY)).hasValue(false);
		Tab.buttonCancel.click();
	}

	protected void payFifthBill() {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDates.get(5));
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.aaaRecurringPaymentsProcessingJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(5), BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		//verify recurring payment is not generated
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(DateTimeUtils.getCurrentDateTime())
			.setAmount(minDue.negate())
				.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RECURRING_PAYMENT).verifyPresent(false);

		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), minDue.negate());
	}

	//DD6-21
	protected void changePaymentPlan(ETCSCoreSoftAssertions softly) {
		LocalDateTime endorseDueDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(6)).minusDays(1);
		TimeSetterUtil.getInstance().nextPhase(endorseDueDate);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar totalDueBeforeEndorsement =  new Dollar(BillingSummaryPage.getTotalDue());
		Dollar endorseAmount = new Dollar(0);

		//if (getPolicyType().isCaProduct()) {
		if (getState().equals(Constants.States.CA)) {
			billingAccount.changePaymentPlan().perform("Semi-Annual");
		}
		else {
			BillingSummaryPage.openPolicy(policyEffectiveDate);
			TestData endorsementTD = getTestSpecificTD("TestData_Endorsement").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
			policy.endorse().performAndFill(endorsementTD);
			endorseAmount = new Dollar(PolicySummaryPage.TransactionHistory.getTranPremium());
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		}

		Dollar totalDueAfterEndorsement;
		if (endorseAmount.isNegative()) {
			totalDueAfterEndorsement = new Dollar(totalDueBeforeEndorsement.add(endorseAmount));
		}
		else {
			totalDueAfterEndorsement = new Dollar(totalDueBeforeEndorsement.subtract(endorseAmount));
		}

		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE)
			.setPaymentPlan("Semi-Annual")
			.setTotalDue(totalDueAfterEndorsement).verifyPresent();

		installmentDueDatesAfterEndorsement = BillingHelper.getInstallmentDueDates();
		List<Dollar> installmentDues = BillingHelper.getInstallmentDues();
		softly.assertThat(installmentDueDatesAfterEndorsement.size()).as("Billing Installments count after payment plan changed to Semi-Annual")
			.isEqualTo(installmentsCountAfterEndorsement);
		softly.assertThat(installmentDues.get(6)).as("Last installment amount is incorrect").isEqualTo(totalDueAfterEndorsement);

		if (!getPolicyType().isCaProduct()) {
			if (!getPolicyType().equals(PolicyType.PUP)) {
			TestData endorsementTD = getTestSpecificTD("TestData_Endorsement").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
			String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
			new BillingPaymentsAndTransactionsVerifier().setTransactionDate(endorseDueDate)
				.setPolicy(policyNum)
					.setType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM)
				.setSubtypeReason(reason)
					.setAmount(endorseAmount).verifyPresent();
			}
		}
	}

	protected void generateSixthBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(6), softly);
	}

	protected void paySixthBill() {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDates.get(6));
		TimeSetterUtil.getInstance().nextPhase(billDueDate);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(6), BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		minDue = minDue.subtract(new Dollar(4.99));
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), minDue.negate());
	}

	protected void smallBalanceGeneration(ETCSCoreSoftAssertions softly) {
		LocalDateTime smallBalanceGenDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1);
		TimeSetterUtil.getInstance().nextPhase(smallBalanceGenDate);
		JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);

		Dollar amount = new Dollar(4.99);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingPaymentsAndTransactionsVerifier(softly).setTransactionDate(smallBalanceGenDate)
			.setAmount(amount.negate())
				.setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.SMALL_BALANCE_WRITE_OFF)
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED).verifyPresent();

		softly.assertThat(BillingSummaryPage.getTotalDue()).as("Total Due is not $0.00").isEqualTo(new Dollar(0));
		softly.assertThat(BillingSummaryPage.getMinimumDue()).as("Min Due is not $0.00").isEqualTo(new Dollar(0));

	}

	protected void cancelNoticeNotGenerated() {
		LocalDateTime cancelNoticeDate = getTimePoints().getCancellationNoticeDate(installmentDueDates.get(6));
		TimeSetterUtil.getInstance().nextPhase(cancelNoticeDate);
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		//PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyFlag(BillingConstants.PolicyFlag.DEFAULT).verifyRowWithEffectiveDate(policyEffectiveDate);
	}

	protected void cancellationNotGenerated() {
		LocalDateTime cancelDate = getTimePoints().getCancellationDate(installmentDueDates.get(6));
		TimeSetterUtil.getInstance().nextPhase(cancelDate);
		JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		//PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();
	}

	protected void renewalImageGeneration() {
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewImageGenDate);
	}

	protected void renewalPreviewGeneration() {
		LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	protected void renewalOfferGeneration(ETCSCoreSoftAssertions softly) {
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE)
				.setPaymentPlan("Semi-Annual").verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED)
				.setPaymentPlan("Semi-Annual (Renewal)").verifyRowWithEffectiveDate(policyExpirationDate);
		BillingSummaryPage.buttonHidePriorTerms.click();

		installmentDueDatesForRenewal = BillingHelper.getInstallmentDueDates();
		softly.assertThat(installmentDueDatesForRenewal.size()).as("Billing Installments count for renewal is incorrect")
			.isEqualTo(installmentsCountForRenewal);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferGenDate)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();

		if (getState().equals(Constants.States.CA)) {
			verifyCaRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), installmentsCountForRenewal, softly);
		}

		if (verifyPligaOrMvleFee(renewOfferGenDate, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewOfferGenDate;
		}

	}

	//Skip this step for CA
	protected void generateRenewalBill() {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		Dollar pligaOrMvleFee = getPligaOrMvleFee(policyNum, pligaOrMvleFeeLastTransactionDate, policyTerm, totalVehiclesNumber);
		verifyRenewPremiumNotice(policyExpirationDate, billDate, pligaOrMvleFee);
	}

	protected void createRenewalVersion() {
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar renewalBillAmount = new Dollar(BillingHelper.getBillCellValue(policyExpirationDate, BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		BillingSummaryPage.openPolicy(policyExpirationDate);

		TestData createVersionTD = getTestSpecificTD("TestData_CreateVersion");

		PolicySummaryPage.buttonRenewals.click();
		policy.policyInquiry().start();

		if (getPolicyType().isAutoPolicy()) {
			if (getPolicyType().isCaProduct()) {
				new aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab().createVersion();
				NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
				aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab premiumTab = new aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab();
				premiumTab.fillTab(createVersionTD);
				premiumTab.calculatePremium();
				NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
				new aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab().submitTab();
			}
			else {
				new GeneralTab().createVersion();
				NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
				new PremiumAndCoveragesTab().fillTab(createVersionTD);
				new PremiumAndCoveragesTab().calculatePremium();
				NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
				new DocumentsAndBindTab().submitTab();
			}
		}
		if (!getPolicyType().isAutoPolicy()) {
			if (getPolicyType().isCaProduct()) {
				new aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab().createVersion();
				NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
				new aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab().fillTab(createVersionTD);
				NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
				NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
				new aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab().fillTab(createVersionTD);
				new aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab().calculatePremium();
				NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
				new aaa.main.modules.policy.home_ca.defaulttabs.BindTab().submitTab();
			}
			else if (getPolicyType().equals(PolicyType.PUP)) {
				new PrefillTab().createVersion();
				NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS.get());
				NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
				new UnderlyingRisksAutoTab().fillTab(createVersionTD);
				NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
				NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
				//new aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab().fillTab(createVersionTD);
				new PremiumAndCoveragesQuoteTab().calculatePremium();
				NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
				new aaa.main.modules.policy.pup.defaulttabs.BindTab().submitTab();
			}
			else {
				new aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab().createVersion();
				NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
				new PropertyInfoTab().fillTab(createVersionTD);
				NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
				NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
				new PremiumsAndCoveragesQuoteTab().fillTab(createVersionTD);
				new PremiumsAndCoveragesQuoteTab().calculatePremium();
				NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
				new BindTab().submitTab();
			}
		}

		PolicySummaryPage.buttonRenewalQuoteVersion.isEnabled();
		PolicySummaryPage.buttonRenewalQuoteVersion.click();
		Dollar premiumNewVersion = PolicySummaryPage.TransactionHistory.readEndingPremium(1);
		Dollar premiumFirstRenewal = PolicySummaryPage.TransactionHistory.readEndingPremium(2);
		PolicySummaryPage.buttonQuoteOverview.click();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(DateTimeUtils.getCurrentDateTime())
				.setType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL)
			.setAmount(premiumNewVersion.subtract(premiumFirstRenewal)).verifyPresent();

		if (getState().equals(Constants.States.CA)) {
			new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.OFFER)
			.setDueDate(policyExpirationDate)
			.setMinDue(renewalBillAmount).verifyPresent();
		}
		else {
			new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL)
			.setDueDate(policyExpirationDate)
			.setMinDue(renewalBillAmount).verifyPresent();
		}

		BillingSummaryPage.getMinimumDue().verify.equals(renewalBillAmount);
	}

	protected void payRenewalBill() {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(policyExpirationDate);
		if (getState().equals(Constants.States.CA)) {
			billDueDate = policyExpirationDate; //avoid switch to Monday, Renewal bill should be payed before policyStatusUpdateJob
		}
		TimeSetterUtil.getInstance().nextPhase(billDueDate);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(policyExpirationDate, BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), minDue.negate());
	}

	protected void updatePolicyStatus() {
		LocalDateTime updateStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(updateStatusDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}
}
