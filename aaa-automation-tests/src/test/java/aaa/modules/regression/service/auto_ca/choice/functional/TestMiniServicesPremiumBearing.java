/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.choice.functional;

import aaa.common.enums.Constants;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
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
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestMiniServicesPremiumBearing extends TestMiniServicesPremiumBearingAbstract {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check Policy Details service for Pending and Active policies
	 * @scenario
	 * 1. Create pending policy
	 * 2. Check policy details
	 * 3. Change date, run policyStatusUpdate
	 * 4. Check policy details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE, testCaseId = {"PAS-9716"})
	public void pas9716_policySummaryForPolicy(@Optional("CA") String state) {

		pas9716_policySummaryForPolicy(getPolicyType(), state);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check Policy Details service for Active renewal
	 * @scenario
	 * 1. Create active policy
	 * 2. Run Renewal Part1
	 * 3. Check policy and renewal details
	 * 4. Run Renewal Part2
	 * 5. Check policy and renewal details
	 * 6. Make a payment for the renewal amount for the next term
	 * 7. change date to R, run policy status update job
	 * 8. Check policy and renewal details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE, testCaseId = {"PAS-9716"})
	public void pas9716_policySummaryForActiveRenewal(@Optional("CA") String state) {

		pas9716_policySummaryForActiveRenewalBody(state);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Endorsement can be performed through service for all states
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Hit start endorsement info service.
	 * 4. Check the response.
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-25042"})
	public void pas25042_endorsementValidateAllowedForAllStates(@Optional("CA") String state) {

		pas25042_endorsementValidateAllowedForAllStatesBody();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check Policy Details service for Lapsed renewal
	 * @scenario
	 * 1. Create active policy
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
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE, testCaseId = {"PAS-9716"})
	public void pas9716_policySummaryForLapsedRenewal(@Optional("CA") String state) {

		pas9716_policySummaryForLapsedRenewal(getPolicyType(), state);
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

}
