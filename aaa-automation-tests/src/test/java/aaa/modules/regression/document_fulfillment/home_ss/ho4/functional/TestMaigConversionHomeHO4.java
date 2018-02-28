/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.document_fulfillment.home_ss.ho4.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestMaigConversionHomeTemplate;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMaigConversionHomeHO4  extends TestMaigConversionHomeTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO4;
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
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO4, testCaseId = {"PAS-2305"})
    public void pas2305_preRenewalLetterHSPRNXX(@Optional("VA") String state) throws NoSuchFieldException {
        super.pas2305_preRenewalLetterHSPRNXX(state);
    }

    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO4, testCaseId = {"PAS-2674"})
    public void pas2674_SpecificConversionPacketGenerationForNJ(@Optional("NJ") String state) throws NoSuchFieldException {

        //HO4 should be adjusted with different data then other products to get Senior Insured form
        TestData testData = getConversionPolicyDefaultTD();
        verifyConversionFormsSequence(testData);
    }

    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO4, testCaseId = {"PAS-2674"})
    public void pas2674_SpecificConversionPacketGenerationForOtherStates(@Optional("DE") String state) throws NoSuchFieldException {

        TestData testData = getConversionPolicyDefaultTD();
        verifyConversionFormsSequence(testData);
    }

    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO4, testCaseId = {"PAS-9816"})
    public void pas9816_SpecificBillingPacketGenerationForOtherStates(@Optional("DE") String state) throws NoSuchFieldException {
        // CW, DE, VA
        verifyBillingFormsSequence(getConversionPolicyDefaultTD().adjust(TestData.makeKeyPath("PremiumsAndCoveragesQuoteTab","Payment plan"),"Monthly (Renewal)").resolveLinks());
    }

}
