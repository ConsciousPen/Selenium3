/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.choice.functional;

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
import aaa.modules.regression.service.helper.TestMiniServicesNonPremiumBearingAbstract;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestMiniServicesNonPremiumBearing extends TestMiniServicesNonPremiumBearingAbstract {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
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
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE, testCaseId = {"PAS-1441", "PAS-5986", "PAS-343"})
	public void pas1441_emailChangeOutOfPas(@Optional("CA") String state) {

		CustomAssert.enableSoftMode();
		pas1441_emailChangeOutOfPasTestBody(getPolicyType());
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
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
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9716"})
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
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9716"})
	public void pas9716_policySummaryForActiveRenewal(@Optional("CA") String state) {

		pas9716_policySummaryForActiveRenewal(getPolicyType(), state);
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
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9716"})
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
