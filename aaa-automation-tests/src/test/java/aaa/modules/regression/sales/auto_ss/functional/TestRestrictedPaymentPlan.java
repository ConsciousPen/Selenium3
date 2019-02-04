package aaa.modules.regression.sales.auto_ss.functional;

import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.google.common.collect.ImmutableList;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.regression.sales.template.functional.TestRestrictedPaymentPlanAbstract;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;

public class TestRestrictedPaymentPlan extends TestRestrictedPaymentPlanAbstract {

	@Override
	public List<String> getExpectedAllPaymentPlans() {
		return ImmutableList.of(
				"Annual",
				"Semi-Annual",
				"Quarterly",
				"Eleven Pay - Standard",
				"Eleven Pay - Zero Down",
				"Eleven Pay - Low Down",
				"Monthly - Zero Down",
				"Monthly - Low Down");
	}

	@Override
	public List<String> getExpectedRestrictedPaymentPlans() {
		return ImmutableList.of(
				"Annual",
				"Monthly");
	}

	@Override
	public List<String> getExpectedUnrestrictedPaymentPlans() {
		return ImmutableList.of(
				"Monthly - Zero Down",
				"Monthly - Low Down",
				"Semi-Annual",
				"Quarterly",
				"Eleven Pay - Standard",
				"Eleven Pay - Zero Down",
				"Eleven Pay - Low Down");
	}

	@Override
	public List<String> getExpectedHeader() {
		return ImmutableList.of(
				"Plan",
				"Premium",
				"Minimum Down Payment",
				"Installment Amount (w/o fees)",
				"# of Remaining Installments");
	}

	@Override
	public List<String> getPaymentPlanComboBox() {
		List<String> paymentPlanList = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).getAllValues();
		paymentPlanList.removeIf(""::equals);
		return paymentPlanList;
	}

	@Override
	public Table getTablePaymentPlans() {
		return PremiumAndCoveragesTab.tablePaymentPlans;
	}

	@Override
	public StaticElement getLabelInstallmentFees() {
		return PremiumAndCoveragesTab.labelInstallmentFees;
	}

	@Override
	public Table getTableUnrestrictedPaymentPlans() {
		return PremiumAndCoveragesTab.tableUnrestrictedPaymentPlans;
	}

	@Override
	public StaticElement getLabelPaymentPlanRestriction() {
		return PremiumAndCoveragesTab.labelPaymentPlanRestriction;
	}

	@Override
	public void clickPaymentPlanLink() {
		PremiumAndCoveragesTab.linkPaymentPlan.click();
	}

	/**
	 * @name Test Restricted Payment Plan For Auto with Membership = Yes and no other restrictions
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Yes', don't add any other restrictions to payment plans.
	 * 3. Go to P&C page and verify  that:
	 * - restriction isn't applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = States.CA)
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
	 * @name Test Restricted Payment Plan For Auto with Membership = Pending and no other restrictions
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Pending', don't add any other restrictions to payment plans.
	 * 3. Go to P&C page and verify  that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text are present
	 * @details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "10870")
	public void pas10870_restrictionPaymentPlansMembershipPending(@Optional("AZ") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		String membershipPendingKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel());
		TestData membershipPendingTD = getTestSpecificTD("AAAMembership_Pending");
		TestData td = getPolicyDefaultTD().adjust(membershipPendingKey, membershipPendingTD);
		policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesTab.class, true);
		verifyRestrictedAndUnrestrictedPaymentPlans();
	}

	/**
	 * @name Test Restricted Payment Plan For Auto with Membership = No and no other restrictions
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'No', don't add any other restrictions to payment plans.
	 * 3. Go to P&C page and verify  that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "10870")
	public void pas10870_restrictionPaymentPlansMembershipNo(@Optional("AZ") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		String membershipNoKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel());
		TestData membershipNoTD = getTestSpecificTD("AAAMembership_No");
		TestData td = getPolicyDefaultTD().adjust(membershipNoKey, membershipNoTD);
		policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesTab.class, true);
		verifyRestrictedPaymentPlans();
	}

	/**
	 * @name Test Restricted Payment Plan For Auto with Membership = Membership Override and no other restrictions
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Membership Override', don't add any other restrictions to payment plans.
	 * 3. Go to P&C page and verify  that:
	 * - restriction isn't applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "11366")
	public void pas11366_restrictionPaymentPlansMembershipOverride(@Optional("AZ") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		String membershipOverrideKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel());
		TestData membershipOverrideTD = getTestSpecificTD("AAAMembership_Override");
		TestData td = getPolicyDefaultTD().adjust(membershipOverrideKey, membershipOverrideTD);
		policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesTab.class, true);
		verifyAllPayPlansAvailable();
	}

	/**
	 * @name Test Restricted Payment Plan For Auto with Membership = Yes and prior BI restriction
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Yes' and no Prior BI.
	 * 3. Go to P&C page and verify  that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = States.CA)
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
	 * @name Test Restricted Payment Plan For Auto with Membership = Membership Override and minimum BI (on P&C page) restriction
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Membership Override' and minimum BI (on P&C page).
	 * 3. Go to P&C page and verify  that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "11366")
	public void pas11366_restrictionPaymentPlansMembershipOverrideMinimumBI(@Optional("AZ") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		String membershipOverrideKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel());
		TestData membershipOverrideTD = getTestSpecificTD("AAAMembership_Override");
		String minimumBIKey = TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName());
		TestData minimumBITD = getTestSpecificTD("PremiumAndCoveragesTab_Minimum");
		TestData td = getPolicyDefaultTD().adjust(membershipOverrideKey, membershipOverrideTD).adjust(minimumBIKey, minimumBITD);
		policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesTab.class, true);
		verifyRestrictedPaymentPlans();
	}

	/**
	 * @name Test Restricted Payment Plan For Auto with Membership = Membership Pending and no Prior BI restriction
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Membership Override' and no Prior BI.
	 * 3. Go to P&C page and verify  that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "10870")
	public void pas10870_restrictionPaymentPlansMembershipPendingPriorBINo(@Optional("AZ") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		String membershipPendingKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel());
		TestData membershipPendingTD = getTestSpecificTD("AAAMembership_Pending");
		String currentCarrierKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel());
		TestData currentCarrierTD = getTestSpecificTD("CurrentCarrierInformation_No");
		TestData td = getPolicyDefaultTD().adjust(membershipPendingKey, membershipPendingTD).adjust(currentCarrierKey,currentCarrierTD);
		policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesTab.class, true);
		verifyRestrictedPaymentPlans();
	}

	/**
	 * @name Test Restricted Payment Plan For Auto with Membership = Membership Pending and minimum BI (on P&C page) restriction
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Membership Pending' and minimum BI (on P&C page).
	 * 3. Go to P&C page and verify  that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "10870")
	public void pas10870_restrictionPaymentPlansMembershipPendingMinimumBI(@Optional("AZ") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		String membershipPendingKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel());
		TestData membershipPendingTD = getTestSpecificTD("AAAMembership_Pending");
		String minimumBIKey = TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName());
		TestData minimumBITD = getTestSpecificTD("PremiumAndCoveragesTab_Minimum");
		TestData td = getPolicyDefaultTD().adjust(membershipPendingKey, membershipPendingTD).adjust(minimumBIKey,minimumBITD);
		policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesTab.class, true);
		verifyRestrictedPaymentPlans();
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

}
