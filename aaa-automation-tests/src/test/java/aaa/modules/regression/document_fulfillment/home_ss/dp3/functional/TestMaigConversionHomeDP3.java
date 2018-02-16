/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.document_fulfillment.home_ss.dp3.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestMaigConversionHomeTemplate;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
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

}
