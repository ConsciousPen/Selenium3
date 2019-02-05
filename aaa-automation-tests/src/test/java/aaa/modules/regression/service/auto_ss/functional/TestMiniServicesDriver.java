package aaa.modules.regression.service.auto_ss.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.text.ParseException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesDriversHelper;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMiniServicesDriver extends TestMiniServicesDriversHelper {

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
	@StateList(states = {Constants.States.AZ})
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
	@StateList(states = {Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-482"})
	public void pas482_ViewDriverServiceOrderOfDriver(@Optional("AZ") String state) {
		TestData td = getTestSpecificTD("TestData1");
		pas482_ViewDriverServiceOrderOfDriverBody(td);
	}

	/**
	 * @author Bob Van
	 * @name View Drivers service, status pending remove/add 2.
	 * @scenario
	 * 1. create a policy 3d. 2afr 1 nafr
	 * 2. create endorsement outside of PAS
	 * 3. delete one driver afr
	 * 4. add a driver through the service
	 * 5. Update driver with required filed
	 * 6. Hit View Driver service verify order.
	 *    driverStatus 'active' should come before any 'pendingAdd' which should come before any 'pendingRemove'
	 * 7. Rate and Bind.
	 * 8. Create new endorsement.
	 * 9. Delete the newest driver.
	 * 10.Hit View Driver service verify order.
	 *    driverStatus 'active' should come before any 'pendingAdd' which should come before any 'pendingRemove'
	 *@scenario PAS-18457: try to remove driver with code RD1003/RD1004 (driverStatus will be updated to driverTypeChanged), and validate that driverTypeChanged drivers are displayed after pending drivers
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14653", "PAS-14470", "PAS-18457"})
	public void pas14653_ViewDriverServiceOrderOfPendingDelete(@Optional("VA") String state) {
		pas14653_ViewDriverServiceOrderOfPendingDeleteBody();
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
	public void pas14591_AddDriversUnhappyAge(@Optional("AZ") String state) throws ParseException {
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
	 *
	 * @author Maris Strazds
	 * @name validate that revert option is available for removed drivers ("PAS-18672", "PAS-18672", "PAS-18643")
	 * @scenario
	 * 1. Retrieve policy with 7 vehicles (max count)
	 * 2. Remove 1 vehicle with reason code RD1001 or RD1002 and validate that there is 'revert' option in response
	 * 3. Remove 1 vehicle with reason code RD1003 or RD1004 and validate that there is 'revert' option in response
	 * 4. Add 1 new driver so that max count of drivers is reached again and validate that driver removed with code RD1001/RD1002 (pendingRemoval) have 'revert' option
	 *    and driver removed with code RD1003/RD1004 (driverTypeChanged) has revert option
	 *    PAS-18643
	 * 5. Try to Revert delete of pendingRemoval driver when there already is max count of drivers (i.e driver without Revert option) ----> I receive error
	 * NOTE: step 5 is not applicable to driverTypeChanged driver as it always should have revert option
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9662", "PAS-18672", "PAS-18672", "PAS-18643"})
	public void pas9662_maxDrivers(@Optional("VA") String state) {

		pas9662_maxDriversBody(getPolicyType());
		mainApp().close();
		pas18672_driversRevertOptionForDeleteBody();
	}

	/**
	 * @author Maris Strazds
	 * @name validate revert option when driver is not updated before removal (removalReasonCode RD1001/RD1002)
	 * @scenario
	 *1. Create a policy in PAS with multiple drivers
	 *2. Create endorsement through service
	 *3. Do not Update and then Remove 1 driver through service with reason code RD101 or RD102 so that driver has status 'pendingRemoval'
	 *4. Run Cancel Remove Driver Transaction Service for 'pendingRemoval' driver
	 *5. Run viewDrivers service and validate response
	 *6. Retrieve endorsement in PAS and validate that both drivers are reverted ---> driver is reverted back to state as it was before removal
	 * Note: test also validates that Driver Level coverages after revert are the same as before revert
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-18643"})
	public void pas18643_CancelRemoveDriverWithoutChangesPendingRemoval(@Optional("VA") String state) {
		String removalReasonCode = getRandomDriverRemovalCode(true);
		pas18643_CancelRemoveDriverBody(false, removalReasonCode);
	}

	/**
	 * @author Maris Strazds
	 * @name validate revert option when driver is not updated before removal (removalReasonCode RD1003/RD1004)
	 * @scenario
	 *1. Create a policy in PAS with multiple drivers
	 *2. Create endorsement through service
	 *3. Do not Update and then Remove 1 driver through service with reason code RD103 or RD104 so that driver has status 'driverTypeChanged' and it is changed to NAFR
	 *4. Run Cancel Remove Driver Transaction Service for 'driverTypeChanged' driver
	 *5. Run viewDrivers service and validate response
	 *6. Retrieve endorsement in PAS and validate that both drivers are reverted ---> driver is reverted back to state as it was before removal
	 * Note: test also validates that Driver Level coverages after revert are the same as before revert
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-18643"})
	public void pas18643_CancelRemoveDriverWithoutChangesDriverTypeChanged(@Optional("VA") String state) {
		String removalReasonCode = getRandomDriverRemovalCode(false);
		pas18643_CancelRemoveDriverBody(false, removalReasonCode);
	}

	/**
	 * @author Maris Strazds
	 * @name validate revert option when driver is updated before removal (removalReasonCode RD1001/RD1002)
	 * @scenario
	 *1. Create a policy in PAS with multiple drivers
	 *2. Create endorsement through service
	 *3. Update and then Remove 1 driver through service with reason code RD101 or RD102 so that driver has status 'pendingRemoval'
	 *4. Run Cancel Remove Driver Transaction Service for 'pendingRemoval' driver
	 *5. Run viewDrivers service and validate response
	 *6. Retrieve endorsement in PAS and validate that both drivers are reverted ---> driver is reverted back to state as it was after update and before removal
	 * Note: test also validates that Driver Level coverages after revert are the same as before revert
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-18643"})
	public void pas18643_CancelRemoveDriverWithChangesPendingRemoval(@Optional("VA") String state) {
		String removalReasonCode = getRandomDriverRemovalCode(true);
		pas18643_CancelRemoveDriverBody(true, removalReasonCode);
	}

	/**
	 * @author Maris Strazds
	 * @name validate revert option when driver is not updated before removal (removalReasonCode RD1003/RD1004)
	 * @scenario
	 *1. Create a policy in PAS with multiple drivers
	 *2. Create endorsement through service
	 *3. Update and then Remove 1 driver through service with reason code RD103 or RD104 so that driver has status 'driverTypeChanged' and it is changed to NAFR
	 *4. Run Cancel Remove Driver Transaction Service for 'driverTypeChanged' driver
	 *5. Run viewDrivers service and validate response
	 *6. Retrieve endorsement in PAS and validate that both drivers are reverted ---> driver is reverted back to state as it was after update and before removal
	 * Note: test also validates that Driver Level coverages after revert are the same as before revert
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-18643"})
	public void pas18643_CancelRemoveDriverWithChangesDriverTypeChanged(@Optional("VA") String state) {
		String removalReasonCode = getRandomDriverRemovalCode(false);
		pas18643_CancelRemoveDriverBody(true, removalReasonCode);
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
	public void pas14474_UpdateSpouseDriver(@Optional("AZ") String state) {

		pas14474_UpdateSpouseDriverBody(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Transaction Information For Endorsements outside of PAS - Add Driver
	 * @scenario 1. Create policy.
	 * 2. Start do endorsement outside of PAS.
	 * 3. Hit "Transaction History Service". Check if response is empty.
	 * 4. Add Driver.
	 * 5. Hit "Transaction History Service". Check new driver info.
	 * 6. Update for bind.
	 * 7. Rate endorsement and Bind.
	 * 8. Create endorsement outside of PAS
	 * 9. Hit "Transaction History Service". Check new driver info. Update for bind.
	 * 10. Bind endorsement.
	 * 11. Hit "Transaction History Service". Check if response is empty.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-16481"})
	public void pas9493_TransactionInformationForEndorsementsAddDriver(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas16481_TransactionInformationForEndorsementsAddDriverBody(softly)
		);
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
	 *
	 * @ Jovita Pukenaite: PAS-17503
	 * 9. Remove Spouse.
	 * 10. Check the FNI driver status.
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA, Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14475", "PAS-17503"})
	public void pas14475_NameInsuredMaritalStatus(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14475_NameInsuredMaritalStatusBodyT(softly, true, "SSS")
		);
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
	 * 9. Remove spouse.
	 * 10. Check the FNI driver status.
	 *
	 * @ Jovita Pukenaite: PAS-17503
	 * 9. Remove Spouse.
	 * 10. Check the FNI driver status.
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14475", "PAS-17503"})
	public void pas14475_NameInsuredMaritalStatusDSS(@Optional("AZ") String state) {
		assertSoftly(softly ->
				pas14475_NameInsuredMaritalStatusBodyT(softly, true, "DSS")
		);
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
	 * 9. Remove spouse.
	 * 10. Check the FNI driver status.
	 *
	 * @ Jovita Pukenaite: PAS-17503
	 * 9. Remove Spouse.
	 * 10. Check the FNI driver status.
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14475", "PAS-17503"})
	public void pas14475_NameInsuredMaritalStatusWSS(@Optional("AZ") String state) {
		assertSoftly(softly ->
				pas14475_NameInsuredMaritalStatusBodyT(softly, true, "WSS")
		);
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
	 * 9. Remove spouse.
	 * 10. Check the FNI driver status.
	 *
	 * @ Jovita Pukenaite: PAS-17503
	 * 9. Remove Spouse.
	 * 10. Check the FNI driver status.
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14475", "PAS-17503"})
	public void pas14475_NameInsuredMaritalStatusPSS(@Optional("AZ") String state) {
		assertSoftly(softly ->
				pas14475_NameInsuredMaritalStatusBodyT(softly, true, "PSS")
		);
	}

	/**
	 * @author Sabra Domeika
	 * @name Default Marital Status - state deviations for multiple married statuses
	 * @scenario1
	 * 1. Create policy on Pas with the FNI set to non-married status
	 * 2. Create endorsement outside of PAS
	 * 3. Add driver.
	 * 4. Validate the driver's metadata allows all marital statuses.
	 * 5. Set the driver to a married relationship.
	 * 6. Validate the driver's metadata restricts marital statuses.
	 * 7. Update the driver's marital status to a married status.
	 * 8. Validate that the FNI marital status has been updated accordingly.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-16548"})
	public void pas16548_NamedInsuredMaritalStatus_MultipleMarital(@Optional("MD") String state) {
		assertSoftly(softly ->
				pas16548_NamedInsuredMaritalStatus_MultipleMaritalBody()
		);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Named Insured and the Relationship to the FNI - FNI is Equivalent to Married
	 * @scenario1
	 * 1. Create policy on Pas with the FNI set to Married status
	 * 2. Create endorsement outside of PAS
	 * 3. Add driver, update like SP.
	 * 4. Check if MS for new driver the same like FNI - Married.
	 * @scenario2
	 * 1. FNI (Married), create new endorsement outside of PAS
	 * 2. Add driver, update like RDP.
	 * 3. Check if MS for new driver the same like FNI - Married.
	 * @scenario3
	 * 4. Update FNI Marital Status to RDP
	 * 5. Bind endorsement.
	 * 6. Create new endorsement outside of PAS
	 * 7. Add driver, update like SP.
	 * 8. Check if MS for new driver the same like FNI = RDP.
	 * @scenario4
	 * 1. FNI(RDP) ,create new endorsement outside of PAS
	 * 2. Add driver, update like RDP.
	 * 3. Check if MS for new driver the same like FNI - RDP.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-16610"})
	public void pas16610_NamedInsuredAndTheRelationshipWhenFniEquivalentToMarried(@Optional("MD") String state) {
		pas16610_NamedInsuredAndTheRelationshipWhenFniEquivalentToMarriedBody();
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
	public void pas16696_AddANameInsuredSameDayPolicyEffectiveDate(@Optional("VA") String state) {
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
	public void pas16696_AddANameInsuredSameDayNotPolicyEffectiveDate(@Optional("VA") String state) {
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
	@StateList(states = {Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-482,Pas-15495"})
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
	public void pas14640_NotNamedInsuredAvailableForRatingHappyPath(@Optional("VA") String state) {
		pas14640_NotNamedInsuredAvailableForRatingHappyPathBody();
	}

	/**
	 * @author Maris Strazds
	 * @name Remove Driver - Hard Stop and Don't Remove
	 * @scenario
	 * 1. Create a policy in PAS with multiple drivers
	 * 2. Create endorsement through service
	 * 3. Run Remove Driver Service with the reason Rule RD1005 for Driver 1 and validate that I receive error message
	 * 4. Run viewEndorsementDrivers service and check that response is the same as before removeDriver action (and driverStatus has not changed)
	 * 5. Run Remove Driver Service with the reason Rule RD1006 for Driver 1 and validate that I receive error message
	 * 6. Run viewEndorsementDrivers service and check that response is the same as before removeDriver action (and driverStatus has not changed)
	 * 7. Run View driver assignments service and validate that driver 1 and driver 2 are available for assignment (assignment has not changed)
	 * 8. Open Endorsement in PAS an validate that both drivers are NOT removed
	 * 9. Rate and bind the policy through service
	 * 10. Run view policy drivers service and validate that the drivers are NOT removed (response is the same as before endorsement)
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14642"})
	public void pas14642_NotNamedInsuredAvailableForRatingHardStop(@Optional("VA") String state) {
		pas14642_NotNamedInsuredAvailableForRatingHardStopBody();
	}

	/**
	 * @author Maris Strazds
	 * @name Remove Driver - But Not Really - Not Available for Rating
	 * @scenario
	 * 1. Create a policy in PAS with multiple drivers
	 * 2. Create endorsement through service
	 * 3. Run Remove Driver Service with the reason Rule RD1003 for Driver 1
	 * 4. Validate that driverStatus in response is changed to "driverTypeChanged" for the driver 1, driver is change to "Not Available for Rating", reason is "Other" AND if there is other text - put rule
	 * 5. Run Remove Driver Service with the reason Rule RD1004 for Driver 2
	 * 6. Validate that driverStatus in response is changed to "driverTypeChanged" for the driver 1, driver is change to "Not Available for Rating", reason is "Other" AND if there is other text - put rule
	 * 7. Run View driver assignments service and validate that driver 1 and driver 2 are not available for assignment (response should not contain Driver at all in any section) (because they are Not available for Rating)
	 * 8. Open Endorsement in PAS an validate that both drivers are Updated
	 * 9. Rate and bind the policy through service
	 * 10. Run view policy drivers service and validate that the drivers are Updated
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14641", "PAS-18457"})
	public void pas14641_NotNamedInsuredUpdateToNotAvailableForRating(@Optional("VA") String state) {
		pas14641_NotNamedInsuredUpdateToNotAvailableForRatingBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name Remove Driver Transaction History
	 * @scenario
	 * 1. Create a policy in PAS with multiple drivers
	 * 2. Create endorsement through service
	 * 3. Run Remove Driver Service with the reason Rule RD1001 for Driver 2
	 * 4. remove driver
	 * 5. Run transaction history service
	 * 6. Validate that transaction history service response shows diver remove status
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14963"})
	public void pas14963_remove_driver_transaction_history(@Optional("VA") String state) {
		pas14963_remove_driver_transaction_historyBody(getPolicyType());
	}

	/**
	 * @author Megha Gubbala
	 * @name Relationship to FNI and the hard stop
	 * @scenario
	 * 1. Create a policy in PAS.
	 * 2. Create endorsement through service
	 * 3. Add driver through service
	 * 4. update driver Relationship to FNI Employee and verify error message.
	 * 5. rate the policy through service and verify error message.
	 * 6. Go to Pas Rate the policy from Pas
	 * 7. Run Bind service And verify message.
	 * 8. Repeat steps 4 to 7 For Relationship to FNI Other Resident Relative
	 * 9.Repeat steps 4 to 7 For Relationship to FNI Other.
	 *
	 *@ Jovita Pukenaite: PAS-18357
	 * 1. Create policy 1 FNI and 1 NI (Relationship to fni = EMP).
	 * 2. Add new Driver (Relationship to fni = CH).
	 * 3. Order reports.
	 * 4. Hit rate service and check if "Relationship to FNI needs review"
	 * error doesn't exist.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-16551", "PAS-17941"})
	public void pas16551_relationToFniHardStop(@Optional("VA") String state) {
		pas16551_relationToFniHardStopBody(getPolicyType());
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 * 	Note: Expected values for deathAndSpecificDisabilityInd and totalDisabilityIn:
	 *  null - coverage not available
	 * 	true - coverage available and selected
	 * 	false - coverage available but not selected
	 *
	 * 1) Create a policy in PAS
	 * 2) Create an endorsement through service
	 * 3) Add 1 driver and validate that "Death and Specific Disability Coverage" is defaulted to false and "Total Disability Coverage" is
	 *    defaulted to null in add driver and view driver responses
	 * 4) Rate endorsement and get total premium (this will be premium without "Death and Specific Disability Coverage" and "Total Disability Coverage")
	 * 5) Check in PAS Drivers tab that "Death and Specific Disability Coverage" is selected as NO
	 * 6) Check in PAS in Forms tab that "Death and Specific Disability Coverage" is not selected for the driver
	 * 7) Check in PAS in P&C tab that premium is the same as it was in DXP response (Step 4)
	 *
	 * 8) Update "Death and Specific Disability Coverage" coverage to Yes for the driver through service and
	 *    validate that coverage is applied in update driver and view driver responses. "Total Disability Coverage" is false
	 * 9) Calculate premium through service
	 * 10) Validate that "Death and Specific Disability Coverage" is updated in PAS Drivers tab and Forms tab and
	 *     that premium is increased because of the premium in P&C tab. "Total Disability Coverage" is No
	 *
	 * 11) Update "Total Disability Coverage" coverage to Yes for the driver through service and validate that coverage  "Death and Specific Disability Coverage"
	 *     and "Total Disability Coverage" is applied in update driver and view driver responses.
	 * 12) Calculate premium through service
	 * 13) validate that "Total Disability Coverage" is updated in PAS Drivers tab and Forms tab and that premium is increased because of "Total Disability Coverage" in P&C tab
	 *
	 * 14) Update "Death and Specific Disability Coverage" to No for Driver which has also "Total Disability Coverage" ---> "Total Disability Coverage" should be defaulted to null in responses
	 *
	 * Note: test also validates that "Death and Specific Disability Coverage" is available for Available for Rating drivers and
	 *       "Total Disability Coverage" is available only if "Death and Specific Disability Coverage" is selected
	 * */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14650", "PAS-17046", "PAS-14652", "PAS-17050", "PAS-17968"})
	public void pas14650_DeathAndSpecificDisabilityCovAndTotalDisabilityCovTC01(@Optional("VA") String state) {
		pas14650_DeathAndSpecificDisabilityCovAndTotalDisabilityCovTC01Body();
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 *  Note: Expected values for deathAndSpecificDisabilityInd and totalDisabilityIn:
	 *        null - coverage not available
	 *  	  true - coverage available and selected
	 *  	  false - coverage available but not selected
	 *
	 * 1) Create a policy in PAS with one
	 *    AFR Driver (with "Death and Specific Disability Coverage"),
	 *    one AFR Driver (without "Death and Specific Disability Coverage")
	 *    and one NAFR driver
	 * 2) Create an endorsement through service
	 * 3) Validate that "Death and Specific Disability Coverage" in viewDrivers response is the same as when policy was created (Step 1)
	 * 4) Validate that for NAFR driver coverages are no selected and are not available in responses
	 * 5) Change "Death and Specific Disability Coverage" to opposite value for all AFR drivers (YES to No, No to Yes) through service
	 * 6) Rate endorsement through service
	 * 7) Validate in PAS that coverages has been changed in Drivers tab and in Forms tab
	 * 8) Validate that premium in P&C tab is the same as it was in rate through service (Step 6)
	 * 9) Validate that premium has not changed if compered with policy when it was created (Step 1). (Because there still is one driver
	 *    without the coverage and one driver with coverage)
	 *
	 * 10) Add one driver through service and change it to NAFR in PAS (because not possible to change to NAFR through service)
	 * 11) Validate that "Death and Specific Disability Coverage" and "Total Disability Coverage" is not
	 *     available for the driver and it and they are not selected
	 *
	 * Note: test also validates that "Death and Specific Disability Coverage" is available for Available for Rating drivers and
	 *      "Total Disability Coverage" is available only if "Death and Specific Disability Coverage" is selected
	 *
	 * Note: test also validates metadata for fields "Death and Specific Disability Coverage" and "Total Disability Coverage" (PAS-16913)
	 * */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14650", "PAS-17046", "PAS-14652", "PAS-17050", "PAS-16913", "PAS-17968"})
	public void pas14650_DeathAndSpecificDisabilityCovAndTotalDisabilityCovTC02(@Optional("VA") String state) {
		pas14650_DeathAndSpecificDisabilityCovAndTotalDisabilityCovTC02Body();
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario
	 *  Note: Expected values for deathAndSpecificDisabilityInd and totalDisabilityIn:
	 *        null - coverage not available
	 *  	  true - coverage available and selected
	 *  	  false - coverage available but not selected
	 *
	 * 1) Create a policy in PAS with one
	 *    AFR Driver (with "Total Disability Coverage"),
	 *    one AFR Driver (without "Total Disability Coverage")
	 *    and one NAFR driver
	 * 2) Create an endorsement through service
	 * 3) Validate that "Total Disability Coverage" in viewDrivers response is the same as when policy was created (Step 1)
	 * 4) Validate that for NAFR driver both coverages are no selected and are not available in responses
	 * 5) Change "Total Disability Coverage" to opposite value for all AFR drivers (YES to No, No to Yes) through service
	 * 6) Rate endorsement through service
	 * 7) validate in PAS that coverages has been changed in Drivers tab and in Forms tab
	 * 8) validate that premium in P&C tab is the same as it was in rate through service (Step 6)
	 * 9) validate that premium has not changed if compered with policy when it was created (Step 1). (Because there still is one driver
	 *    without the coverage and one driver with coverage)
	 *
	 * 10) Add one driver through service and change it to NAFR in PAS (because not possible to change to NAFR through service)
	 * 11) Validate that "Death and Specific Disability Coverage" and "Total Disability Coverage" is not
	 *     available for the driver and it and they are not selected
	 *
	 * Note: test also validates that "Death and Specific Disability Coverage" is available for Available for Rating drivers and
	 *      "Total Disability Coverage" is available only if "Death and Specific Disability Coverage" is selected
	 * */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14650", "PAS-17046", "PAS-14652", "PAS-17050", "PAS-17968"})
	public void pas14650_DeathAndSpecificDisabilityCovAndTotalDisabilityCovTC03(@Optional("VA") String state) {
		pas14650_DeathAndSpecificDisabilityCovAndTotalDisabilityCovTC03Body();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Removing a driver - Create a Task?
	 * @scenario 1. Create policy.
	 * 2. Start do endorsement outside of PAS.
	 * 3. Add new Driver CH. Update that driver.
	 * 4. Order report.
	 * 5. Try order reports for existing driver. Check error.
	 * 6. Delete Driver.
	 * 7. Check if Task was created.
	 * 8. Rate and Bind.
	 * 9. Create new endorsement outside of PAS.
	 * 10. Add new Driver CH. Update.
	 * 11. Delete new Driver.
	 * 12. Check if Task wasn't created.
	 * 13. Create endorsement.
	 * 14. Add new Driver, rate and bind endorsement.
	 * 15. Create endorsement and try to order report
	 * for previous added driver. Check error.
	 *
	 * Repeat the same scenario with SP drivers.
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-16578", "PAS-17933", "PAS-17957"})
	public void pas16578_removeDriverCheckIfTaskWasCreated(@Optional("VA") String state) {

		pas16578_removeDriverCheckIfTaskWasCreatedBody();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Order report error / too old (earlier than 1900) Driver error
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS
	 * 3. Add new Driver (with "no hint" from the stubs). Update that driver.
	 * 4. Order report. Check response in dxp and PAS.
	 * 5. Delete Driver.
	 * 6. Try add new Driver with a birth year earlier than 1900
	 * 7. Check error.
	 * 8. Check if Driver wasnt added.
	 * 9. Try to add one more time with 1900
	 * 10. No errors, check if driver was added.
	 * 11. Rate and Bind
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-17769", "PAS-17658"})
	public void pas17769_tooOldDriverErrorAndNoHintFromReportResponse(@Optional("VA") String state) {

		pas17769_tooOldDriverErrorAndNoHintFromReportResponseBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name Driver MetaData Service
	 * @scenario 1. Create policy. with 1 Afr and 1 Nafr
	 * 2. Create endorsement outside of PAS
	 * 3. run driver meta data service
	 * 4. Verify ADB is visible true for AFR and disable to NAFR driver
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"pas17641"})
	public void pas17641_MetaDataServiceDriverAddADB(@Optional("AZ") String state) {

		TestData td = getTestSpecificTD("TestData");
		assertSoftly(softly ->
				pas17641_MetaDataServiceDriverAddADBBody(softly, getPolicyType(), td)
		);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name The "Age first licensed" cannot be greater than the age of the driver
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS
	 * 3. Add new Driver. Update and set "Age First Licensed" to an age greater than their DOB.
	 * 4. Check the error message.
	 * 5. Set "Age First Licensed" = DOB.
	 * 6. Check if error message is not displaying.
	 * 7. Update "Age First Licensed" < DOB.
	 * 8. Order report and bind the endorsement.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-19768"})
	public void pas19768_ageFirstLicensedCannotBeGreaterThanDob(@Optional("VA") String state) {

		pas19768_ageFirstLicensedCannotBeGreaterThanDobBody();
	}

}


