/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.account.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.AccountConstants;
import aaa.main.pages.summary.CustomerSummaryPage;
import base.modules.cem.account.AccountBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Account Update
 * @scenario
 * 1. Create Account with Individual Customer
 * 2. Navigate to Account tab
 * 3. Perform Update action
 * 3. a) set Confidential Account on Update
 * 4. Verify that Confidential status is changed to Yes
 * @details
 */
public class TestAccountUpdate extends AccountBaseTest {

    @Test
    @TestInfo(component = "CRM.Account")
    public void testAccountUpdate() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        NavigationPage.toMainTab(CustomerSummaryTab.ACCOUNT.get());
        String accountNumber = CustomerSummaryPage.labelAccountNumber.getValue();
        CustomerSummaryPage.labelConfidential.verify.value(AccountConstants.AccountConfidential.NO);

        log.info("TEST: Update Account # " + accountNumber);
        account.update().perform(tdSpecific.getTestData("TestData"));
        CustomerSummaryPage.labelConfidential.verify.value(AccountConstants.AccountConfidential.YES);
    }
}
