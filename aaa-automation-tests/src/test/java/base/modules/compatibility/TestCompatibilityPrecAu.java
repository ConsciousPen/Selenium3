/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.compatibility;

import java.util.Stack;

import org.testng.annotations.Test;

import aaa.common.pages.MainPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.TestData;

public class TestCompatibilityPrecAu extends CompatibilityBaseTest {

    private PolicyType type = PolicyType.AUTO_SS;
    private IPolicy policy = type.get();
    private TestData tdPolicy = testDataManager.policy.get(type);

    @Test
    public void testCompatibilityPrecAu() {

        Stack<String> policyNumbers = new Stack<>();
        policyNumbers.addAll(tdCompatibility.getTestData("TestData").getList(type.toString()));

        mainApp().open();
        MainPage.QuickSearch.search(policyNumbers.pop());

        policy.cancel().perform(tdPolicy.getTestData("Cancellation", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
    }
}
