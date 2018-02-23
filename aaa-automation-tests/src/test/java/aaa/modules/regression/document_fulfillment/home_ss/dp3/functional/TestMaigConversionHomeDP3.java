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
import toolkit.utils.TestInfo;

public class TestMaigConversionHomeDP3 extends TestMaigConversionHomeTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_DP3;
    }

    /**
     * @name Test MAIG Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate MAIG Renewal Entry
     * 3. Fill Conversion Policy data
     * 3. Check that HSPRNXX document section is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2305"})
    public void pas2305_preRenewalLetterHSPRNXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas2305_preRenewalLetterHSPRNXX(state);
    }

    /**
     * @name Test MAIG Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate MAIG Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan
     * 3. Check that HSPRNMXX document section is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2305"})
    public void pas2305_preRenewalLetterHSPRNMXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas2305_preRenewalLetterHSPRNXX(state);
    }

    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2674"})
    public void pas2674_SpecificRenewalPacketGenerationForNJ(@Optional("NJ") String state) {

        TestData testData = adjustWithSeniorInsuredData(getConversionPolicyDefaultTD());
        verifyFormsSequence(testData);
    }

    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2674"})
    public void pas2674_SpecificRenewalPacketGenerationForOtherStates(@Optional("DE") String state) {
        // CW, DE, VA
        TestData testData = adjustWithMortgageeData(getConversionPolicyDefaultTD());
        verifyFormsSequence(testData);
    }

    @Test(description = "Check membership endpoint", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
    public static void retrieveMembershipSummaryEndpointCheck() {
        assertThat(DBService.get().getValue(RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_CHECK).get()).contains(PropertyProvider.getProperty(CustomTestProperties.APP_HOST));
    }

}
