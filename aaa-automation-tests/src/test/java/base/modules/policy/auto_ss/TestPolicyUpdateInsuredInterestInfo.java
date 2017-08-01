/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.auto_ss;

import org.testng.annotations.Test;

import aaa.main.metadata.policy.AutoSSMetaData.NonPremiumBearingEndorsementActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Auto Policy Update Insured / Interest Info
 * @scenario
 * 1. Create Customer
 * 2. Create Auto (Preconfigured) Policy
 * 3. Update Insured/Interest Info
 * 4. Verify Updated information appears in InsuredInformation at consolidated view
 * @details
 */
public class TestPolicyUpdateInsuredInterestInfo extends AutoSSBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyUpdateInsuredInterestInfo() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        log.info("TEST: Update Insured / Interest Info Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.nonPremiumBearingEndorsement().perform(getPolicyTD("NonPremiumBearingEndorsement", "TestData"));

        TestData testData = getPolicyTD("NonPremiumBearingEndorsement", "TestData").getTestData(
                NonPremiumBearingEndorsementActionTab.class.getSimpleName());

        PolicySummaryPage.tableInsuredInformation.getRow(1).getCell(1).verify.value(
                testData.getValue(NonPremiumBearingEndorsementActionTab.FIRST_NAME.getLabel()) + " "
                        + testData.getValue(NonPremiumBearingEndorsementActionTab.LAST_NAME.getLabel()));
    }
}
