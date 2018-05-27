package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.auto_ss.defaulttabs.FormsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.table.Table;

public class TestPaymentPlanTable extends AutoSSBaseTest {
	/**
	 * @author Jovita Pukenaite
	 * @name Verify if Premium part exist in Payment plan table
	 * @scenario 1. Initiate quote creation.
	 * 2. Fill all data until P&C page.
	 * 3. Go to P&C page, open Payment Plan table.
	 * 4. Check if Premium part exist.
	 * 5. Calculate premium.
	 * 6. Check if Premium part still exist on the table.
	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-13787")
	public void pas13787_CheckPremiumInPaymentPlanTable(@Optional("VA") String state) {

		TestData td = getPolicyTD("DataGather", "TestData");
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(td, FormsTab.class, true);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
		premiumAndCoveragesTab.linkPaymentPlan.click();

		//Check Payment Plan table
		assertThat(getTablePaymentPlans()).isPresent();
		getTablePaymentPlans().getRow(1).getCell("Premium").getValue().startsWith("$");
		getTablePaymentPlans().getRow(1).getCell("Minimum Down Payment").getValue().startsWith("$");
		getTablePaymentPlans().getRow(1).getCell("Installment Amount (w/o fees)").getValue().startsWith("$");
	}

	protected Table getTablePaymentPlans() {
		return PremiumsAndCoveragesQuoteTab.tablePaymentPlans;
	}
}
