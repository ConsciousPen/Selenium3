package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.google.common.collect.ImmutableList;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;

public class TestRestrictedPaymentPlan extends AutoSSBaseTest {

	private static final ImmutableList<String> ALL_PAYMENT_PLANS= ImmutableList.of(
			"Annual",
			"Semi-Annual",
			"Quarterly",
			"Eleven Pay - Standard",
			"Eleven Pay - Zero Down",
			"Eleven Pay - Low Down",
			"Monthly - Zero Down",
			"Monthly - Low Down");
	private static final ImmutableList<String> RESTRICTED_PAYMENT_PLANS = ImmutableList.of(
			"Annual",
			"Monthly");
	private static final ImmutableList<String> UNRESTRICTED_PAYMENT_PLANS = ImmutableList.of(
			"Monthly - Zero Down",
			"Monthly - Low Down",
			"Semi-Annual",
			"Quarterly",
			"Eleven Pay - Standard",
			"Eleven Pay - Zero Down",
			"Eleven Pay - Low Down");
	private static final ImmutableList<String> PAYMENT_PLAN_HEADER = ImmutableList.of(
			"Plan",
			"Premium",
			"Minimum Down Payment",
			"Installment Amount (w/o fees)",
			"# of Remaining Installments");
	private static final String RESTRICTED_PAY_PLANS_MESSAGE = "The available pay plans for this quote are restricted to those shown above. The below options can be offered if the following condition is addressed: AAA Membership must be provided.\nAfter addressing the condition, recalculate premium to refresh the available pay plans.";
	private static final String INSTALLMENT_FEES_MESSAGE = "Installment Amount does not include transaction fees. View applicable fee schedule.";

