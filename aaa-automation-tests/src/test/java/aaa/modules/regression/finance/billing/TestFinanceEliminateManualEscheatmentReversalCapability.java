package aaa.modules.regression.finance.billing;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.actiontabs.OtherTransactionsActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.utils.TestInfo;

public class TestFinanceEliminateManualEscheatmentReversalCapability extends FinanceBaseTest {

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

	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-19072")
	public void pas19072_testFinanceEliminateManualEscheatmentReversalCapability() {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.linkOtherTransactions.click();
		otherTransactionsActionTab.getAssetList().getAsset(BillingAccountMetaData.OtherTransactionsActionTab.TRANSACTION_TYPE).setValue("Adjustment");

		assertThat(otherTransactionsActionTab.getAssetList().getAsset(BillingAccountMetaData.OtherTransactionsActionTab.TRANSACTION_SUBTYPE)).doesNotContainOption("Escheatment");
	}
}
