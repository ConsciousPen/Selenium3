/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss.functional;

import static aaa.modules.regression.service.auto_ss.functional.preconditions.MiniServicesSetupPreconditions.MY_POLICY_USER_CONFIG_CHECK;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static toolkit.verification.CustomAssertions.assertThat;
import org.assertj.core.api.SoftAssertions;
import org.testng.ITestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.regression.service.auto_ss.functional.preconditions.MiniServicesSetupPreconditions;
import aaa.modules.regression.service.helper.TestMiniServicesNonPremiumBearingAbstract;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestMiniServicesNonPremiumBearing extends TestMiniServicesNonPremiumBearingAbstract {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Test(description = "Precondition", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void miniServicesEndorsementDeleteDelayConfigCheck() {
		assertSoftly(softly -> {
			miniServicesEndorsementDeleteDelayConfigCheckAssertion(softly, 2, "is null");
			miniServicesEndorsementDeleteDelayConfigCheckAssertion(softly, 0, " = 'AZ'");
			miniServicesEndorsementDeleteDelayConfigCheckAssertion(softly, 5, " = 'AZ'");
		});
	}

	private static void miniServicesEndorsementDeleteDelayConfigCheckAssertion(SoftAssertions softly, int i, String s) {
		softly.assertThat(DBService.get().getValue(String.format(MiniServicesSetupPreconditions.AAA_CUSTOMER_ENDORSEMENT_DAYS_CONFIG_CHECK, i, s)).get()).isNotEmpty();
	}

	@Test(description = "Precondition adding MyPolicy as a user for Digital", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void myPolicyUserAddedConfigCheck() {
		assertThat(DBService.get().getValue(MY_POLICY_USER_CONFIG_CHECK)).hasValue("MyPolicy");
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Email change through service
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Run a request in Swagger-UI
	 * 4. Check Transaction history contains "Email Updated - External System"  for a new endorsement
	 * 5. Check quote in Inquiry mode has new eMail address in General Tab and in Documents Tab
	 * 6. Do one more endorsement, issue
	 * Check there is no extra Actions for the product
	 * Check that number of document records in the DB before running the eMail update is equal to the number of document records after update
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-1441", "PAS-5986, PAS-343"})
	public void pas1441_emailChangeOutOfPas(@Optional("VA") String state) {

		CustomAssert.enableSoftMode();
		pas1441_emailChangeOutOfPasTestBody();
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Endorsement can be performed through service without sending Effective date
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Create an endorsement, issue
	 * 4. Check Green Button endorsement is allowed and there are no errors present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568"})
	public void pas6560_endorsementValidateAllowedNoEffectiveDate(@Optional("VA") String state) {

		pas6560_endorsementValidateAllowedNoEffectiveDate();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Endorsement can be performed through service when there is another completed endorsement
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Create an endorsement, issue
	 * 4. Check Green Button endorsement is allowed and there are no errors present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568"})
	public void pas6560_endorsementValidateAllowed(@Optional("VA") String state) {

		pas6560_endorsementValidateAllowed();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Endorsement can be performed through service when there is another User created Pended endorsement
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Start an endorsement created by user, but not finish (Pended Endorsement)
	 * 4. Check Green Button endorsement is allowed and there are no errors
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560"})
	public void pas6560_endorsementValidateAllowedPendedEndorsementUser(@Optional("VA") String state) {

		pas6560_endorsementValidateAllowedPendedEndorsementUser(getPolicyType());
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Endorsement can not be performed through service when there is Future Dated endorsement
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Create endorsement in the Future, issue
	 * 4. Check Endorsement is Not allowed and there is an error about OOSE or Future Dated Endorsement
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568"})
	public void pas6562_endorsementValidateNotAllowedFutureDatedEndorsement(@Optional("VA") String state) {

		pas6562_endorsementValidateNotAllowedFutureDatedEndorsement(getPolicyType());
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Endorsement can NOT be performed through service for NANO policy
	 * @scenario 1. Create customer
	 * 2. Create a NANO policy
	 * 3. Check Green Button endorsement is not allowed. There is a PolicyRules error about NANO
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568"})
	public void pas6562_endorsementValidateNotAllowedNano(@Optional("AZ") String state) {

		pas6562_endorsementValidateNotAllowedNano(getPolicyType(), state);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Endorsement can NOT be performed through service when there is Pended System Endorsement
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Start an endorsement created by System, but not finish (Pended Endorsement)
	 * 4. Check Green Button endorsement is not allowed. There is a PolicyRules error about System Pended Endorsement
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568"})
	public void pas6562_endorsementValidateNotAllowedPendedEndorsementSystem(@Optional("VA") String state) {

		pas6562_endorsementValidateNotAllowedPendedEndorsementSystem(getPolicyType());
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Endorsement can be performed through service when the vehicle has UBI
	 * @scenario 1. Create customer
	 * 2. Create a policy with a vehicle with UBI
	 * 3. Check Green Button endorsement is not allowed. There is a VehicleRules error about UBI
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568"})
	public void pas6562_endorsementValidateNotAllowedUBI(@Optional("AZ") String state) {

		pas6562_endorsementValidateNotAllowedUBI(getPolicyType());
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Email change through service
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Check Green Button endorsement is not allowed for the date outside of policy term
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568", "PAS-9337"})
	public void pas6562_endorsementValidateNotAllowedOutOfBound(@Optional("") String state) {

		pas6562_endorsementValidateNotAllowedOutOfBound(getPolicyType());
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test cannot delete User Created Pended Endorsement within delay period
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Create User Endorsement through service
	 * 4. Validate through service, that this endorsement cannot be deleted on creation date and new endorsement cannot be started
	 * 5. change date to Creation Date + delay
	 * 6. Validate through service, that this endorsement cannot be deleted and new endorsement cannot be started
	 * 7. change date to Creation Date + delay + 1
	 * Validate through service, that this endorsement can be deleted and new endorsement can be created
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-8784"})
	public void pas8784_endorsementValidateNotAllowedCustomer(@Optional("UT") String state) {

		pas8784_endorsementValidateNotAllowedCustomer(getPolicyType());
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test cannot delete Agent Created Pended Endorsement within delay period
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Create Agent Endorsement
	 * 4. Validate through service, that this endorsement can be deleted on creation date and new endorsement can be started
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-8784"})
	public void pas8784_endorsementValidateNoDelayAllowedAgent(@Optional("UT") String state) {

		pas8784_endorsementValidateNoDelayAllowedAgent(getPolicyType());
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test cannot delete System Created Pended Endorsement within delay period
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Create Agent Endorsement and convert it to System
	 * 4. Validate through service, that this endorsement cannot be deleted
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-8784"})
	public void pas8784_endorsementValidateNoDelayNotAllowedSystem(@Optional("UT") String state) {

		pas8784_endorsementValidateNoDelayNotAllowedSystem(getPolicyType());
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test different Config Versions for Delete User Endorsement Day Delay for AZ
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Create User Endorsement
	 * 4. Validate through service, that this endorsement can be deleted, since config is set to DateDelay = 0
	 * 5. Change date to Current date +10 (next config version start date)
	 * 6. Delete old User Endorsement and start new one
	 * 7. Shift time to Current date +5, since config is set to DateDelay = 5
	 * 8. Validate through service, that this endorsement cannot be deleted, since config is set to DateDelay = 5
	 * 9. Shift time to Current date +1 (new User Endorsment creation date + 6) since config is set to DateDelay = 5
	 * 10. Validate through service, that this endorsement can be deleted
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "miniServicesEndorsementDeleteDelayConfigCheck")
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-8784"})
	public void pas8784_endorsementValidateStateSpecificConfigVersioning(@Optional("AZ") String state) {

		pas8784_endorsementValidateStateSpecificConfigVersioning(getPolicyType());
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Endorsement can not be performed through service when there is Future Dated endorsement
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Create Pended System endorsement
	 * 4. run Start Endorsement Service
	 * 5. Check Endorsement is deleted and User Endorsement is created instead
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "miniServicesEndorsementDeleteDelayConfigCheck")
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-7332", "PAS-8785"})
	public void pas7332_deletePendingSystemEndorsementStartNewEndorsementThroughService(@Optional("") String state) {

		pas7332_deletePendingEndorsementStartNewEndorsementThroughService(getPolicyType(), "System");
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Endorsement can not be performed through service when there is Future Dated endorsement
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Create Pended Agent endorsement
	 * 4. run Start Endorsement Service
	 * 5. Check Endorsement is deleted and User Endorsement is created instead
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "miniServicesEndorsementDeleteDelayConfigCheck")
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-7332", "PAS-8785"})
	public void pas7332_deletePendingAgentEndorsementStartNewEndorsementThroughService(@Optional("AZ") String state) {

		pas7332_deletePendingEndorsementStartNewEndorsementThroughService(getPolicyType(), "Agent");
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Email change through service
	 * @scenario 1. Create customer
	 * 2. Create a policy with a vehicle with UBI
	 * 3. Check Green Button endorsement is not allowed. There is a VehicleRules error about UBI
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-8275"})
	public void pas8275_vinValidate(@Optional("") String state) {
		assertSoftly(softly ->
				pas8275_vinValidateCheck(softly, getPolicyType())
		);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Check if only active Vehicles are allowed using DXP
	 * @scenario 1. Create policy with two vehicles.
	 * 2. Check if the same vehicles are displayed in dxp server.
	 * 3. Initiate endorsement, and change VIN for one of the vehicles. Don't bind.
	 * 4. Check if the new vehicle, which wad added during endorsement is not displayed in dxp server.
	 * 5. Bind the endorsement.
	 * 6. Check if new vehicle is displayed, and the old one is not displayed anymore.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-8273", "PAS-7145"})
	public void pas8273_OnlyActiveVehiclesAreAllowed(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas8273_CheckIfOnlyActiveVehiclesAreAllowed(softly, getPolicyType())
		);
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
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11932"})
	public void pas11932_viewDrivers(@Optional("VA") String state) {

		pas11932_viewDriversInfo(getPolicyType(), state);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Check dxp server if Nano policy not returning any information about vehicle.
	 * @scenario 1. Create Nano policy.
	 * 2. Check dxp server, any info should not be displayed about vehicle.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-8273"})
	public void pas8273_NanoPolicyShouldNotReturnVehicleInfo(@Optional("AZ") String state) {

		pas8273_CheckIfNanoPolicyNotReturningVehicle(getPolicyType(), state);
	}

	/**
	 * @author Megha Gubbala
	 * @name Check dxp server To add vehicle.
	 * Create a Policy
	 * Create a pended endorsement
	 * Hit "add-vehicle" dxp server.
	 * Pass Pearches date and VIN to the service
	 * Go to pas open pended endorsement and go to vehicle tab
	 * Check the new vehicle is added with the vin number.
	 * @scenario
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-7082", "PAS-7145"})
	public void pas7082_AddVehicle(@Optional("AZ") String state) {

		pas7082_AddVehicle(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Check Start Endorsement info server response for Future policy
	 * @scenario 1. Create Future Policy.
	 * 2. Hit "start endorsement info" dxp server.
	 * 3. Check error message.
	 * 4. Start renew action.
	 * 5. Hit "start endorsement info" dxp server.
	 * 6. Check error message. Policy should be locked.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9337"})
	public void pas9337_StartEndorsementInfoServerResponseForFuturePolicy(@Optional("VA") String state) {

		pas9337_CheckStartEndorsementInfoServerResponseForFuturePolicy(getPolicyType());
	}

	/**
	 * @author Megha Gubbala
	 * @name Check Start Endorsement info server response for allow endorsements
	 * @scenario 1. Create active policy for NJ.
	 * 2. Hit dxp start-endorsement-info.
	 * 3. Verify the response State does not allow endorsements.
	 * 4. Hit "start endorsement info" dxp server.
	 * 5. Update the configuration from DB with effective date (6 days future).
	 * 6. change the server date to the 6 days future .
	 * 7. Hit "start endorsement info" dxp server.
	 * 8.Verify the response we should not see this message State does not allow endorsements.
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-9489"})
	public void pas9489_GreenButtonServiceStateAndProductConfigurationEffective(@Optional("PA") String state) {

		pas9337_CheckStartEndorsementInfoServerResponseErrorForEffectiveDate(getPolicyType());

	}

	/**
	 * @author Jovita Pukenaite
	 * @name Check Start Endorsement info server response for Cancel Policy
	 * @scenario 1. Create active policy.
	 * 2. Cancel policy.
	 * 3. Verify Policy status is 'Policy Cancelled'.
	 * 4. Hit "start endorsement info" dxp server.
	 * 5. Check error message.
	 * 6. Open the same policy again.
	 * 7. Do reinstatement +6d
	 * 8. Move time to the lapse period, +3d.
	 * 9. Hit "start endorsement info" dxp server.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9337"})
	public void pas9337_StartEndorsementInfoServerResponseForCancelPolicy(@Optional("VA") String state) {

		pas9337_CheckStartEndorsementInfoServerResponseForCancelPolicy(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Check Start Endorsement info server response for Expired Policy
	 * @scenario 1. Create active policy.
	 * 2. Change time to the policy expiration date +2d
	 * 3. Run policyUpdate job
	 * 4. Check if policy is expired.
	 * 5. Hit "start endorsement info" dxp server.
	 * 6. Check if error message is displaying.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9337"})
	public void pas9337_StartEndorsementInfoServerResponseForExpiredPolicy(@Optional("VA") String state) {

		pas9337_CheckStartEndorsementInfoServerResponseForExpiredPolicy(getPolicyType());
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check Policy Details service for Pending and Active policies
	 * @scenario 1. Create pending policy
	 * 2. Check policy details
	 * 3. Change date, run policyStatusUpdate
	 * 4. Check policy details
	 * @Megha Added Term and actual premium  //Pas-11809
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9716"})
	public void pas9716_policySummaryForPolicy(@Optional("AZ") String state) {

		pas9716_policySummaryForPolicy(getPolicyType(), state);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check Policy Details service for Active renewal
	 * @scenario 1. Create active policy
	 * 2. Run Renewal Part1
	 * 3. Check policy and renewal details
	 * 4. Run Renewal Part2
	 * 5. Check policy and renewal details
	 * 6. Make a payment for the renewal amount for the next term
	 * 7. change date to R, run policy status update job
	 * 8. Check policy and renewal details
	 * @Megha Added Term and actual premium //Pas-11809
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9716"})
	public void pas9716_policySummaryForActiveRenewal(@Optional("VA") String state) {

		pas9716_policySummaryForActiveRenewalBody(state);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check Policy Details service for Lapsed renewal
	 * @scenario 1. Create active policy
	 * 2. Run Renewal Part1
	 * 3. Check policy and renewal details
	 * 4. Run Renewal Part2
	 * 5. Check policy and renewal details
	 * 6. DONT Make a payment for the renewal amount for the next term
	 * 6.5. Check policy and renewal details
	 * 7. change date to R, run policy status update job
	 * 8. Check policy and renewal details
	 * 9. change date to R+15, run lapse job
	 * 10. Check policy and renewal details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9716"})
	public void pas9716_policySummaryForLapsedRenewal(@Optional("AZ") String state) {

		pas9716_policySummaryForLapsedRenewal(getPolicyType(), state);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check Conversion policy details
	 * @scenario 1. Create manual conversion policy
	 * 2. Check stub policy and renewal details
	 * 4. Run Renewal Part2
	 * 5. Check stub policy and renewal details
	 * 6. Make a payment for the renewal amount for the next term
	 * 7. change date to R, run policy status update job
	 * 8. Check policy and renewal details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9716"})
	public void pas9716_policySummaryForConversionManual(@Optional("AZ") String state) {

		pas9716_policySummaryForConversionManualBody();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9716"})
	public void pas9716_policySummaryForConversion(@Optional("VA") String state, ITestContext context) {

		pas9716_policySummaryForConversionBody("1.xml", context);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Policy lock unlock functionality using services / sessionId.
	 * @scenario 1. Create active policy.
	 * 2. Hit lock service with sessionId1. Check service status.
	 * 3. Hit start endorsement info service with sessionId1.
	 * 4. Hit stat endorsement info service with sessionId2. Check error.
	 * 5. Try to lock policy with sessionId2. Check error.
	 * 6. Go to policy in PAS.
	 * 7. Start do endorsement.
	 * 8. Check if policy is locked.
	 * 9. Hit Unlock service with sessionId2. Check error.
	 * 10. Hit Unlock service with sessionId1. Check service status.
	 * 11. Open policy in PAS again.
	 * 12.  Start do endorsement.
	 * 13. Check if policy is unlocked.
	 * 14. Try to lock policy using lock service. Check service status.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9456", "PAS-9455", "PAS-10825"})
	public void pas9456_9455_PolicyLockUnlockServices(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas9456_9455_PolicyLockUnlockServicesBody(softly)
		);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Retrieve lookup data service - Payment Methods
	 * @scenario 1. Add State Specific configurations for specific dates, which changes Default configuration's values
	 * 2. Add State Specific configurations for specific dates, which adds new values to it
	 * 3. Retrieve lookup values for the mentioned dates, check value
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9997"})
	public void pas9997_paymentMethodsLookup(@Optional("") String state) {

		pas9997_paymentMethodsLookup();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Retrieve lookup data service - Payment Plans
	 * @scenario 1. Add State Specific configurations for specific dates, which changes Default configuration's values
	 * 2. Add State Specific configurations for specific dates, which adds new values to it
	 * 3. Retrieve lookup values for the mentioned dates, check value
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9997"})
	public void pas9997_paymentPlansLookup(@Optional("") String state) {

		pas9997_paymentPlansLookup();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Check Vehicle status using view vehicle service/ check endorsement rate service
	 * @scenario 1. Create active policy with one vehicle.
	 * 2. Create Endorsement using dxp server.
	 * 3. Hit rate endorsement service.
	 * 4. Check premium amount in service and UI, check the endorsement status.
	 * 5. Add new vehicle.
	 * 6. Hit the view vehicle service.
	 * 7. Check the vehicles status.
	 * 8. Edit endorsement, add usage for new vehicle. Save it.
	 * 9. Hit rating service.
	 * 10. Check if premium amount from ui and from service is the same. Check endorsement status again.
	 * 11. Bind pended endorsement.
	 * 12. Hit the view vehicle service again.
	 * 13. Check if Pended vehicle status was changed.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9490", "PAS-479"})
	public void pas9490_ViewVehicleServiceCheckVehiclesStatus(@Optional("AZ") String state) {

		pas9490_ViewVehicleServiceCheckVehiclesStatus();
	}

	/**
	 * @author Megha Gubbala
	 * @name Check Vehicle vehicle service
	 * @scenario 1.Create a policy with 4 vehicles (1.PPA 2.PPA 3. Conversion Van 4. Trailer )
	 * 2.hit view vehicle service
	 * 3.get a response in right sequence
	 * 4.perform endorsement
	 * 5.add new vehicle (that will be pending)
	 * 6.hit view vehicle service
	 * 7.validate response shows pending vehicle first.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-10449"})
	public void pas10449_ViewVehicleServiceOrderOfVehicle(@Optional("VA") String state) {

		pas10449_ViewVehicleServiceCheckOrderOfVehicle(getPolicyType(), state);
	}

	/**
	 * @author Megha Gubbala
	 * @name Verify update vehicle service
	 * @scenario 1. Create active policy with one vehicle.
	 * 2. hit view vehicle service.
	 * 3. get OID from view vehicle service.
	 * 4. hit update vehicle service.
	 * 5. verify on Pas ui that vehicle updated with the provided information
	 * 6. hit view vehicle service again to verify vehicle information is updated.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9610"})
	public void pas9610_UpdateVehicleService(@Optional("VA") String state) {

		pas9610_UpdateVehicleService();
	}

	/**
	 * @author Megha Gubbala
	 * Create a policy with 2 driver and 2 vehicle
	 * Create pended endorsement using DXP
	 * For 2 driver and 1 vehicle check primary response
	 * Add vehicle Using DXP
	 * Hit driver assignement service to verify unassigned response
	 * Pas go to assign page and save
	 * Hit driver assignement service to verify unassigned response
	 * Verify Occasional Satatus
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-10484"})
	public void pas10484_ViewDriverAssignment(@Optional("VA") String state) {

		pas10484_ViewDriverAssignmentService(getPolicyType());
	}

	/**
	 * @author Megha Gubbala
	 * Create a policy with 1 driver and 1 vehicle
	 * Create pended endorsement using DXP
	 * Add vehicle Using DXP
	 * Hit driver assignement service to verify Response
	 * Pas go to assign page and get information
	 * Hit driver assignement service to verify 1 driver is assigned to both vehicle
	 * Verify primary for first vehicle and ocasional for 2nd vehicle
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11633"})
	public void pas11633_ViewDriverAssignmentAutoAssign(@Optional("VA") String state) {

		pas11633_ViewDriverAssignmentAutoAssignService(getPolicyType());
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Bind Manually created endorsement
	 * @scenario 1. Create active policy
	 * 2. Create an endorsement manually
	 * 3. Rate endorsement manually
	 * 4. Bind endorsement using service
	 * 5. Check number of document records generated in aaaDocGenEntity
	 * 6. Check Authorized By was set correctly
	 * 7. Create and Issue one more endorsement
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-508"})
	public void pas508_BindManualEndorsement(@Optional("VA") String state) {

		pas508_BindManualEndorsement();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Bind Manually created endorsement
	 * @scenario 1. Create active policy
	 * 2. Create an endorsement through service
	 * 3. Rate endorsement through service
	 * 4. Bind endorsement using service
	 * 5. Check number of document records generated in aaaDocGenEntity
	 * 6. Check Authorized By was set correctly
	 * 7. Create and Issue one more endorsement
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-508"})
	public void pas508_BindServiceEndorsement(@Optional("VA") String state) {

		pas508_BindServiceEndorsement();
	}

	/**
	 * @author Megha Gubbala
	 * Create a policy
	 * Get actual and term premium from pas
	 * Run view Premium service
	 * Match both premium
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-10227"})
	public void pas10227_ViewPremiumServicePolicy(@Optional("VA") String state) {

		pas10227_ViewPremiumServiceForPolicy();
	}

	/**
	 * @author Megha Gubbala
	 * Create a policy
	 * create a pended endorsment
	 * add vehicle
	 * rate policy
	 * hit view premium service
	 * Validate premium with pas
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-10227"})
	public void pas10227_ViewPremiumServicePendedEndorsement(@Optional("VA") String state) {

		pas10227_ViewPremiumServiceForPendedEndorsement();
	}

	/**
	 * @author Megha Gubbala
	 * Create a active policy with 2008 vehicle
	 * Get vehicle coverages from Pas
	 * run Dxp ViewManageVehicleLevelCoverages
	 * verify coverages are same like pas coverages
	 * calculate premium save and exit
	 * run ViewManageVehicleLevelCoverages for endorsemnt
	 * validate they are matching with pas.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11741"})
	public void pas11741_ManageVehicleLevelCoverages(@Optional("VA") String state) {

		pas11741_ViewManageVehicleLevelCoverages(getPolicyType());
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Validation on Update/Rate/Bind for vehicle use = Business
	 * @scenario 1. Create active policy
	 * 2. Add a vehicle
	 * 3. Update vehicle, set usage = Business
	 * Error expected
	 * 4. Rate the endorsement
	 * Error expected
	 * 5. Bind the endorsement
	 * Error expected
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "myPolicyUserAddedConfigCheck")
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-7147"})
	public void pas7147_VehicleUpdateBusiness(@Optional("VA") String state) {

		pas7147_VehicleUpdateBusinessBody();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Validation on Update/Rate/Bind for vehicle use = Registered Owner
	 * @scenario 1. Create active policy
	 * 2. Add a vehicle
	 * 3. Update vehicle, set usage = Business
	 * Error expected
	 * 4. Rate the endorsement
	 * Error expected
	 * 5. Bind the endorsement
	 * Error expected
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "myPolicyUserAddedConfigCheck")
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-7147"})
	public void pas7147_VehicleUpdateRegisteredOwner(@Optional("VA") String state) {

		pas7147_VehicleUpdateRegisteredOwnerBody();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Validation of E2E flow in DXP
	 * @scenario 1. see script body
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12866"})
	public void pas12866_e2e(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas12866_e2eBctBody(state, true, softly)
		);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Validation of E2E flow in DXP
	 * @scenario 1. see script body
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12866"})
	public void pas12866_e2eBct(@Optional("KY") String state) {
		assertSoftly(softly ->
				pas12866_e2eBctBody(state, false, softly)
		);
	}

	@Override
	protected String getGeneralTab() {
		return NavigationEnum.AutoSSTab.GENERAL.get();
	}

	@Override
	protected String getPremiumAndCoverageTab() {
		return NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get();
	}

	@Override
	protected String getDocumentsAndBindTab() {
		return NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get();
	}

	@Override
	protected String getVehicleTab() {
		return NavigationEnum.AutoSSTab.VEHICLE.get();
	}

	@Override
	protected Tab getGeneralTabElement() {
		return new GeneralTab();
	}

	@Override
	protected Tab getPremiumAndCoverageTabElement() {
		return new PremiumAndCoveragesTab();
	}

	@Override
	protected Tab getDocumentsAndBindTabElement() {
		return new DocumentsAndBindTab();
	}

	@Override
	protected Tab getVehicleTabElement() {
		return new VehicleTab();
	}

	@Override
	protected AssetDescriptor<JavaScriptButton> getCalculatePremium() {
		return AutoCaMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM;
	}

}
