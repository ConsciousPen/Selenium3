/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.document_fulfillment.home_ss.dp3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestMaigConversionHomeAbstract;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestMaigConversionHomeDP3 extends TestMaigConversionHomeAbstract {

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for Home
     * 4. Check that HSPRNXX document is getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2305"})
    public void pas2305_preRenewalLetterHSPRNXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas2305_preRenewalLetterHSPRNXX(state);
    }

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for Home
     * 4. Initiate PUP conversion policy
     * 5. Check that HSPRNXX document is getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2305"})
    public void pas2305_preRenewalLetterPupConvHSPRNXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas2305_preRenewalLetterPupConvHSPRNXX(state);
    }

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with PUP added to OtherActiveAAAPolicies for Home
     * 4. Check that HSPRNXX document is getting generated with PUP section
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-9170"})
    public void pas9170_preRenewalLetterPupHSPRNXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas9170_preRenewalLetterPupHSPRNXX(state);
    }

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
     * 4. Check that HSPRNMXX document is getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-7342"})
    public void pas7342_preRenewalLetterHSPRNMXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas7342_preRenewalLetterHSPRNMXX(state);
    }

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan
     * 4. Initiate PUP conversion policy
     * 5. Check that HSPRNMXX document is getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-7342"})
    public void pas7342_preRenewalLetterPupConvHSPRNMXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas7342_preRenewalLetterPupConvHSPRNMXX(state);
    }

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan and PUP added to OtherActiveAAAPolicies  for Home
     * 4. Check that HSPRNMXX document is getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-9170"})
    public void pas9170_preRenewalLetterPupHSPRNMXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas9170_preRenewalLetterPupHSPRNMXX(state);
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for Home
     * 4. Check that HSRNHODPXX document is getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2309"})
    public void pas2309_renewalCoverLetterHSRNHODPXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas2309_renewalCoverLetterHSRNHODPXX(state);
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for home
     * 4. Initiate PUP conversion policy
     * 5. Check that HSRNHODPXX document is getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2309"})
    public void pas2309_renewalCoverLetterPupConvHSRNHODPXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas2309_renewalCoverLetterPupConvHSRNHODPXX(state);
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with PUP added to OtherActiveAAAPolicies for Home
     * 4. Check that HSRNHODPXX document is getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2309"})
    public void pas2309_renewalCoverLetterPupHSRNHODPXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas2309_renewalCoverLetterPupHSRNHODPXX(state);
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
     * 4. Check that HSRNMXX document is getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2570"})
    public void pas2570_renewalCoverLetterHSRNMXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas2570_renewalCoverLetterHSRNMXX(state);
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
     * 4. Initiate PUP conversion policy.
     * 5. Check that HSRNMXX document is getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2570"})
    public void pas2570_renewalCoverLetterPupConvHSRNMXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas2570_renewalCoverLetterPupConvHSRNMXX(state);
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan and PUP added to OtherActiveAAAPolicies for Home
     * 4. Check that HSRNMXX document is getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2570"})
    public void pas2570_renewalCoverLetterPupHSRNMXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas2570_renewalCoverLetterPupHSRNMXX(state);
    }

    /**
     * @name Test Conversion Document generation (Insurance Renewal Bill)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for Home
     * 4. Check that AHRBXX document is getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-8789"})
    public void pas8789_insuranceRenewalBillHomeAHRBXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas8789_insuranceRenewalBillHomeAHRBXX(state);
    }

    /**
     * @name Test Conversion Document generation (Insurance Renewal Bill)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
     * 4. Check that AHRBXX document is getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-10241"})
    public void pas10241_insuranceRenewalBillHomeMortAHRBXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas10241_insuranceRenewalBillHomeMortAHRBXX(state);
    }

    /**
     * @name Test Conversion Document generation (Mortgagee Bill Final Expiration Notice)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
     * 4. Check that HSRR2XX document is getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-8763"})
    public void pas8763_mortgageeBillFinalExpirationHSRR2XX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas8763_mortgageeBillFinalExpirationHSRR2XX(state);
    }

    /**
     * @name Test Conversion Document generation (Mortgagee Bill First Renewal Reminder)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
     * 4. Check that HSRRXX document is getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-8762"})
    public void pas8762_mortgageeBillFirstRenewalReminderHSRRXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas8762_mortgageeBillFirstRenewalReminderHSRRXX(state);
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_DP3;
    }

    /**
     * @name Test Conversion Document generation
     * @scenario
     * 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for Home - MD
     * 4. Check that HS65MD documents are getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(states = States.MD)
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2662"})
    public void pas12047_noticeOfNonRenewalLetterHS65MD(@Optional("MD") String state) throws NoSuchFieldException {
        super.pas12047_noticeOfNonRenewalLetterHS65MD(state);
    }

    /**
     * @name Test Conversion Document generation
     * @scenario
     * 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for Home
     * 4. Check that HSFLD documents are getting generated
     * 5. Buy Conversion Policy
     * 6. Move time to 2nd Renewals Offer Generation date (usually R-35)
     * 7. Check that HSFLD document is NOT generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(states = {States.MD, States.IN, States.CT, States.WV})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-12589", "PAS-18431"})
    public void pas11772_importantNoticeRegardingFloodInsuranceHSFLD(@Optional("CT") String state) throws NoSuchFieldException {
        super.pas11772_importantNoticeRegardingFloodInsuranceHSFLD(state);
    }

}
