package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestNoPriorInsuranceError extends AutoSSBaseTest {
	private GeneralTab generalTab = new GeneralTab();

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-4244")
	public void testErrorMessagePresenceDARTabDE(@Optional("DE") String state) {
		TestData defaultTestData = getPolicyTD("DataGather", "TestData");
		TestData currentCarrierSectionAdjusted = defaultTestData.getTestData("CurrentCarrierInformation");
		//TestData generalTabAdjusted = DataProviderFactory.emptyData().adjust("CurrentCarrierInformation", currentCarrierSectionAdjusted);
		//TestData currentCarrierData = defaultTestData.adjust("GeneralTab", generalTabAdjusted);

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());

		//generalTab.fillTab(currentCarrierData);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-4244")
	public void testErrorMessagePresenceDARTabNJ(@Optional("NJ") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();

	}
}
