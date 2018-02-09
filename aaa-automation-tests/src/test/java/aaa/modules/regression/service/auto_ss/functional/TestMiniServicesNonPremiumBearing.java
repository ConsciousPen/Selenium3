/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss.functional;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.assertj.core.api.SoftAssertions;
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
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.TestMiniServicesNonPremiumBearingAbstract;
import aaa.modules.regression.service.helper.wiremock.dto.WireMockMappingRequest;
import aaa.modules.regression.service.helper.wiremock.factory.PaperlessPreferencesWMRequestFactory;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestMiniServicesNonPremiumBearing extends TestMiniServicesNonPremiumBearingAbstract {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Test(description = "Precondition")
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
		pas1441_emailChangeOutOfPasTestBody(getPolicyType());
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

		pas6560_endorsementValidateAllowedNoEffectiveDate(getPolicyType());
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

		pas6560_endorsementValidateAllowed(getPolicyType());
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
	public void pas6562_endorsementValidateNotAllowedNano(@Optional("VA") String state) {

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
	public void pas6562_endorsementValidateNotAllowedUBI(@Optional("VA") String state) {

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
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568"})
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
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-7332"})
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
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-7332"})
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

		pas8275_vinValidateCheck(getPolicyType());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-8273"})
	public void pas8273_OnlyActiveVehiclesAreAllowed(@Optional("VA") String state) {

		pas8273_CheckIfOnlyActiveVehiclesAreAllowed(getPolicyType());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-8275"})
	public void pas111_paperlessMockTest(@Optional("VA") String state) {
		/* http://nvdxpas1agl007:9999/__admin/swagger-ui/*/

		String policyNumber = "VASS926232058";
		String url = "http://nvdxpas1agl007:9999/__admin/mappings";
		String scenarioJsonFile = "paperlessOptInPendingResponse.json";
		WireMockMappingRequest request = PaperlessPreferencesWMRequestFactory.create(policyNumber, scenarioJsonFile);
		String id = request.id;
		log.info("id = {}", id);
		HelperCommon.runJsonRequestPostDxp(url, request, String.class);
		//rest

		HelperCommon.runJsonRequestDeleteDxp(url + "/" + id, String.class);
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
	protected AssetDescriptor<Button> getCalculatePremium() {
		return AutoCaMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM;
	}

}
