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
import aaa.common.enums.Constants;
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
import aaa.modules.regression.service.helper.TestMiniServicesPremiumBearingAbstract;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import aaa.utils.StateList;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestMiniServicesPremiumBearing extends TestMiniServicesPremiumBearingAbstract {

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
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-7332", "PAS-8785", "PAS-11622"})
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
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-8273", "PAS-7145", "PAS-11622"})
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
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11932", "PAS-12768"})
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
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-7082", "PAS-7145", "PAS-11621"})
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
	public void pas9489_GreenButtonServiceStateAndProductConfigurationEffective(@Optional("NY") String state) {

		pas9337_CheckStartEndorsementInfoServerResponseErrorForEffectiveDate();
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
	 * Added Pas 12244
	 * Add 2 PPA vehicle
	 * hit view vehicle service on pended endorsement
	 * verify order of vehicle
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-10449", "PAS-12244", "PAS-14593"})
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
	 * @author Jovita Pukenaite
	 * @name Update driver assignment, rule D=V
	 * @scenario 1. Create a policy with V1 and D1, D2.
	 * 2. Hit view driver assignment service. Get all info.
	 * 3. Add V2.
	 * 4. Hit DA update service.
	 * a) V2-->D1 and check response (V2-->D1, V1-->D2)
	 * b) Update , V1-->D1 (V1-->D1, V2-->Unn)
	 * c) Update V2-->D2 (V1-->D1, V2-->D2)
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13994"})
	public void pas13994_UpdateDriverAssignmentServiceRule1(@Optional("VA") String state) {

		pas13994_UpdateDriverAssignmentServiceRule1Body(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Update driver assignment, rule V>D
	 * @scenario 1. Create a policy with 3V and 2D
	 * 2. Hit view driver assignment service. Get all info.
	 * 3. Add V4.
	 * 4. Hit view vehicle service, get all info.
	 * 4. Hit DA update service:
	 * a) V4-->D1 (D1-->V1,V3,V4, D2-->V2)
	 * b) V4-->D2 (D1-->V1,V3 D2-->V2,V4)
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13994"})
	public void pas13994_UpdateDriverAssignmentServiceRule2(@Optional("VA") String state) {

		pas13994_UpdateDriverAssignmentServiceRule2Body(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Update driver assignment, rule V<D
	 * @scenario 1. Create a policy with 2V and 4D
	 * 2. Hit view driver assignment service. Get all info.
	 * 3. Add one more vehicle V3.
	 * 4. Hit view vehicle service, get all info.
	 * 4. Hit DA update service:
	 * a) V3-->D1 (V1-->D2, V2-->D3,D4, V3-->D1)
	 * b) V2-->D2 (V1-->D2, V2-->D3,D4,D2 V3-->D1)
	 * c) V2-->D1 (V1-->Unn, V2-->D3,D4,D2,D1 V3-->Unn)
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13994"})
	public void pas13994_UpdateDriverAssignmentServiceRule3(@Optional("VA") String state) {

		pas13994_UpdateDriverAssignmentServiceRule3Body(getPolicyType());
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
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-10227", "PAS-11810"})
	public void pas10227_ViewPremiumServicePendedEndorsement(@Optional("VA") String state) {

		pas10227_ViewPremiumServiceForPendedEndorsement();
	}

	/**
	 * @author Megha Gubbala
	 * Create a active policy with 2018 vehicle
	 * Get vehicle coverages from Pas
	 * run Dxp ViewManageVehicleLevelCoverages
	 * verify coverages are same like pas coverages
	 * calculate premium save and exit
	 * run ViewManageVehicleLevelCoverages for endorsemnt
	 * validate coverages are matching with pas and all delimators .
	 * Go to pas and change all the coverages for vehicle
	 * calculate premium save.
	 * open pended endorsement go to P and C page.
	 * get all vehicle coverages save them
	 * hit view manage vehicle level coverages dxp
	 * validate response aginst pas vehicle coverages.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11741", "PAS-11852", "PAS-12601"})
	public void pas11741_ManageVehicleLevelCoverages(@Optional("VA") String state) {

		pas11741_ViewManageVehicleLevelCoverages(getPolicyType());
	}

	//Scenario 2
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11741", "PAS-11852", "PAS-12601"})
	public void pas11741_ManageVehicleLevelCoveragesOtherThanVA(@Optional("AZ") String state) {

		pas11741_ViewManageVehicleLevelCoveragesForAZ(getPolicyType());
	}

	/**
	 * @author Megha Gubbala
	 * Create a active policy with 2018 vehicle
	 * Create an endorsement.
	 * add new vehicle
	 * Update newly added vehicle to ownership LSD
	 * Get all the vehicle level coverages from pas
	 * request update vehicle level coverage with Oid policy number and coverage name and value
	 * Scanario 1.
	 * Change all the coverages using service
	 * remove one by one coverage and veryfi dependent coverages
	 * change all the coverages back
	 * verify if all other coverages changed
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-10352"})
	public void pas10352_ManageVehicleCoverageUpdateCoverage(@Optional("VA") String state) {

		pas10352_ManageVehicleCoverageUpdateCoverage(getPolicyType());

	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-10352"})
	public void pas10352_ManageVehicleCoverageUpdateCoverageOtherThanVa(@Optional("AZ") String state) {

		pas10352_ManageVehicleCoverageUpdateCoverageOtherState(getPolicyType());
	}

	/**
	 * @author Megha Gubbala
	 * Create a active policy in the pas
	 * Create an endorsement.
	 * Scenario 1
	 * run update coverage service
	 * set BI Coverage 50000/100000 and verify response and check BI limit updated PD available limit should be up to upper limit of BI.
	 * Scenario 2
	 * run update coverage service to set PD 3000000
	 * Verify since PD can not exceed BI so PD limit should set to 100000
	 * Update set BI Coverage 500000/500000 and verify response PD available limit should be up to upper limit of BI
	 * PD limit should not change.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14648"})
	public void pas14721_UpdateCoveragesBI_PD(@Optional("VA") String state) {

		pas14721_UpdateCoveragesServiceBIPD(getPolicyType());
	}

	/**
	 * @author Megha Gubbala
	 * Create a active policy with 2018 and leased  vehicle
	 * Create an endorsement.
	 * run viewCoverageInfo service
	 * verify loan and leas coverage
	 * verify customerDisplayed canChangeCoverage is true
	 * update vehicle using DXP change ownership to own
	 * verify customerDisplayed canChangeCoverage is false
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13353"})
	public void pas13353_LoanLeaseCoverage(@Optional("VA") String state) {

		pas13353_LoanLeaseCoverage(getPolicyType());
	}

	/**
	 * @author Megha Gubbala
	 * Create a active policy with 2018 vehicle
	 * add pending vehicle on policy
	 * update pending vehicle usage as pleasure and ownership as leased
	 * Get vehicle coverages from Pas for both the vehicles using policy number and oid
	 * run Dxp ViewManageVehicleLevelCoveragesone vehicle
	 * verify coverages are same like pas coverages
	 * calculate premium save and exit
	 * run ViewManageVehicleLevelCoverages for endorsemnt
	 * validate coverages are matching with pas and all delimators .
	 * Go to pas and change all the coverages for vehicle
	 * calculate premium save.
	 * open pended endorsement go to P and C page.
	 * get all vehicle coverages save them
	 * hit view manage vehicle level coverages dxp
	 * validate response aginst pas vehicle coverages.
	 * bind the endorsement
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12769"})
	public void pas12769_ViewVehicleLevelCoveragesOneVehicle(@Optional("VA") String state) {

		pas11741_ViewVehicleLevelCoverages(getPolicyType());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14648"})
	public void pas14645_ViewPolicyLevelCoverages(@Optional("VA") String state) {

		pas14645_ViewCoveragesBiPd(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Add new vehicle when you had Vehicle, without Transportation Expense before.
	 * @scenario 1. Create policy with one vehicle,
	 * without Transportation Expense. (
	 * 2. Start endorsement outside of PAS.
	 * 3. Add vehicle.
	 * 4. Hit View Coverage service.
	 * 5. Check coverages for new vehicle:
	 * Transportation Expense, Comprehensive Coverage
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14536"})
	public void pas14536_TransportationExpensePart1(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14536_TransportationExpensePart1Body(getPolicyType(), softly)
		);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Transportation Expense remains the limit I chose
	 * @scenario 1. Create policy with one vehicle,
	 * with Transportation Expense 1.200$
	 * 2. Start endorsement outside of PAS.
	 * 3. Add vehicle.
	 * 4. Remove "COMPDED" coverage from my newly added vehicle.
	 * 5. Hit View Coverage service.
	 * 6. Check if Transportation Expense remains the limit I chose.
	 * 7. Update: add "COMPDED" coverage again.
	 * 8. Hit View Coverage service.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14536"})
	public void pas14536_TransportationExpensePart2(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14536_TransportationExpensePart2Body(getPolicyType(), softly)
		);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Transportation Expense is defaulted
	 * @scenario 1. Create policy with one vehicle,
	 * with Transportation Expense 600$
	 * 2. Start endorsement outside of PAS.
	 * 3. Add vehicle.
	 * 4. Remove "COMPDED" coverage from my newly added vehicle.
	 * 5. Hit View Coverage service.
	 * 6. Update: add "COMPDED" coverage again.
	 * 7. Hit View Coverage service.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14536"})
	public void pas14536_TransportationExpensePart3(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14536_TransportationExpensePart3Body(getPolicyType(), softly)
		);
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
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
	public void pas12866_e2e(@Optional("AZ") String state) {
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
	public void pas12866_e2eBct(@Optional("ID") String state) {
		assertSoftly(softly ->
				pas12866_e2eBctBody(state, false, softly)
		);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11684"})
	public void pas11684_DriverAssignmentExistsForState(@Optional("UT") String state) {
		assertSoftly(softly ->
				pas11684_DriverAssignmentExistsForStateBody(state, softly)
		);
	}

	/**
	 * @author Megha Gubbala
	 * @name Validate Meta data for Vehicle
	 * Create a va policy
	 * Create an endorsement using DXP
	 * Hit view vehicle service to get OID
	 * Hit Meta data service
	 * Validate the response
	 * Add garage address different than Residential
	 * Hit Meta Data service again.
	 * Validate the garage address response.
	 * @scenario
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12407"})
	public void pas12407_BigMetaDataService(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas12407_bigDataService(softly)
		);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Validation on Update/Rate/Bind for vehicle use = Registered Owner
	 * @scenario 1) Create a policy
	 * 2) Start an endorsement
	 * 3) Add a vehicle with Ownership info = Leased or Financed
	 * 3.1) check field values in ui, check address is validated
	 * 5) rate
	 * 6) bind
	 * 7) do an endorsement in PAS, rate bind
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11618"})
	public void pas11618_UpdateVehicleLeasedInfo(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas11618_UpdateVehicleLeasedFinancedInfoBody(softly, "LSD")
		);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Validation on Update/Rate/Bind for vehicle use = Registered Owner
	 * @scenario 1) Create a policy
	 * 2) Start an endorsement
	 * 3) Add a vehicle
	 * 4) update vehicle adding Ownership info = Leased or Financed
	 * 4.1) check field values in ui, check address is validated
	 * 5) rate
	 * 6) bind
	 * 7) do an endorsement in PAS, rate bind
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11618"})
	public void pas11618_UpdateVehicleFinancedInfo(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas11618_UpdateVehicleLeasedFinancedInfoBody(softly, "FNC")
		);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Loan Lease coverage check for veh older than 3 years
	 * @scenario 1) Create a policy
	 * 2) Start an endorsement
	 * 3) Add a vehicle Older than 3 years with Ownership info = Owned
	 * 5) rate
	 * 6) check Loan Lease coverage is not available
	 * 6) Add Ownership Leased or Financed
	 * 7) rate
	 * 8) check coverage is not available
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14316"})
	public void pas14316_LoanLeasedCovForLeasedOldVehicle(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14316_LoanLeasedCovForLeasedOldVehicleBody(softly, "LSD")
		);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Loan Lease coverage check for veh equal to 3 years old
	 * @scenario 1) Create a policy
	 * 2) Start an endorsement
	 * 3) Add a vehicle Older than 3 years with Ownership info = Owned
	 * 5) rate
	 * 6) check Loan Lease coverage is not available
	 * 7) Add Ownership Leased or Financed
	 * 8) rate
	 * 9) check Loan Lease coverage is available and defaulted to No Coverage
	 * 10) set value of the coverage to Yes
	 * 11) rate
	 * 12) check Loan Lease coverage is available and defaulted to No Coverage
	 * 13) Issue
	 * 14) set Ownership to Owned
	 * 15) rate
	 * 16) check Loan Lease is not available
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14316"})
	public void pas14316_LoanLeasedCovForFinancedNewVehicle(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14316_LoanLeasedCovForFinancedNewVehicleBody(softly, "FNC")
		);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13252"})
	public void pas13252_UpdateVehicleGaragingAddressProblem(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas13252_UpdateVehicleGaragingAddressProblemBody(softly)
		);
	}

	/**
	 * @author Megha Gubbala
	 * 1. create a policy with 2 ppa,1 conversion-van and 1 motor vehicle
	 * 2. create pended endorsement.
	 * 2. run view vehicle service and get OID of PPA vehicle
	 * 3. run delete vehicle service and delete vehicle using Oid
	 * 4. verify response status should be pending removal
	 * 5. rate endorsement
	 * 6. bind endorsement
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-488"})
	public void pas488_VehicleDelete(@Optional("VA") String state) {

		pas488_VehicleDeleteBody(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Check Duplicate VINS and the Add Vehicle Service
	 * @scenario 1. Create policy with Two vehicles.
	 * 2. Create endorsement outside of PAS.
	 * 3. Try Add Vehicle with the same VIN witch already exist in the policy.
	 * 4. Check Error about duplicate VIN.
	 * 5. Add new vehicle with new VIN. Check the status.
	 * 6. Try add the same vehicle one more time.
	 * 7. Check if error is displaying.
	 * Start PAS-11005
	 * 8. Try add to expensive vehicle.
	 * 9. Check if error is displaying.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-502", "PAS-11005"})
	public void pas502_DuplicateVinAddVehicleService(@Optional("VA") String state) {

		pas502_CheckDuplicateVinAddVehicleService(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Transaction Information For Endorsements outside of PAS - AddVehicle
	 * @scenario 1. Create policy.
	 * 2. Start do endorsement outside of PAS.
	 * 3. Hit "Transaction History Service". Check if response is empty.
	 * 4. Add Vehicle.
	 * 5. Hit "Transaction History Service". Check new vehicle info.
	 * 6. Update "Usage".
	 * 7. Rate endorsement
	 * 8. Add one more vehicle.
	 * 9. Hit "Transaction History Service". Check new vehicle info.
	 * 10. Bind endorsement.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9493"})
	public void pas9493_TransactionInformationForEndorsementsAddVehicle(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas9493_TransactionInformationForEndorsementsAddVehicleBody(getPolicyType())
		);
	}

	/**
	 * @author Megha Gubbala
	 * 1. create a policy with 2 ppa,1 conversion-van and 1 motor vehicle
	 * 2. hit view vehicle servise to get order of all active vehicles
	 * 3. create pended endorsement.
	 * 2. add  one PPA vehicle through DXP servise.
	 * 3. run delete vehicle service and delete vehicle using Oid
	 * 4. verify response status should be pending removal
	 * 5. then hit view vehicle servise again to get proper order of vehicle
	 * 6. pending removal vehicle should be the 1st and then pending added vehicle and then rest of order will be same.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12246"})
	public void pas12246_ViewVehiclePendingRemoval(@Optional("AZ") String state) {

		pas12246_ViewVehiclePendingRemovalService(getPolicyType());
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Validation of E2E flow in DXP
	 * @scenario 1. Create a policy
	 * 2. Add maximum number of vehicle
	 * 3. Check when the last vehicle is added, the response contains canAddVehicle = false
	 * 4. Try to add 1 more vehicle
	 * 5. Check there is an error message
	 * 6. Rate, Bind
	 * 7. Start new endorsement
	 * 8. add 1 more vehicle - check error
	 * 9. remove a vehicle
	 * 10. add a vehicle
	 * 11. Check when the last vehicle is added, the response contains canAddVehicle = false
	 * 12. rate, bind
	 * 13. Create 1 more endorsement in UI
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9546"})
	public void pas9546_maxVehicles(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas9546_maxVehiclesBody(softly)
		);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name no error about garaging address being different when binding endorsement
	 * @scenario 1. see script body
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA, Constants.States.DE, Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14501"})
	public void pas14501_garagingDifferent(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14501_garagingDifferentBody(state, softly)
		);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.VA, Constants.States.DE, Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14952"})
	public void pas14952_EndorsementStatusResetForVehRatingFactors(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14952_EndorsementStatusResetForVehRatingFactorsBody(state, softly)
		);
	}

	@Parameters({"state"})
	//@StateList(states = {Constants.States.VA, Constants.States.DE, Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14952", "PAS-15152"})
	public void pas14952_StatusResetsForNewlyAddedVehicle(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14952_StatusResetsForNewlyAddedVehicleBody(softly)
		);
	}

	/**
	 * @author gzkvano
	 * @name Check MEDPM delimiter
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14648"})
	public void pas14648_MedpmDelimiter(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14648_MedpmDelimiter(getPolicyType())
		);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Manual Endorsement Deletion
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12767"})
	public void pas12767_ManualEndorsementCancel(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas12767_ManualEndorsementCancelBody()
		);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Service Endorsement Deletion
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12767"})
	public void pas12767_ServiceEndorsementCancel(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas12767_ServiceEndorsementCancelBody()
		);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15483"})
	public void pas15483_deleteOriginalVehicle(@Optional("VA") String state) {
		pas15483_deleteOriginalVehicleBody();
	}

	/**
	 * @author gzkvano
	 * @name Check UIM delimiter
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA, Constants.States.DE, Constants.States.IN, Constants.States.KS,
			Constants.States.MD, Constants.States.NV, Constants.States.NJ, Constants.States.OH, Constants.States.OR, Constants.States.CT})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14646"})
	public void pas14646_UimDelimiter(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14646_UimDelimiter(state, softly)
		);
	}

	/**
	 * @author mstrazds
	 * @name Check UM/UIM delimiter
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ, Constants.States.DC, Constants.States.ID, Constants.States.KY, Constants.States.PA, Constants.States.SD})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15228"})
	public void pas15228_UmUimDelimiter(@Optional("DC") String state) {
		pas15228_UmUimDelimiterBody();
	}

	/**
	 * @author mstrazds
	 * @name Check UMPD delimiter
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.DE, Constants.States.MD, Constants.States.VA, Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15824"})
	public void pas15824_UmpdDelimiter(@Optional("NJ") String state) {
		pas15824_UmpdDelimiterBody();
	}

	/**
	 * @author mstrazds
	 * @name Check UMPD doesn't exit
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ, Constants.States.CT, Constants.States.KS, Constants.States.KY, Constants.States.MT, Constants.States.NY,
			Constants.States.OK, Constants.States.PA, Constants.States.SD, Constants.States.WY, Constants.States.ID})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15325"})
	public void pas15325_UmpdNotExist(@Optional("AZ") String state) {
		pas15325_UmpdNotExistBody();
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
