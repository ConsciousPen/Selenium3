package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.regression.sales.template.functional.TestAutoClueResponseTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import java.util.HashMap;
import java.util.LinkedHashMap;

@StateList(states = Constants.States.CA)
public class TestClueResponseMultipleDriversAssignment extends TestAutoClueResponseTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Override
	protected DriverTab getDriverTab() {
		return new DriverTab();
	}

	@Override
	protected MembershipTab getFirstReportsTab() {
		return new MembershipTab();
	}

	@Override
	protected PremiumAndCoveragesTab getPremiumAndCoveragesTab() {
		return new PremiumAndCoveragesTab();
	}

	@Override
	protected DriverActivityReportsTab getDriverActivityReportsTab() {
		return new DriverActivityReportsTab();
	}

	@Override
	protected DocumentsAndBindTab getDocumentsAndBindTab() {
		return new DocumentsAndBindTab();
	}

	/**
	 *@author Dominykas Razgunas
	 *@name Test Clue assignment for multiple drivers
	 *@scenario
	 * 1. Create A customer with Driver1 name and lastname
	 * 2. Create Auto Quote
	 * 3. Add 2nd Driver
	 * 4. Rate Quote
	 * 5. Order Reports
	 * 6. Navigate to Driver Tab
	 * 7. Check That both drivers have 3 claims each
	 *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-20371")
	public void pas20371_testClueActivityMappingToDriver(@Optional("CA") String state) {

		HashMap<String, String> drivers = new LinkedHashMap<>();

		drivers.put("JIm", "Smith");
		drivers.put("Jane", "Smith");

		testReturnedClaimsForNB(drivers);
		pas_20371_ClueActivityMappingToDriver();
	}
}