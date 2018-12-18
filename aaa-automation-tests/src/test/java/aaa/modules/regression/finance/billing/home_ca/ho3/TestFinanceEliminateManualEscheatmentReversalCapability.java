package aaa.modules.regression.finance.billing.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.actiontabs.OtherTransactionsActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.utils.TestInfo;

public class TestFinanceEliminateManualEscheatmentReversalCapability extends PolicyBaseTest {

	/**
	 * @author Maksim Piatrouski
	 * Objectives : check "Escheatment" not displayed in Transaction Subtype drop down section
	 * on the PAS UI billing page - the Other Transactions button under the payments and other transactions section
	 * TC Steps:
	 * 1. Create Policy
	 * 2. Navigate Billing - Other Transactions
	 * 3. Set Transaction Type = "Adjustment"
	 * 4. Check "Escheatment" not displayed in Transaction Subtype drop down
	 */

	OtherTransactionsActionTab otherTransactionsActionTab = new OtherTransactionsActionTab();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-19072")
	public void pas19072_testFinanceEliminateManualEscheatmentReversalCapability(@Optional("CA") String state) {
		openAppAndCreatePolicy();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.linkOtherTransactions.click();
		otherTransactionsActionTab.getAssetList().getAsset(BillingAccountMetaData.OtherTransactionsActionTab.TRANSACTION_TYPE).setValue("Adjustment");

		assertThat(otherTransactionsActionTab.getAssetList().getAsset(BillingAccountMetaData.OtherTransactionsActionTab.TRANSACTION_SUBTYPE)).doesNotContainOption("Escheatment");
	}
}
