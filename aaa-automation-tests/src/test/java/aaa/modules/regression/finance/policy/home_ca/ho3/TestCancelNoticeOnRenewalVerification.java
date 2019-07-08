package aaa.modules.regression.finance.policy.home_ca.ho3;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;

import aaa.helpers.billing.BillingHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.PolicyOperations;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

public class TestCancelNoticeOnRenewalVerification extends PolicyOperations {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	/**
	 * @author Reda Kazlauskiene
	 * Objectives : Cancel Notice On Policy
	 * Preconditions:
	 * 1. Create Monthly Home CA Policy with Effective date today
	 * 2. Add Cancel Notice Flag
	 * 3. Create renewal proposal
	 * 4. Expected results: Renewal proposal should not be created
	 * 5. Propose renewal manually
	 * 6. Expected results: Propose button is disabled
	 * 7. Remove Cancel Notice Flag
	 * 8. Create renewal proposal
	 * 9. Expected results: Renewal proposal should be created successfully
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-30649")
	public void pas30649_testCancelNoticeOnPolicy(@Optional("CA") String state) {
		String expectedMessage = "Pended Cancellation Exists.";

		mainApp().open();
		createCustomerIndividual();
		TestData policyTD = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData")
				.adjust("PremiumsAndCoveragesQuoteTab|Payment Plan", BillingConstants.PaymentPlan.MONTHLY_STANDARD).resolveLinks();
		String policyNumber = createPolicy(policyTD);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		log.info("TEST: Cancel Notice for Policy #" + policyNumber);
		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData"));
		assertThat(PolicySummaryPage.labelCancelNotice).isPresent();
		LocalDateTime renewDateImage = getTimePoints().getRenewImageGenerationDate(expirationDate);
		createRenewalImage(policyNumber, expirationDate, renewDateImage);
		assertThat(PolicySummaryPage.buttonRenewals).isDisabled();

		policy.renew().start();
		assertThat(Page.dialogConfirmation.labelMessage.getValue()).contains(expectedMessage);
		Page.dialogConfirmation.buttonCancel.click();
		policy.deleteCancelNotice().perform(new SimpleDataProvider());
		assertThat(PolicySummaryPage.labelCancelNotice).isPresent(false);

		renewalImageGeneration(policyNumber, expirationDate);
		renewalPreviewGeneration(policyNumber, expirationDate);
		renewalOfferGeneration(policyNumber, expirationDate);
	}

	/**
	 * @author Reda Kazlauskiene
	 * Objectives : Cancel Notice On Renewal
	 * Preconditions:
	 * 1. Create Monthly Home CA Policy with Effective date today
	 * 2. Create Renewal in Data Gathering status
	 * 2. Add Cancel Notice Flag
	 * 3. Create renewal proposal
	 * 4. Expected results: Renewal proposal should not be created
	 * 5. Propose renewal manually
	 * 6. Expected results: Propose button is disabled
	 * 7. Remove Cancel Notice Flag
	 * 8. Create renewal proposal
	 * 9. Expected results: Renewal proposal should be created successfully
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-30649")
	public void pas30649_testCancelNoticeOnRenewalDataGathering(@Optional("CA") String state) {

		mainApp().open();
		createCustomerIndividual();
		TestData policyTD = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData")
				.adjust("PremiumsAndCoveragesQuoteTab|Payment Plan", BillingConstants.PaymentPlan.MONTHLY_STANDARD).resolveLinks();
		String policyNumber = createPolicy(policyTD);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		//Verify Cancel Notice when Renewal status Data Gathering
		renewalImageGeneration(policyNumber, expirationDate);

		log.info("TEST: Cancel Notice for Policy #" + policyNumber);
		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData"));
		assertThat(PolicySummaryPage.labelCancelNotice).isPresent();
		createRenewalPreviewGeneration(policyNumber, expirationDate);

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.DATA_GATHERING).verify(1);
		Tab.buttonBack.click();

		//Proposed button should be disabled when Cancel Notice flag exists Renewal status Data Gathering)
		createRenewalOffer(policyNumber, expirationDate);
		verifyProposedButton();
		Tab.buttonCancel.click();
		Page.dialogConfirmation.confirm();

		policy.deleteCancelNotice().perform(new SimpleDataProvider());
		assertThat(PolicySummaryPage.labelCancelNotice).isPresent(false);
		renewalOfferGeneration(policyNumber, expirationDate);
	}

	/**
	 * @author Reda Kazlauskiene
	 * Objectives : Cancel Notice On Renewal
	 * Preconditions:
	 * 1. Create Monthly Home CA Policy with Effective date today
	 * 2. Create Renewal in Premium calculated status
	 * 2. Add Cancel Notice Flag
	 * 3. Create renewal proposal
	 * 4. Expected results: Renewal proposal should not be created
	 * 5. Propose renewal manually
	 * 6. Expected results: Propose button is disabled
	 * 7. Remove Cancel Notice Flag
	 * 8. Create renewal proposal
	 * 9. Expected results: Renewal proposal should be created successfully
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-30649")
	public void pas30649_testCancelNoticeOnRenewalPremiumCalculated(@Optional("CA") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData policyTD = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData")
				.adjust("PremiumsAndCoveragesQuoteTab|Payment Plan", BillingConstants.PaymentPlan.MONTHLY_STANDARD).resolveLinks();
		String policyNumber = createPolicy(policyTD);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		//Verify Cancel Notice when Renewal status Premium Calculated
		renewalImageGeneration(policyNumber, expirationDate);
		renewalPreviewGeneration(policyNumber, expirationDate);
		Tab.buttonBack.click();

		log.info("TEST: Cancel Notice for Policy #" + policyNumber);
		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData"));
		assertThat(PolicySummaryPage.labelCancelNotice).isPresent();

		createRenewalOffer(policyNumber, expirationDate);

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		Tab.buttonBack.click();

		//Proposed button should be disabled when Cancel Notice flag exists Renewal status Premium Calculated)
		verifyProposedButton();
		Tab.buttonCancel.click();
		Page.dialogConfirmation.confirm();

		policy.deleteCancelNotice().perform(new SimpleDataProvider());
		assertThat(PolicySummaryPage.labelCancelNotice).isPresent(false);
		renewalOfferGeneration(policyNumber, expirationDate);
	}

	/**
	 * @author Andrii Voievodin
	 * Objectives : Cancel Notice On Renewal
	 * Preconditions:
	 * 1. Create Policy
	 * 2. Make payment - for 9 instalm.
	 * 3. At R-45 Create Renewal: Status - Premium calculated
	 * 4. At R-40 Add Cancel Notice
	 * 5. Proposed renewal - renewal should be in Premium Calculated status, because Cancel Notice is added
	 * 6. At R-30 Make payment - Min due - Cancel Notice Flag is removed
	 * 7. Propose renewal - Renewal is successfully proposed
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-31941")
	public void pas31941_testCancelNoticeOnRenewalProposeRenewal(@Optional("CA") String state) {
		//Create Policy
		mainApp().open();
		createCustomerIndividual();
		TestData policyTD = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData")
				.adjust("PremiumsAndCoveragesQuoteTab|Payment Plan", BillingConstants.PaymentPlan.MONTHLY_STANDARD).resolveLinks();
		String policyNumber = createPolicy(policyTD);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		//Make payment - for 9 instalm.
		BillingSummaryPage.open();
		List<Dollar> installmentAmounts = BillingHelper.getInstallmentDues();
		Dollar paymentAmount = BillingHelper.DZERO;
		for (int i = 1; i < 8; i++) {
			paymentAmount = paymentAmount.add(installmentAmounts.get(i));
		}
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), paymentAmount);
		billingAccount.generateFutureStatement().perform();

		//At R-45 Create Renewal: Status - Premium calculated
		renewalPreviewGeneration(policyNumber, expirationDate);
		Tab.buttonBack.click();

		//At R-40 Add Cancel Notice
		TimeSetterUtil.getInstance().nextPhase(expirationDate.minusDays(40));
		searchForPolicy(policyNumber);

		log.info("TEST: Cancel Notice for Policy #" + policyNumber);
		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData"));
		assertThat(PolicySummaryPage.labelCancelNotice).isPresent();

		//Proposed renewal - renewal should be in Premium Calculated status, because Cancel Notice is added
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		searchForPolicy(policyNumber);

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		Tab.buttonBack.click();

		//At R-30 Make payment - Min due - Cancel Notice Flag is removed
		TimeSetterUtil.getInstance().nextPhase(expirationDate.minusDays(30));
		searchForPolicy(policyNumber);
		BillingSummaryPage.open();
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), BillingHelper.getPolicyMinimumDueAmount(policyNumber));

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());
		PolicySummaryPage.tableSelectPolicy.getRow(1).getCell(1).controls.links.get(1).click();
		assertThat(PolicySummaryPage.labelCancelNotice).isAbsent();

		//Propose renewal - Renewal is successfully proposed
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		searchForPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
	}

	private void verifyProposedButton() {
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		assertThat(new BindTab().btnPurchase).isEnabled(false);
	}
}
