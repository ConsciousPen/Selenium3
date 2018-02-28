/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.document_fulfillment.home_ss.dp3.functional;

import static aaa.modules.regression.sales.auto_ss.functional.preconditions.TestEValueMembershipProcessPreConditions.RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_CHECK;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestMaigConversionHomeTemplate;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestMaigConversionHomeAbstract;
import toolkit.utils.TestInfo;

public class TestMaigConversionHomeDP3 extends TestMaigConversionHomeTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_DP3;
    }

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for Home
     * 4. Check that HSPRNXX document is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
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
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
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
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
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
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
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
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
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
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
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
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
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
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
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
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
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
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
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
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
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
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2570"})
    public void pas2570_renewalCoverLetterPupHSRNMXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas2570_renewalCoverLetterPupHSRNMXX(state);
    }


    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2674"})
    public void pas2674_SpecificConversionPacketGenerationForNJ(@Optional("NJ") String state) throws NoSuchFieldException {

        TestData testData = getConversionPolicyDefaultTD();
        verifyConversionFormsSequence(testData);
    }

    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2674"})
    public void pas2674_SpecificConversionPacketGenerationForOtherStates(@Optional("DE") String state) throws NoSuchFieldException {
        // CW, DE, VA
        TestData testData = adjustWithMortgageeData(getConversionPolicyDefaultTD());
        verifyConversionFormsSequence(testData);
    }

    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-9816"})
    public void pas9816_SpecificBillingPacketGenerationForOtherStates(@Optional("DE") String state) throws NoSuchFieldException {
        // CW, DE, VA
        verifyBillingFormsSequence(getConversionPolicyDefaultTD().adjust(TestData.makeKeyPath("PremiumsAndCoveragesQuoteTab","Payment plan"),"Monthly (Renewal)").resolveLinks());
    }

    @Test(description = "Check membership endpoint", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
    public static void retrieveMembershipSummaryEndpointCheck() {
        assertThat(DBService.get().getValue(RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_CHECK).get()).contains(PropertyProvider.getProperty(CustomTestProperties.APP_HOST));
    }

}
