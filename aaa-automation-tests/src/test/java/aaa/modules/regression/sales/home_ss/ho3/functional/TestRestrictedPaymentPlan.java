/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ss.ho3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestRestrictedPaymentPlanAbstract;
import toolkit.utils.TestInfo;

public class TestRestrictedPaymentPlan extends TestRestrictedPaymentPlanAbstract {

	/**
	 * @name Test Restricted Payment Plan For Home with Membership = Yes and no other restrictions
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Yes', don't add any other restrictions to payment plans.
	 * 3. Go to P&C page and verify  that:
	 * - restriction isn't applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-10894"})
	public void pas10894_restrictionPaymentPlansMembershipYes(@Optional("AZ") String state) {
		super.pas10894_restrictionPaymentPlansMembershipYes(state);
	}

	/**
	 * @name Test Restricted Payment Plan For Home with Membership = Pending and no other restrictions
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Pending', don't add any other restrictions to payment plans.
	 * 3. Go to P&C page and verify  that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text are present
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-10894"})
	public void pas10894_restrictionPaymentPlansMembershipPending(@Optional("AZ") String state) {
		super.pas10894_restrictionPaymentPlansMembershipPending(state);
	}

	/**
	 * @name Test Restricted Payment Plan For Home with Membership = No and no other restrictions
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'No', don't add any other restrictions to payment plans.
	 * 3. Go to P&C page and verify  that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-10894"})
	public void pas10894_restrictionPaymentPlansMembershipNo(@Optional("AZ") String state) {
		super.pas10894_restrictionPaymentPlansMembershipNo(state);
	}

	/**
	 * @name Test Restricted Payment Plan For Home with Membership = Membership Override and no other restrictions
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Membership Override', don't add any other restrictions to payment plans.
	 * 3. Go to P&C page and verify  that:
	 * - restriction isn't applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-11367"})
	public void pas11367_restrictionPaymentPlansMembershipOverride(@Optional("AZ") String state) {
		super.pas11367_restrictionPaymentPlansMembershipOverride(state);
	}

	/**
	 * @name Compare payment plans premiums for Membership = Yes and Membership = Pending in restricted/unrectricted tables
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Yes', don't add any other restrictions to payment plans.
	 * 3. Go to P&C page and calculate premium, check premiums in Payment Plans section (all payplans are present).
	 * 4. Go to General page and update Membership to 'Pending'.
	 * 5. Go to P&C page and calculate premium, check premiums in Payment Plans section (restricted/unrestricted tables) .
	 * 6. Check that premiums are the same for payment plans when Membership = Yes and membership = 'Pending'.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-10998"})
	public void pas10998_restrictionPaymentPlansMembershipPendingCalculation(@Optional("AZ") String state) {
		super.pas10998_restrictionPaymentPlansMembershipPendingCalculation(state);
	}

	/**
	 * @name Test Restricted Payment Plan For Home with Membership = Yes and companion auto restriction
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Yes' and don't add companion auto.
	 * 3. Go to P&C page and verify  that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-10894"})
	public void pas10894_restrictionPaymentPlansMembershipYesAutoNo(@Optional("AZ") String state) {
		super.pas10894_restrictionPaymentPlansMembershipYesAutoNo(state);
	}

	/**
	 * @name Test Restricted Payment Plan For Home with Membership = Pending and companion auto restriction
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Pending' and don't add companion auto.
	 * 3. Go to P&C page and verify that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-10894"})
	public void pas10894_restrictionPaymentPlansMembershipPendingAutoNo(@Optional("AZ") String state) {
		super.pas10894_restrictionPaymentPlansMembershipPendingAutoNo(state);
	}

	/**
	 * @name Test Restricted Payment Plan For Home with Membership = No and companion auto restriction
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'No' and don't add companion auto.
	 * 3. Go to P&C page and verify  that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-10894"})
	public void pas10894_restrictionPaymentPlansMembershipNoAutoNo(@Optional("AZ") String state) {
		super.pas10894_restrictionPaymentPlansMembershipNoAutoNo(state);
	}

	/**
	 * @name Test Restricted Payment Plan For Home with Membership = Membership Override and companion auto restriction
	 * @scenario
	 * 1. Initiate quote creation.
	 * 2. Select Membership = 'Membership Override' and don't add companion auto.
	 * 3. Go to P&C page and verify that:
	 * - restriction is applied
	 * - table for unrestricted payment plans and help text aren't present
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-11367"})
	public void pas11367_restrictionPaymentPlansMembershipOverrideAutoNo(@Optional("AZ") String state) {
		super.pas11367_restrictionPaymentPlansMembershipOverrideAutoNo(state);
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
}
