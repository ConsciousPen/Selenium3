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
	 * @author Jovita Pukenaite
	 * @name view Drivers service, check info.
	 * @scenario
	 * 1. Create policy with two drivers.
	 * 2. Check if the same drivers are displaying in dxp service.
	 * 3. Initiate endorsement, and add driver middle name and suffix for one of the drivers. Don't bind.
	 * 4. Check if user can't be able to see new driver information.
	 * 5. Bind the endorsement.
	 * 6. Check if new information from endorsement is displaying.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11932", "PAS-12768"})
	public void pas11932_viewDrivers(@Optional("VA") String state) {

		pas11932_viewDriversInfo(getPolicyType());
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
	 * @Megha : added Update driver
	 * 1. Add drive more than 55 years old
	 * 2. update driver with these parameters  stateLicensed,licenseNumber,gender,relationToApplicantCd,maritalStatusCd,ageFirstLicensed
	 * 3.Verify Response
	 * 4. Go to Pas and validate Defensive Driver course Completed = No
	 * 5. rate and bind
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-478", "PAS-477"})
	public void pas478_AddDrivers(@Optional("AZ") String state) {

		pas478_AddDriversBody(getPolicyType());
	}

	/**
	 * @author Megha Gubbala
	 * @name Update Drivers service, check info.
	 * @scenario
	 * 1. Create policy on Pas.
	 * 2. To add driver send request first name, middle name, last name, suffix and Date of Birth < 26
	 * 3. run add driver service
	 * 4. go to Pas ui and verify if driver is added
	 * 5. update driver with these parameters  stateLicensed,licenseNumber,gender,relationToApplicantCd,maritalStatusCd,ageFirstLicensed
	 * 6. Go TO pas UI and verify Most Recent GPA = None and Smart Driver Course = No
	 * 6. Rate and Bind
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-477"})
	public void pas477_UpdateDrivers(@Optional("AZ") String state) {

		pas477_UpdateDriversBody(getPolicyType());
	}
}


