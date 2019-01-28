package aaa.modules.bct.service;

import static aaa.common.enums.Constants.States.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
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
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public class ReinstatePolicyTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_006_ReinstatePolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_006_ReinstatePolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_SS.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.AUTO_SS);
		aaa.main.modules.policy.auto_ss.actiontabs.ReinstatementActionTab reinstatementTab = new aaa.main.modules.policy.auto_ss.actiontabs.ReinstatementActionTab();

		SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_CANCELLED);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		LocalDateTime cancellationDate = TimeSetterUtil.getInstance().parse(PolicySummaryPage.tableGeneralInformation
				.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.CANCELLATION_EFF_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
		String reinstatementDate = cancellationDate.plusDays(48).format(DateTimeUtils.MM_DD_YYYY);
		String reinstatementKey = TestData.makeKeyPath(reinstatementTab.getMetaKey(), AutoSSMetaData.ReinstatementActionTab.REINSTATE_DATE.getLabel());

		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData").adjust(reinstatementKey, reinstatementDate));

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyLapseExistFlagPresent();
		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1)).exists();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Type")).hasValue("Reinstatement with Lapse");
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_007_ReinstatePolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_007_ReinstatePolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_SS.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.AUTO_SS);
		aaa.main.modules.policy.auto_ss.actiontabs.ReinstatementActionTab reinstatementTab = new aaa.main.modules.policy.auto_ss.actiontabs.ReinstatementActionTab();

		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		deletePendingTransaction(policy);

		policy.cancel().perform(getStateTestData(tdPolicy, "Cancellation", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		LocalDateTime cancellationDate = TimeSetterUtil.getInstance().parse(PolicySummaryPage.tableGeneralInformation
				.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.CANCELLATION_EFF_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
		String reinstatementDate = cancellationDate.plusDays(48).format(DateTimeUtils.MM_DD_YYYY);
		String reinstatementKey = TestData.makeKeyPath(reinstatementTab.getMetaKey(), AutoSSMetaData.ReinstatementActionTab.REINSTATE_DATE.getLabel());

		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData").adjust(reinstatementKey, reinstatementDate));

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyLapseExistFlagPresent();
		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1)).exists();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Type")).hasValue("Reinstatement with Lapse");
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_010_ReinstatePolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_010_ReinstatePolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.HOME_SS_HO3.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.HOME_SS_HO3);
		ReinstatementActionTab reinstatementTab = new ReinstatementActionTab();

		SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_CANCELLED);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		policy.reinstate().start();
		LocalDateTime cancellationDate = TimeSetterUtil.getInstance().parse(reinstatementTab.getAssetList()
				.getAsset(HomeSSMetaData.ReinstatementActionTab.CANCELLATION_EFFECTIVE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
		String reinstatementDate = cancellationDate.plusDays(48).format(DateTimeUtils.MM_DD_YYYY);
		String reinstatementKey = TestData.makeKeyPath(reinstatementTab.getMetaKey(), HomeSSMetaData.ReinstatementActionTab.REINSTATE_DATE.getLabel());
		reinstatementTab.fillTab(getStateTestData(tdPolicy, "Reinstatement", "TestData").adjust(reinstatementKey, reinstatementDate));
		Tab.buttonOk.click();
		assertThat(Page.dialogConfirmation.labelMessage).valueContains("Policy will be reinstated with a lapse");
		Page.dialogConfirmation.confirm();

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyLapseExistFlagPresent();

		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1)).exists();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Type")).hasValue("Reinstatement with Lapse");
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_011_ReinstatePolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_011_ReinstatePolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.HOME_SS_HO3.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.HOME_SS_HO3);
		ReinstatementActionTab reinstatementTab = new ReinstatementActionTab();

		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		deletePendingTransaction(policy);

		policy.cancel().perform(getStateTestData(tdPolicy, "Cancellation", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		LocalDateTime cancellationDate = TimeSetterUtil.getInstance().parse(PolicySummaryPage.tableGeneralInformation
				.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.CANCELLATION_EFFECTIVE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);

		String reinstatementKey = TestData.makeKeyPath(reinstatementTab.getMetaKey(), HomeSSMetaData.ReinstatementActionTab.REINSTATE_DATE.getLabel());

		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData").adjust(reinstatementKey, cancellationDate.format(DateTimeUtils.MM_DD_YYYY)));

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.labelLapseExist).isPresent(false);
		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1)).exists();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Type")).hasValue("Reinstatement");
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {CA})
	public void BCT_ONL_014_ReinstatePolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_014_ReinstatePolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_CA_SELECT.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.AUTO_CA_SELECT);

		SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_CANCELLED);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		//Reinstatement date field is disabled
		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData"));

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {CA})
	public void BCT_ONL_016_ReinstatePolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_016_ReinstatePolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_CA_SELECT.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.AUTO_CA_SELECT);

		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.cancel().perform(getStateTestData(tdPolicy, "Cancellation", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData"));

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.labelLapseExist).as("Lapse period flag is present").isPresent(false);

		assertThat(PolicySummaryPage.TransactionHistory.getType(1)).as("Reinstatement transaction added to Transaction History").isEqualTo("Reinstatement");
	}
}