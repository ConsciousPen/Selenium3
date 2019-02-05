package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.modules.regression.sales.template.functional.TestClaimPointsVRDPageAbstract;
import aaa.modules.regression.sales.template.functional.TestInsuranceScoreNBTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Table;

public class TestInsuranceScoreNB extends TestInsuranceScoreNBTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA, Constants.States.MD})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-24967")
	public void pas24967_testInsuranceScoreNB(@Optional("") String state) {
		testInsuranceScoreNB();
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.MD})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-24967")
	public void pas24967_testInsuranceScoreNBMD(@Optional("MD") String state) {
		testInsuranceScoreNB();
	}
}