	/**
	 * @name Test Restricted Payment Plan For Home with Membership = Yes and no other restrictions
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Yes', don't add any other restrictions to payment plans.
	 * 3. Go to P&C page and verify  that:
	 * - restriction isn't applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "10870")
	public void pas10870_restrictionPaymentPlansMembershipYes(@Optional("AZ") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyDefaultTD(), PremiumAndCoveragesTab.class, true);
		verifyAllPayPlansAvailable();
	}

	/**
	 * @name Test Restricted Payment Plan For Home with Membership = Pending and no other restrictions
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Pending', don't add any other restrictions to payment plans.
	 * 3. Go to P&C page and verify  that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text are present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "10870")
	public void pas10870_restrictionPaymentPlansMembershipPending(@Optional("AZ") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		String membershipPendingKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());
		TestData membershipTD = getTestSpecificTD("AAAProductOwned_Pending");
		TestData td = getPolicyDefaultTD().adjust(membershipPendingKey, membershipTD);
		policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesTab.class, true);
		verifyRestrictedAndUnrestrictedPaymentPlans();
	}

	/**
	 * @name Test Restricted Payment Plan For Home with Membership = No and no other restrictions
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'No', don't add any other restrictions to payment plans.
	 * 3. Go to P&C page and verify  that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "10870")
	public void pas10870_restrictionPaymentPlansMembershipNo(@Optional("D") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		String membershipNoKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());
		TestData membershipTD = getTestSpecificTD("AAAProductOwned_No");
		TestData td = getPolicyDefaultTD().adjust(membershipNoKey, membershipTD);
		policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesTab.class, true);
		verifyRestrictedPaymentPlans();
	}

	/**
	 * @name Test Restricted Payment Plan For Home with Membership = Membership Override and no other restrictions
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Membership Override', don't add any other restrictions to payment plans.
	 * 3. Go to P&C page and verify  that:
	 * - restriction isn't applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "11366")
	public void pas11366_restrictionPaymentPlansMembershipOverride(@Optional("NJ") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		String membershipNoKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());
		TestData membershipTD = getTestSpecificTD("AAAProductOwned_Override");
		TestData td = getPolicyDefaultTD().adjust(membershipNoKey, membershipTD);
		policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesTab.class, true);
		verifyAllPayPlansAvailable();
	}

	/**
	 * @name Test Restricted Payment Plan For Home with Membership = Yes and prior BI restriction
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Yes' and no Prior BI.
	 * 3. Go to P&C page and verify  that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "10870")
	public void pas10870_restrictionPaymentPlansMembershipYesPriorBINo(@Optional("AZ") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		String currentCarrierKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel());
		TestData currentCarrierTD = getTestSpecificTD("CurrentCarrierInformation_No");
		TestData td = getPolicyDefaultTD().adjust(currentCarrierKey, currentCarrierTD);
		policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesTab.class, true);
		verifyRestrictedPaymentPlans();
	}

	/**
	 * @name Test Restricted Payment Plan For Home with Membership = Membership Override and minimum BI (on P&C page) restriction
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Membership Override' and minimum BI (on P&C page).
	 * 3. Go to P&C page and verify  that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "11366")
	public void pas11366_restrictionPaymentPlansMembershipOverrideMinimumBI(@Optional("NJ") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		String membershipOverrideKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());
		TestData membershipOverrideTD = getTestSpecificTD("AAAProductOwned_Override");
		String minimumBIKey = TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName());
		TestData minimumBITD = getTestSpecificTD("PremiumAndCoveragesTab_Minimum");
		TestData td = getPolicyDefaultTD().adjust(membershipOverrideKey, membershipOverrideTD).adjust(minimumBIKey, minimumBITD);
		policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesTab.class, true);
		verifyRestrictedPaymentPlans();
	}

	/**
	 * @name Test Restricted Payment Plan For Home with Membership = Membership Pending and no Prior BI restriction
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Membership Override' and no Prior BI.
	 * 3. Go to P&C page and verify  that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "10870")
	public void pas10870_restrictionPaymentPlansMembershipPendingPriorBINo(@Optional("AZ") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		String membershipPendingKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());
		TestData membershipPendingTD = getTestSpecificTD("AAAProductOwned_Pending");
		String currentCarrierKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel());
		TestData currentCarrierTD = getTestSpecificTD("CurrentCarrierInformation_No");
		TestData td = getPolicyDefaultTD().adjust(membershipPendingKey, membershipPendingTD).adjust(currentCarrierKey,currentCarrierTD);
		policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesTab.class, true);
		verifyRestrictedPaymentPlans();
	}

	/**
	 * @name Test Restricted Payment Plan For Home with Membership = Membership Pending and minimum BI (on P&C page) restriction
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Membership Pending' and minimum BI (on P&C page).
	 * 3. Go to P&C page and verify  that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "10870")
	public void pas10870_restrictionPaymentPlansMembershipPendingMinimumBI(@Optional("AZ") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		String membershipPendingKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());
		TestData membershipPendingTD = getTestSpecificTD("AAAProductOwned_Pending");
		String minimumBIKey = TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName());
		TestData minimumBITD = getTestSpecificTD("PremiumAndCoveragesTab_Minimum");
		TestData td = getPolicyDefaultTD().adjust(membershipPendingKey, membershipPendingTD).adjust(minimumBIKey,minimumBITD);
		policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesTab.class, true);
		verifyRestrictedPaymentPlans();
	}

	private void verifyAllPayPlansAvailable(){
		//check that Payment plan drop down has all payment plans
		ComboBox paymentPlan = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN);
		verifyPaymentPlansList(paymentPlan, ALL_PAYMENT_PLANS);
		PremiumAndCoveragesTab.linkPaymentPlan.click();
		//check that table for PaymentPlans has all payment plans
		assertThat(PremiumAndCoveragesTab.tablePaymentPlans).isPresent();
		assertThat(PremiumAndCoveragesTab.tablePaymentPlans.getHeader().getValue()).isEqualTo(PAYMENT_PLAN_HEADER);
		assertThat(PremiumAndCoveragesTab.tablePaymentPlans.getColumn(1).getValue()).isEqualTo(ALL_PAYMENT_PLANS);
		//check that installment fees message is present
		assertThat(PremiumAndCoveragesTab.labelInstallmentFees.getValue()).isEqualTo(INSTALLMENT_FEES_MESSAGE);
		//check that unrestricted payment plans table is absent
		assertThat(PremiumAndCoveragesTab.tableUnrestrictedPaymentPlans).isAbsent();
	}

	private void verifyRestrictedAndUnrestrictedPaymentPlans(){
		//check that Payment plan drop down has all payment plans
		ComboBox paymentPlan = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN);
		verifyPaymentPlansList(paymentPlan, RESTRICTED_PAYMENT_PLANS);
		PremiumAndCoveragesTab.linkPaymentPlan.click();
		//check that first table for PaymentPlans has restricted payment plans
		assertThat(PremiumAndCoveragesTab.tablePaymentPlans).isPresent();
		assertThat(PremiumAndCoveragesTab.tablePaymentPlans.getHeader().getValue()).isEqualTo(PAYMENT_PLAN_HEADER);
		assertThat(PremiumAndCoveragesTab.tablePaymentPlans.getColumn(1).getValue()).isEqualTo(RESTRICTED_PAYMENT_PLANS);
		//check that restricted payment plans message is present
		assertThat(PremiumAndCoveragesTab.labelPaymentPlanRestriction.getValue()).isEqualTo(RESTRICTED_PAY_PLANS_MESSAGE);
		//check that second table for PaymentPlans has unrestricted payment plans
		assertThat(PremiumAndCoveragesTab.tableUnrestrictedPaymentPlans).isPresent();
		assertThat(PremiumAndCoveragesTab.tableUnrestrictedPaymentPlans.getHeader().getValue()).isEqualTo(PAYMENT_PLAN_HEADER);
		assertThat(PremiumAndCoveragesTab.tableUnrestrictedPaymentPlans.getColumn(1).getValue()).isEqualTo(UNRESTRICTED_PAYMENT_PLANS);
		//check that installment fees message is present
		assertThat(PremiumAndCoveragesTab.labelInstallmentFees.getValue()).isEqualTo(INSTALLMENT_FEES_MESSAGE);
	}

	private void verifyRestrictedPaymentPlans(){
		//check that Payment plan drop down has all payment plans
		ComboBox paymentPlan = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN);
		verifyPaymentPlansList(paymentPlan, RESTRICTED_PAYMENT_PLANS);
		PremiumAndCoveragesTab.linkPaymentPlan.click();
		//check that table for PaymentPlans has restricted payment plans
		assertThat(PremiumAndCoveragesTab.tablePaymentPlans).isPresent();
		assertThat(PremiumAndCoveragesTab.tablePaymentPlans.getHeader().getValue()).isEqualTo(PAYMENT_PLAN_HEADER);
		assertThat(PremiumAndCoveragesTab.tablePaymentPlans.getColumn(1).getValue()).isEqualTo(RESTRICTED_PAYMENT_PLANS);
		//check that installment fees message is present
		assertThat(PremiumAndCoveragesTab.labelInstallmentFees.getValue()).isEqualTo(INSTALLMENT_FEES_MESSAGE);
		//check that unrestricted payment plans table is absent
		assertThat(PremiumAndCoveragesTab.tableUnrestrictedPaymentPlans).isAbsent();
	}

	private void verifyPaymentPlansList(ComboBox paymentPlan, ImmutableList<String> expectedPaymentPlans) {
		List<String> actualPaymentPlan = paymentPlan.getAllValues();
		assertThat(actualPaymentPlan.size()).as("Incorrect PaymentPlans amount in dropdown").isEqualTo(expectedPaymentPlans.size());
		for (String expectedPaymentPlan : expectedPaymentPlans){
			String foundPaymentPlan = checkPaymentPlan(actualPaymentPlan, expectedPaymentPlan);
			assertThat(foundPaymentPlan).as("PayPlan %s isn't found", expectedPaymentPlan).isEqualTo(expectedPaymentPlan);
		}
	}

	private String checkPaymentPlan(List<String> actualPaymentPlan, String expectedPaymentPlan) {
		for (String actualPaymentPlanValue : actualPaymentPlan) {
			if (actualPaymentPlanValue.equals(expectedPaymentPlan)) {
				return actualPaymentPlanValue;
			}
		}
		return null;
	}
}
