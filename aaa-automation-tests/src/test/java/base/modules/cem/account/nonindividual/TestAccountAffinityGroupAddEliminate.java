/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.account.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.pages.summary.CustomerSummaryPage;
import base.modules.cem.account.AccountBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Account Add/Eliminate Affinity Group
 * @scenario
 * 1. Create Account with Non-Individual Customer
 * 2. Navigate to Account tab
 * 3. Add Affinity Group
 * 4. Verify that Affinity Group is present in Affinity Groups table in Account tab
 * 5. Eliminate previously created Affinity Group
 * 6. Verify that previously created Affinity Group is not present in Affinity Groups table in Account tab
 * @details
 */
public class TestAccountAffinityGroupAddEliminate extends AccountBaseTest {

    @Test
    @TestInfo(component = "CRM.Account")
    public void testAccountAffinityGroupAddEliminate() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        NavigationPage.toMainTab(CustomerSummaryTab.ACCOUNT.get());
        String accountNumber = CustomerSummaryPage.labelAccountNumber.getValue();
        int affinityGroupsCount = CustomerSummaryPage.tableAffinityGroups.getRowsCount();

        log.info("TEST: Add Affinity Group to Account # " + accountNumber);
        account.addAffinityGroup().perform(tdAccountNonIndividual.getTestData("AffinityGroups", "TestData"));
        CustomerSummaryPage.tableAffinityGroups.verify.rowsCount(affinityGroupsCount + 1);

        log.info("TEST: Eliminate Affinity Group from Account # " + accountNumber);
        account.eliminateAffinityGroup().perform(1);
        CustomerSummaryPage.tableAffinityGroups.verify.rowsCount(affinityGroupsCount);
    }
}
