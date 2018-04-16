package aaa.modules.regression.billing_and_payments.home_ca.ho3.functional;

import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.regression.billing_and_payments.template.functional.TestEarnedPremiumWriteOffAbstract;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestEarnedPremiumWriteOff extends TestEarnedPremiumWriteOffAbstract{

	@Override
	protected TestData getTdPolicy() {
		return testDataManager.policy.get(getPolicyType());
	}

	@Override
	protected TestData getTestSpecificTDForTestEndorsement() {
		return getTestSpecificTD("TestData_Endorsement");
	}

	public void changeStatusFromDeclineToProposed(String policyNumber) {
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		policy.renew().perform();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().submitTab();
	}

	@Override
	public String processEarnedPremiumJobWithAPEndorsementMortgagee(LocalDateTime expirationDate, String policyNumber, String endorsementAmount) {
		processEarnedPremiumJob(expirationDate, policyNumber);
		assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff).isPresent();
		assertThat(BillingSummaryPage.labelAmountEarnedPremiumWriteOff.getValue()).isEqualTo(endorsementAmount);
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue()).isEqualTo("Adjustment");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(SUBTYPE_REASON).getValue()).isEqualTo("Earned Premium Write-off");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(AMOUNT).getValue()).isEqualTo("("+endorsementAmount+")");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(STATUS).getValue()).isEqualTo("Applied");
		return endorsementAmount;
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffLessDecline(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffLessDecline(state);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffEqualDecline(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffEqualDecline(state);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffMoreDecline(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffMoreDecline(state);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffLessProposed(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffLessProposed(state);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffEqualProposed(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffEqualProposed(state);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffMoreProposed(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffMoreProposed(state);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffNoAP(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffNoAP(state);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffLessDeclineMortgagee(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffLessDeclineMortgagee(state);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffEqualProposedMortgagee(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffEqualProposedMortgagee(state);
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffMoreDeclineMortgagee(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffMoreDeclineMortgagee(state);
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}
}
