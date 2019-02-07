/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss.functional;

import static aaa.modules.regression.service.auto_ss.functional.preconditions.MiniServicesSetupPreconditions.MY_POLICY_USER_CONFIG_CHECK;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import static toolkit.verification.CustomAssertions.assertThat;
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
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestMiniServicesPremiumBearing extends TestMiniServicesPremiumBearingAbstract {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	public static void miniServicesEndorsementDeleteDelayConfigCheck() {
		assertSoftly(softly -> {
			miniServicesEndorsementDeleteDelayConfigCheckAssertion(softly, 2, "is null");
			miniServicesEndorsementDeleteDelayConfigCheckAssertion(softly, 0, " = 'AZ'");
			miniServicesEndorsementDeleteDelayConfigCheckAssertion(softly, 5, " = 'AZ'");
		});
	}

	private static void miniServicesEndorsementDeleteDelayConfigCheckAssertion(ETCSCoreSoftAssertions softly, int i, String s) {
		softly.assertThat(DBService.get().getValue(String.format(MiniServicesSetupPreconditions.AAA_CUSTOMER_ENDORSEMENT_DAYS_CONFIG_CHECK, i, s)).get()).isNotEmpty();
	}

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
	public void pas6560_endorsementValidateAllowedPendedEndorsementUser(@Optional("AZ") String state) {

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
		//The test was moved out from dxp_suite, need to refactor after PAS-19725
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-7332", "PAS-8785", "PAS-11622"})
	public void pas7332_deletePendingAgentEndorsementStartNewEndorsementThroughService(@Optional("AZ") String state) {

		pas7332_deletePendingEndorsementStartNewEndorsementThroughService(getPolicyType(), "Agent");
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
	 * @author Jovita Pukenaite
	 * @name Check create endorsement service Transaction Date
	 * @scenario 1. Create Policy
	 * 2. Create endorsement: effective date=today
	 * 3. Validate response (transaction date)
	 * 4. Hit "pending endorsement info" service. Check transactionDate.
	 * 5. Create future date endorsement
	 * 6. Validate response (transaction date)
	 * 7. Hit "pending endorsement info" service. Check transactionDate.
	 * 8. Move time to the future endorsement date +2days.
	 * 9. Hit "pending endorsement info" service. Check transactionDate.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9337"})
	public void pas15846_CheckTransactionDateForEndorsements(@Optional("VA") String state) {

		pas15846_CheckTransactionDateForEndorsementsBody();
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
	 * @author Megha Gubbala
	 * @name Check Policy Details service for Semi annual policy term
	 * @scenario 1. Create  policy with semi annual pay term
	 * 2. hit policy summary service
	 * 3. verify response is returning Term 6 month
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-16678"})
	public void pas16678_policySummaryForPolicyForPolicyTerm(@Optional("VA") String state) {

		pas16678_policySummaryForPolicyForPolicyTermBody(getPolicyType(), state);
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
		pas9456_9455_PolicyLockUnlockServicesBody();
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
	 * @author Jovita Pukenaite
	 * @name View Premium Service - Tax information
	 * @scenario 1. Create policy
	 * 2. Hit view Policy Premium service
	 * 3. Check if tax info exist
	 * 4. Create endorsement outside of PAS
	 * 5. Add new driver and bind endorsement
	 * 6. Check tax info
	 * 7. Hit view endorsement Premium info
	 * 8. Check tax information
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.KY, Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-19742"})
	public void pas19742ViewPremiumServiceTaxInformation(@Optional("KY") String state) {

		pas19742ViewPremiumServiceTaxInformationBody();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name View Premium Service - PLIGA Fee
	 * @scenario 1. Create policy
	 * 2. Hit view Policy Premium service
	 * 3. Check if PLIGA fee is there
	 * 4. Create endorsement outside of PAS
	 * 5. Add vehicle.
	 * 6. Rate and check if PLIGA fee is there
	 * 7. Hit view endorsement Premium info
	 * 8. Check response again.
	 * 9. Bind and check premium info again.
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-19166"})
	public void pas19166ViewPremiumServicePligaFeeInformation(@Optional("NJ") String state) {

		pas19166ViewPremiumServicePligaFeeInformationBody();
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

	/**
	 * @author Megha Gubbala
	 * Create a policy
	 * create a pended endorsment
	 * hit EndorsementChangeLog service validare response NO_CHANGES
	 * rate policy
	 * hit view premium service
	 * Validate premium with pas
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15897"})
	public void pas15897_transactionHistoryAndMessage(@Optional("VA") String state) {

		pas15897_TransactionHistoryAndMessage();
	}

	/**
	 * @author Megha Gubbala
	 * Create a policy
	 * create a pended endorsment
	 * add new vehicle
	 * hit Endorsement ChangeLog service validare response verify all coverages are there for added vehicle
	 * verify is chage type is added
	 * rate policy
	 * hit view premium service
	 * Validate premium with pas
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14540"})
	public void pas14540_transactionInfoAddVehicleCoverages(@Optional("VA") String state) {

		pas14539_transactionInfoAddVehicleCoveragesBody();
	}

	/**
	 * @author Megha Gubbala
	 * Create a policy
	 * update coverages on existing vehicle
	 * hit EndorsementChangeLog service validare response verify all coverages are there as updated
	 * verify is chage type is Modified
	 * rate policy
	 * hit view premium service
	 * Validate premium with pas
	 */
	//coverages update on existing policy Scenario 2
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14540"})
	public void pas14540_transactionInfoAddVehicleCoveragesUpdate(@Optional("VA") String state) {

		pas14539_transactionInfoAddVehicleCoveragesUpdateBody();
	}

	/**
	 * @author Megha Gubbala
	 * from pre-conditions VA shouls allow all 4 actions
	 * Create a policy
	 * hit startEndorsement service
	 * Validate Respose should show UpdateVehicle  UpdateDriver UpdateCoverage
	 *
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13287"})
	public void pas13287_ViewStartEndorsementInfoService(@Optional("VA") String state) {
		pas13287_ViewStartEndorsementInfoServiceBody();

	}

	/**
	 * @author Megha Gubbala
	 * from pre-conditions DC shouls allow UpdateVehicle and UpdateCoverages actions
	 * Create a policy
	 * hit startEndorsement service
	 * Validate Respose should show UpdateVehicle  UpdateCoverage
	 *
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13287"})
	public void pas13287_ViewStartEndorsementInfoServiceForDC(@Optional("DC") String state) {
		pas13287_ViewStartEndorsementInfoServiceDCBody();

	}

	/**
	 * @author Megha Gubbala
	 * from pre-conditions AZ shouls allow UpdateVehicle and UpdateDriver actions
	 * Create a policy
	 * hit startEndorsement service
	 * Validate Respose should show UpdateVehicle  UpdateDriver
	 *
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13287"})
	public void pas13287_ViewStartEndorsementInfoServiceForAZ(@Optional("AZ") String state) {
		pas13287_ViewStartEndorsementInfoServiceAZBody();

	}

	/**
	 * @author Megha Gubbala
	 * from pre-conditions MD shouls allow UpdateDriver and UpdateCoverages actions
	 * Create a policy
	 * hit startEndorsement service
	 * Validate Respose should show  UpdateDriver and UpdateCoverages
	 *
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13287"})
	public void pas13287_ViewStartEndorsementInfoServiceForMD(@Optional("MD") String state) {
		pas13287_ViewStartEndorsementInfoServiceMDBody();

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
