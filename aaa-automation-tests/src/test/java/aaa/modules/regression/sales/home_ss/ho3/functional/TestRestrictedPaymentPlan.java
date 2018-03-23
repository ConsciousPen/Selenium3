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
	 * @name
	 * @scenario 1.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-2305"})
	public void pas10870_restrictionPaymentPlansMembershipYes(@Optional("DE") String state) throws NoSuchFieldException {
		super.pas10870_restrictionPaymentPlansMembershipYes(state);
	}

	/**
	 * @name
	 * @scenario 1.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-2305"})
	public void pas10870_restrictionPaymentPlansMembershipPending(@Optional("NJ") String state) throws NoSuchFieldException {
		super.pas10870_restrictionPaymentPlansMembershipPending(state);
	}

	/**
	 * @name
	 * @scenario 1.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-2305"})
	public void pas10870_restrictionPaymentPlansMembershipNo(@Optional("CT") String state) throws NoSuchFieldException {
		super.pas10870_restrictionPaymentPlansMembershipNo(state);
	}

	/**
	 * @name
	 * @scenario 1.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-2305"})
	public void pas10870_restrictionPaymentPlansMembershipOverride(@Optional("DE") String state) throws NoSuchFieldException {
		super.pas10870_restrictionPaymentPlansMembershipOverride(state);
	}

	/**
	 * @name
	 * @scenario 1.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-2305"})
	public void pas10870_restrictionPaymentPlansMembershipPendingCalculation(@Optional("DE") String state) throws NoSuchFieldException {
		super.pas10870_restrictionPaymentPlansMembershipPendingCalculation(state);
	}

	/**
	 * @name
	 * @scenario 1.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-2305"})
	public void pas10870_restrictionPaymentPlansMembershipYesAutoNo(@Optional("DE") String state) throws NoSuchFieldException {
		super.pas10870_restrictionPaymentPlansMembershipYesAutoNo(state);
	}

	/**
	 * @name
	 * @scenario 1.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-2305"})
	public void pas10870_restrictionPaymentPlansMembershipPendingAutoNo(@Optional("DE") String state) throws NoSuchFieldException {
		super.pas10870_restrictionPaymentPlansMembershipPendingAutoNo(state);
	}

	/**
	 * @name
	 * @scenario 1.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-2305"})
	public void pas10870_restrictionPaymentPlansMembershipNoAutoNo(@Optional("DE") String state) throws NoSuchFieldException {
		super.pas10870_restrictionPaymentPlansMembershipNoAutoNo(state);
	}

	/**
	 * @name
	 * @scenario 1.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-2305"})
	public void pas10870_restrictionPaymentPlansMembershipOverrideAutoNo(@Optional("DE") String state) throws NoSuchFieldException {
		super.pas10870_restrictionPaymentPlansMembershipOverrideAutoNo(state);
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
}
