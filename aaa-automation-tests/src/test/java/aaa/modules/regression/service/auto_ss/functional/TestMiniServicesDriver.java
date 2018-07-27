package aaa.modules.regression.service.auto_ss.functional;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.text.ParseException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.TestMiniServicesDriversHelper;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMiniServicesDriver extends TestMiniServicesDriversHelper {

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
	 * @author Maris Strazds
	 * @name Add Drivers service, check info.
	 * @scenario 1
	 * 1. Create policy on PAS
	 * 2. Create endorsement through service
	 * 3. To add driver send request first name, middle name, last name, suffix and Date of Birth
	 *      Date of Birth should be so that age is 1 year LESS than the minimum age for the particular state:
	 *      |=================================|
	 *      | State	        | Minimum Age     |
	 *      |=================================|
	 *      | Kansas	    |     15          |
	 *      |---------------------------------|
	 *      | Montana	    |     15          |
	 *      |---------------------------------|
	 *      | South Dakota  |	  14          |
	 *      |---------------------------------|
	 *      | All Others	|     16          |
	 *      |=================================|
	 * 4. Run add driver service and verify that I get an error AND this is not a hard stop
	 * 5. Go to Pas UI and verify that driver is NOT added
	 * 6. Hit view driver service and verify that driver is NOT added
	 * ---------------
	 * 7. Repeat steps 3-6 with driver age THE SAME as the minimum age for the particular state (see the table above) and validate that driver IS added and no there is no error and hard stop
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14591"})
	public void pas14591_AddDriversUnhappyAge(@Optional("") String state) throws ParseException {
		pas14591_AddDriversUnhappyAgeBody(getPolicyType());
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
	public void pas477_UpdateDrivers(@Optional("DC") String state) {

		pas477_UpdateDriversBody(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Validation of max drivers on DXP
	 * @scenario 1. Create a policy with 5 drivers
	 * 2. Check view drivers service response (true)
	 * 3. Create endorsement outside of PAS
	 * 4. Hit view driver endorsement service (true).
	 * 5. Add Driver 6, update new driver.
	 * 6. Hit view driver endorsement service (true).
	 * 7. Add Driver 7, update new driver.
	 * 8. Hit view driver endorsement service (false).
	 * 9. Try add one more driver. Check error response.
	 * 10. Rate and Bind endorsement.
	 * 11. Create second endorsement outside of PAS.
	 * 12. Hit view driver endorsement service (false).
	 * 13. Try add driver. Check error.
	 * 14. Rate and Bind endorsement.
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9662"})
	public void pas9662_maxDrivers(@Optional("VA") String state) {

		pas9662_maxDriversBody(getPolicyType());
	}

	/**
	 * @author Dakota Berg
	 * @name Test Meta Data Service for Drivers
	 * @scenario 1. Create a customer and policy
	 * 2. Initiate an endorsement outside of PAS
	 * 3. Add a driver outside of PAS
	 * 4. Run Meta Data Service for Drivers
	 * 5. Verify that the correct field options display
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15076"})
	public void pas15076_MetadataServiceDriver(@Optional("VA") String state) {

		pas15076_MetadataServiceDriverBody();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Validate Driver License By State and Age First Licensed < 14
	 * @scenario 1. Create Policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Try Update existing Driver add new State and a DL.
	 * Update  Age First Licensed, add < 14.
	 * 4. Check error. DL should be updated.
	 * 5. Update existing Driver add new State and a DL which is not correct by State.
	 * Update Age First Licensed, add > 14.
	 * 6. Check error, the years should be successfully updated.
	 * 7. Update existing Driver add new State and a DL which is not correct by State.
	 * Update Age First Licensed, add < 14.
	 * 8. Check the errors.
	 * 9. Add new driver.
	 * 10. Repeat the same scenario for the newly added driver.
	 * 11. Rate and Bind.
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13301", "PAS-14633"})
	public void pas13301_validateDriverLicenseAndAgeFirstLicensed(@Optional("VA") String state) {

		pas13301_validateDriverLicenseAndAgeFirstLicensedBody();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Unique Driver Licenses - error 200008
	 * @scenario 1. Create Policy with two drivers.
	 * 2. Start do endorsement outside of PAS.
	 * 3. Update first driver with the same DL like the second driver have.
	 * 4. Update with correct DL.
	 * 5. Bind endorsement. Create new one.
	 * 6. Add new driver.
	 * 7. Add DL which already exist.
	 * 8. Update with new DL.
	 * 9. Rate and Bind.
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15373"})
	public void pas15373_uniqueDriverLicenses(@Optional("VA") String state) {

		pas15373_uniqueDriverLicensesBody(getPolicyType());
	}

	/**
	 * @author Maris Strazds
	 * @name Test Report Ordering for Endorsement (Named Insured)
	 * @scenario
	 * 1. Create a policy in PAS
	 * 2. Create an endorsement through service
	 * 3. Add Driver 1 (Named Insured) through service with MVR status =  Hit - Activity Found, CLUE status = processing complete, results clear
	 * 4. Run the Report Order Service for MVR/CLUE
	 * 5. Open the Endorsement in PAS, navigate to "Driver Activity Reports" tab and validate that MVR/CLUE reports have been ordered successfully with no errors
	 * 6. Validate that I receive the report response
	 *          AND it is viewable in PAS (pdf)
	 *          AND it is reconciled in PAS
	 *          AND a positive response is provided
	 * 7. Rate and bind the policy
	 * 8. Rate and Bind
	 * 9. Create an endorsement through service
	 * 10. Add Driver 2 (Named Insured) through service with MVR status =  Clear, CLUE status = processing complete, with results information
	 * 11. Repeat steps 4 - 8
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15077"})
	public void pas15077_orderReports_endorsement(@Optional("") String state) {

		pas15077_orderReports_endorsementBody(getPolicyType());
	}

	/**
	 * @author Maris Strazds
	 * @name Test Report Ordering for Endorsement (not a Named Insured)
	 * @scenario
	 * 1. Create a policy in PAS
	 * 2. Create an endorsement through service
	 * 3. Add Driver 1 (not a Named Insured) through service with MVR status =  Hit - Activity Found, CLUE status = processing complete, results clear
	 * 4. Run the Report Order Service for MVR/CLUE
	 * 5. Open the Endorsement in PAS, navigate to "Driver Activity Reports" tab and validate that MVR/CLUE reports have been ordered successfully with no errors
	 * 6. Validate that I receive the report response
	 *          AND it is viewable in PAS (pdf)
	 *          AND it is reconciled in PAS
	 *          AND a positive response is provided
	 * 7. Rate and bind the policy
	 * 8. Rate and Bind
	 * 9. Create an endorsement through service
	 * 10. Add Driver 2 (not Named Insured) through service with MVR status =  Clear, CLUE status = processing complete, with results information
	 * 11. Repeat steps 4 - 8
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-16694"})
	public void pas16694_orderReports_not_Named_Insured_endorsement(@Optional("") String state) {

		pas16694_orderReports_not_Named_Insured_endorsementBody(getPolicyType());
	}

	/**
	 * @author Bob Van
	 * @name Update Drivers service, set marital status.
	 * @scenario
	 * 1. Create policy on Pas.
	 * 2. Create endorsement outside of PAS
	 * 2. Add 2nd driver outside of PAS
	 * 3. Update 2nd driver as spouse outside of PAS
	 * 4. Verify married status in update response
	 * 5. Verify married status in view driver response
	 * 6. Verify PAS pended endorsement general tab data
	 * 7. Verify PAS pended endorsement driver tab data
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14474"})
	public void pas14474_UpdateSpouseDriver(@Optional("") String state) {
		pas14474_UpdateSpouseDriverBody(getPolicyType());
	}

	/**
	 * @author Megha Gubbala
	 * @name Update Drivers service, set marital status.
	 * @scenario1
	 * 1. Create policy on Pas.
	 * 2. Create endorsement outside of PAS
	 * 3. update marital status of FNI Single
	 * 4. Add 1 more drive NI as spouse
	 * 5. Update Driver
	 * 6. Verify married status of primary driver is changed to Married
	 * 7. Verify married status in view driver response
	 * 8. Verify PAS pended endorsement driver tab data status is married
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14475"})
	public void pas14475_NameInsuredMaritalStatus(@Optional("") String state) {
		pas14475_NameInsuredMaritalStatusBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name Update Drivers service, set marital status.
	 * @scenario2
	 * 1. Create policy on Pas.
	 * 2. Create endorsement outside of PAS
	 * 3. update marital status of FNI Divorced
	 * 4. Add 1 more drive NI as spouse
	 * 5. Update Driver
	 * 6. Verify married status of primary driver is changed to Married
	 * 7. Verify married status in view driver response
	 * 8. Verify PAS pended endorsement driver tab data status is married
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14475"})
	public void pas14475_NameInsuredMaritalStatusDSS(@Optional("") String state) {
		pas14475_NameInsuredMaritalStatusFNIIsDSSBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name Update Drivers service, set marital status.
	 * @scenario3
	 * 1. Create policy on Pas.
	 * 2. Create endorsement outside of PAS
	 * 3. update marital status of FNI Widow
	 * 4. Add 1 more drive NI as spouse
	 * 5. Update Driver
	 * 6. Verify married status of primary driver is changed to Married
	 * 7. Verify married status in view driver response
	 * 8. Verify PAS pended endorsement driver tab data status is married
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14475"})
	public void pas14475_NameInsuredMaritalStatusWSS(@Optional("") String state) {
		pas14475_NameInsuredMaritalStatusFNIIsWSSBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name Update Drivers service, set marital status.
	 * @scenario4
	 * 1. Create policy on Pas.
	 * 2. Create endorsement outside of PAS
	 * 3. update marital status of FNI PSS
	 * 4. Add 1 more drive NI as spouse
	 * 5. Update Driver
	 * 6. Verify married status of primary driver is changed to Married
	 * 7. Verify married status in view driver response
	 * 8. Verify PAS pended endorsement driver tab data status is married
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14475"})
	public void pas14475_NameInsuredMaritalStatusPSS(@Optional("") String state)
	{
		assertSoftly(softly ->
		pas14475_NameInsuredMaritalStatusFNIIsPSSBody(softly)
				);
	}


	/**
	 * @author Megha Gubbala
	 * @name Update Drivers service,Error insured score.
	 * @scenario4
	 * 1. Create policy on Pas.
	 * 2. Create endorsement outside of PAS on the same day policy effective day
	 * 3. Add drive NI as spouse
	 * 4. Update Driver
	 * 5. Verify Error message "Need Insurance Score Order (AAA_SS9192341)"
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-16696"})
	public void pas16696_AddANameInsuredSameDayPolicyEffectiveDate(@Optional("") String state) {
		pas16696_AddANameInsuredSameDayPolicyEffectiveDateBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name Update Drivers service,Error insured score.
	 * @scenario4
	 * 1. Create policy on Pas.
	 * 2. Create future dated endorsement outside of PAS +5 days
	 * 3. Add drive NI as spouse
	 * 4. Update Driver
	 * 5. We should not see message Error message "Need Insurance Score Order (AAA_SS9192341)"
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-16696"})
	public void pas16696_AddANameInsuredSameDayNotPolicyEffectiveDate(@Optional("") String state) {
		pas16696_AddANameInsuredSameDayNotPolicyEffectiveDateBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name View Drivers service, remove driver indicator.
	 * @scenario4
	 * 1. Create policy on Pas with 5 drives FNI ,AFR and NI, afr and nni, nni and nafr,nni excuded driver.
	 * 2. Run view driver service
	 * 3. verify remove indicator should show only for afr driver and not others
	 * 4. Create endorsement outside of PAS
	 * 5. Add 1 more drive NI as spouse
	 * 5. Update Driver
	 * 6. Run view driver service
	 * 7. verify remove indicator should show only for afr driver and not others
	 *  *      |============================================|
	 * 	 *      | Driver Type            | Remove Status    |
	 * 	 *      |===========================================|
	 * 	 *      | First Named Insured    |     no           |
	 * 	 *      |-------------------------------------------|
	 * 	 *      | Named Insured	         |    no            |
	 * 	 *      |-------------------------------------------|
	 * 	 *      | A for Rating Driver    |Yes               |
	 * 	 *      |-------------------------------------------|
	 * 	 *      | Not A f R Driver	     |    no            |
	 * 	        |-------------------------------------------|
	 * 	 *      | Excluded Driver	     |     no           |
	 * 	 *      |============================================
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-482"})
	public void pas15513_ViewDriverRemoveDriverIndicator(@Optional("AZ") String state) {
		TestData td = getTestSpecificTD("TestData2");
		pas15513_ViewDriverRemoveDriverIndicatorBody(td, getPolicyType());

	}

	/**
	 * @author Maris Strazds
	 * @name Remove Driver - Not a Named Insured, Available for Rating - Happy Path
	 * @scenario
	 * 1. Create a policy in PAS with multiple drivers
	 * 2. Create endorsement through service
	 * 3. Run Remove Driver Service with the reason Rule RD1001 for Driver 1
	 * 4. Validate that driverStatus in response is "pendingRemove" for the driver 1
	 * 5. Run Remove Driver Service with the reason Rule RD1002 for Driver 2
	 * 6. Validate that driverStatus in response is "pendingRemove" for the driver 2
	 * 7. Run View driver assignments service and validate that removed driver 1 and driver 2 are not available for assignment (response should not contain Driver at all in any section)
	 * 8. Open Endorsement in PAS an validate that both drivers are removed
	 * 9. Rate and bind the policy through service
	 * 10. Run view policy drivers service and validate that the drivers are removed (not present in response)
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14640"})
	public void pas14640_Not_a_Named_Insured_Available_for_Rating_Happy_Path(@Optional("VA") String state){
		pas14640_Not_a_Named_Insured_Available_for_Rating_Happy_Path_Body();
	}

}


