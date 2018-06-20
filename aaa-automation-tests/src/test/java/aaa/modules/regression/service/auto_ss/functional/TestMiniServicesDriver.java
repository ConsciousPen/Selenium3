package aaa.modules.regression.service.auto_ss.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.TestMiniServicesDriversAbstract;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMiniServicesDriver extends TestMiniServicesDriversAbstract {

	private final DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private final TestEValueDiscount testEValueDiscount = new TestEValueDiscount();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Megha Gubbala
	 * @name View Drivers service, check info.
	 * @scenario
	 * 1. Create policy on Pas with 3 drivers 2NI
	 * 2. hit view vehicle service and check.
	 * 3.First Named Insured (FNI) = the named insured that is first on the policy
	 * 4.Named Insured (NI) = all other named insureds on the policy
	 * 5.Not a Named Insured = a driver who is not a named insured on the policy
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14463"})
	public void pas14463_ViewDriverService(@Optional("AZ") String state) {
		TestData td = getTestSpecificTD("TestData");

		pas14463_viewDriverServiceBody(getPolicyType(), td);
	}

	/**
	 * @author Megha Gubbala
	 * @name View Drivers service, check info.
	 * @scenario
	 * 1. Create policy on Pas with 6 drivers (2 NI 2 AFR 2 not name insured and 1 excluded driver)
	 * 2. hit view vehicle service and check order of drivers.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-482"})
	public void pas482_ViewDriverServiceOrderOfDriver(@Optional("AZ") String state) {
		TestData td = getTestSpecificTD("TestData1");
		pas482_ViewDriverServiceOrderOfDriverBody(td);
	}

	/**
	 * @author Megha Gubbala
	 * @name Add Drivers service, check info.
	 * @scenario
	 * 1. Create policy on Pas.
	 * 2. To add driver send request first name, middle name, last name, suffix and Date of Birth
	 * 3. run add driver service
	 * 4. go to Pas ui and verify if driver is added
	 * 5. hit view driver service and check all defaults
	 * 6. verify status of driver pending
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-478"})
	public void pas478_AddDrivers(@Optional("AZ") String state) {

		pas478_AddDriversBody(getPolicyType());
	}
}

