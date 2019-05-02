/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.select.functional;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.modules.regression.service.helper.TestMiniServicesPremiumBearingAbstract;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestMiniServicesPremiumBearing extends TestMiniServicesPremiumBearingAbstract {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	/**
	 * @author Nauris Ivanans
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
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-28288"})
	public void pas28288_PolicyLockUnlockServices(@Optional("CA") String state) {
		policyLockUnlockServicesBody();
	}

	/**
	 * @author Maris Strazds
	 * @name Endorsement can be performed through service without sending Effective date
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Create an endorsement, issue
	 * 4. Check Green Button endorsement is allowed and there are no errors present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568", "PAS-15400"})
	public void pas6560_endorsementValidateAllowedNoEffectiveDate(@Optional("CA") String state) {

		pas6560_endorsementValidateAllowedNoEffectiveDate();
	}

	/**
	 * @author Maris Strazds
	 * @name Endorsement can be performed through service when there is another completed endorsement
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Create an endorsement, issue
	 * 4. Check Green Button endorsement is allowed and there are no errors present
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568", "PAS-15400"})
	public void pas6560_endorsementValidateAllowed(@Optional("CA") String state) {

		pas6560_endorsementValidateAllowed();
	}

	/**
	 * @author Maris Strazds
	 * @name Endorsement can be performed through service when there is another User created Pended endorsement
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Start an endorsement created by user, but not finish (Pended Endorsement)
	 * 4. Check Green Button endorsement is allowed and there are no errors
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-6560", "PAS-15400"})
	public void pas6560_endorsementValidateAllowedPendedEndorsementUser(@Optional("CA") String state) {

		pas6560_endorsementValidateAllowedPendedEndorsementUser(getPolicyType());
	}

	/**
	 * @author Maris Strazds
	 * @name Endorsement can not be performed through service when there is Future Dated endorsement
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Create endorsement in the Future, issue
	 * 4. Check Endorsement is Not allowed and there is an error about OOSE or Future Dated Endorsement
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568", "PAS-15400"})
	public void pas6562_endorsementValidateNotAllowedFutureDatedEndorsement(@Optional("CA") String state) {

		pas6562_endorsementValidateNotAllowedFutureDatedEndorsement(getPolicyType());
	}

	/**
	 * @author Maris Strazds
	 * @name Endorsement can NOT be performed through service for NANO policy
	 * @scenario 1. Create customer
	 * 2. Create a NANO policy
	 * 3. Check Green Button endorsement is not allowed. There is a PolicyRules error about NANO
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568", "PAS-15400"})
	public void pas6562_endorsementValidateNotAllowedNano(@Optional("CA") String state) {

		pas6562_endorsementValidateNotAllowedNano(getPolicyType(), state);
	}

	/**
	 * @author Maris Strazds
	 * @name Endorsement can NOT be performed through service when there is Pended System Endorsement
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Start an endorsement created by System, but not finish (Pended Endorsement)
	 * 4. Check Green Button endorsement is not allowed. There is a PolicyRules error about System Pended Endorsement
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568", "PAS-15400"})
	public void pas6562_endorsementValidateNotAllowedPendedEndorsementSystem(@Optional("CA") String state) {

		pas6562_endorsementValidateNotAllowedPendedEndorsementSystem(getPolicyType());
	}

	/**
	 * @author Maris Strazds
	 * @name Test Email change through service
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Check Green Button endorsement is not allowed for the date outside of policy term
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568", "PAS-9337", "PAS-15400"})
	public void pas6562_endorsementValidateNotAllowedOutOfBound(@Optional("CA") String state) {

		pas6562_endorsementValidateNotAllowedOutOfBound(getPolicyType());
	}

	/**
	 * @author Maris Strazds
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
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-8784", "PAS-15400"})
	public void pas8784_endorsementValidateNotAllowedCustomer(@Optional("CA") String state) {

		pas8784_endorsementValidateNotAllowedCustomer(getPolicyType());
	}

	/**
	 * @author Maris Strazds
	 * @name Test cannot delete Agent Created Pended Endorsement within delay period
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Create Agent Endorsement
	 * 4. Validate through service, that this endorsement can be deleted on creation date and new endorsement can be started
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-8784", "PAS-15400"})
	public void pas8784_endorsementValidateNoDelayAllowedAgent(@Optional("CA") String state) {

		pas8784_endorsementValidateNoDelayAllowedAgent(getPolicyType());
	}

	/**
	 * @author Maris Strazds
	 * @name Test cannot delete System Created Pended Endorsement within delay period
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Create Agent Endorsement and convert it to System
	 * 4. Validate through service, that this endorsement cannot be deleted
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-8784", "PAS-15400"})
	public void pas8784_endorsementValidateNoDelayNotAllowedSystem(@Optional("CA") String state) {

		pas8784_endorsementValidateNoDelayNotAllowedSystem(getPolicyType());
	}

	/**
	 * @author Maris Strazds
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
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-7332", "PAS-8785", "PAS-15400"})
	public void pas7332_deletePendingSystemEndorsementStartNewEndorsementThroughService(@Optional("CA") String state) {

		pas7332_deletePendingEndorsementStartNewEndorsementThroughService(getPolicyType(), "System");
	}

	@Override
	protected String getGeneralTab() {
		return NavigationEnum.AutoCaTab.GENERAL.get();
	}

	@Override
	protected String getPremiumAndCoverageTab() {
		return NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get();
	}

	@Override
	protected String getDocumentsAndBindTab() {
		return NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get();
	}

	@Override
	protected String getVehicleTab() {
		return NavigationEnum.AutoCaTab.VEHICLE.get();
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

	@Override
	protected String getProductCd() {
		return "AAA_CSA";
	}

	@Override
	protected String getPolicyFormCd() {
		return null;
	}
}
