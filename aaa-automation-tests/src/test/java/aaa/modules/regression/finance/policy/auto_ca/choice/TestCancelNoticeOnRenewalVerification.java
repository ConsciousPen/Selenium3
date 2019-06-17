package aaa.modules.regression.finance.policy.auto_ca.choice;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
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
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.PolicyOperations;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

public class TestCancelNoticeOnRenewalVerification extends PolicyOperations {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	/**
	 * @author Reda Kazlauskiene
	 * Objectives : Cancel Notice On Policy
	 * Preconditions:
	 * 1. Create Monthly Auto CA choice Policy with Effective date today
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
				.adjust("PremiumAndCoveragesTab|Payment Plan", BillingConstants.PaymentPlan.STANDARD_MONTHLY).resolveLinks();
		String policyNumber = createPolicy(policyTD);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		log.info("TEST: Cancel Notice for Policy #" + policyNumber);
		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData"));
		assertThat(PolicySummaryPage.labelCancelNotice).isPresent();
		createRenewalOffer(policyNumber, expirationDate);
		assertThat(PolicySummaryPage.buttonRenewals).isDisabled();

		policy.renew().start();
		assertThat(Page.dialogConfirmation.labelMessage.getValue()).contains(expectedMessage);
		Page.dialogConfirmation.buttonCancel.click();

		policy.deleteCancelNotice().perform(new SimpleDataProvider());
		assertThat(PolicySummaryPage.labelCancelNotice).isPresent(false);
		renewalOfferGeneration(policyNumber, expirationDate);
	}

	/**
	 * @author Reda Kazlauskiene
	 * Objectives : Cancel Notice On Renewal
	 * Preconditions:
	 * 1. Create Monthly Auto CA Choice Policy with Effective date today
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
				.adjust("PremiumAndCoveragesTab|Payment Plan", BillingConstants.PaymentPlan.STANDARD_MONTHLY).resolveLinks();
		String policyNumber = createPolicy(policyTD);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		//Verify Cancel Notice when Renewal status Data Gathering
		renewalImageGeneration(policyNumber, expirationDate);

		log.info("TEST: Cancel Notice for Policy #" + policyNumber);
		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData"));
		assertThat(PolicySummaryPage.labelCancelNotice).isPresent();
		createRenewalOffer(policyNumber, expirationDate);

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.DATA_GATHERING).verify(1);
		Tab.buttonBack.click();

		//Proposed button should be disabled when Cancel Notice flag exists Renewal status Data Gathering)
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
	 * 1. Create Monthly Auto Ca Choice Policy with Effective date today
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
				.adjust("PremiumAndCoveragesTab|Payment Plan", BillingConstants.PaymentPlan.STANDARD_MONTHLY).resolveLinks();
		String policyNumber = createPolicy(policyTD);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		//Verify Cancel Notice when Renewal status Premium Calculated
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

	private void verifyProposedButton() {
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
		assertThat(new DocumentsAndBindTab().btnPurchase).isEnabled(false);
	}
}
