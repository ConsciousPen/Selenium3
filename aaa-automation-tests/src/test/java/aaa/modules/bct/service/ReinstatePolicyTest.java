package aaa.modules.bct.service;

import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.actiontabs.ReinstatementActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

import java.time.LocalDateTime;

public class ReinstatePolicyTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	public void BCT_ONL_006_ReinstatePolicy(@Optional("") String state) {
		String policyNumber = getPoliciesByQuery("BCT_ONL_006_ReinstatePolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_SS.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.AUTO_SS);
		aaa.main.modules.policy.auto_ss.actiontabs.ReinstatementActionTab reinstatementTab = new aaa.main.modules.policy.auto_ss.actiontabs.ReinstatementActionTab();

		mainApp().open();
		SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_CANCELLED);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		LocalDateTime cancellationDate = TimeSetterUtil.getInstance().parse(PolicySummaryPage.tableGeneralInformation
				.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.CANCELLATION_EFF_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
		//TODO Check what reinstatement date do we need
		String reinstatementDate = cancellationDate.plusDays(48).format(DateTimeUtils.MM_DD_YYYY);
		String reinstatementKey = TestData.makeKeyPath(reinstatementTab.getMetaKey(), AutoSSMetaData.ReinstatementActionTab.REINSTATE_DATE.getLabel());

		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData").adjust(reinstatementKey, reinstatementDate));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyLapseExistFlagPresent();

		CustomAssert.assertEquals("Reinstatement transaction added to Transaction History", "Reinstatement with Lapse", PolicySummaryPage.TransactionHistory.getType(1));
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_007_ReinstatePolicy(@Optional("") String state) {
		String policyNumber = getPoliciesByQuery("BCT_ONL_007_ReinstatePolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_SS.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.AUTO_SS);
		aaa.main.modules.policy.auto_ss.actiontabs.ReinstatementActionTab reinstatementTab = new aaa.main.modules.policy.auto_ss.actiontabs.ReinstatementActionTab();

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.cancel().perform(getStateTestData(tdPolicy, "Cancellation", "TestData"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.labelLapseExist.verify.present("Lapse period flag is present", false);

		CustomAssert.assertEquals("Reinstatement transaction added to Transaction History", PolicySummaryPage.TransactionHistory.getType(1), "Reinstatement");
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_010_ReinstatePolicy(@Optional("") String state) {
		String policyNumber = getPoliciesByQuery("BCT_ONL_010_ReinstatePolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.HOME_SS_HO3.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.HOME_SS_HO3);
		ReinstatementActionTab reinstatementTab = new ReinstatementActionTab();

		mainApp().open();
		SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_CANCELLED);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		policy.reinstate().start();
		//TODO Check what reinstatement date do we need
		LocalDateTime cancellationDate = TimeSetterUtil.getInstance().parse(reinstatementTab.getAssetList()
				.getAsset(HomeSSMetaData.ReinstatementActionTab.CANCELLATION_EFFECTIVE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
		String reinstatementDate = cancellationDate.plusDays(48).format(DateTimeUtils.MM_DD_YYYY);
		String reinstatementKey = TestData.makeKeyPath(reinstatementTab.getMetaKey(), HomeSSMetaData.ReinstatementActionTab.REINSTATE_DATE.getLabel());
		reinstatementTab.fillTab(getStateTestData(tdPolicy, "Reinstatement", "TestData").adjust(reinstatementKey, reinstatementDate));
		reinstatementTab.buttonOk.click();
		Page.dialogConfirmation.labelMessage.verify.contains("Policy will be reinstated with a lapse");
		Page.dialogConfirmation.confirm();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyLapseExistFlagPresent();

		CustomAssert.assertEquals("Reinstatement transaction added to Transaction History", "Reinstatement with Lapse", PolicySummaryPage.TransactionHistory.getType(1));
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_011_ReinstatePolicy(@Optional("") String state) {
		String policyNumber = getPoliciesByQuery("BCT_ONL_011_ReinstatePolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.HOME_SS_HO3.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.HOME_SS_HO3);
		ReinstatementActionTab reinstatementTab = new ReinstatementActionTab();

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.cancel().perform(getStateTestData(tdPolicy, "Cancellation", "TestData"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.labelLapseExist.verify.present("Lapse period flag is present", false);

		CustomAssert.assertEquals("Reinstatement transaction added to Transaction History", PolicySummaryPage.TransactionHistory.getType(1), "Reinstatement");
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_014_ReinstatePolicy(@Optional("") String state) {
		String policyNumber = getPoliciesByQuery("BCT_ONL_014_ReinstatePolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_CA_SELECT.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.AUTO_CA_SELECT);
		aaa.main.modules.policy.auto_ca.actiontabs.ReinstatementActionTab reinstatementTab = new aaa.main.modules.policy.auto_ca.actiontabs.ReinstatementActionTab();

		mainApp().open();
		SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_CANCELLED);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		//Reinstatement date field is disabled
		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_016_ReinstatePolicy(@Optional("") String state) {
		String policyNumber = getPoliciesByQuery("BCT_ONL_016_ReinstatePolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_CA_SELECT.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.AUTO_CA_SELECT);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.cancel().perform(getStateTestData(tdPolicy, "Cancellation", "TestData"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.labelLapseExist.verify.present("Lapse period flag is present", false);

		CustomAssert.assertEquals("Reinstatement transaction added to Transaction History", PolicySummaryPage.TransactionHistory.getType(1), "Reinstatement");
	}
}
