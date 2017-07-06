/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.account.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.AccountConstants;
import aaa.main.modules.customer.actiontabs.CommunicationActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import base.modules.cem.account.AccountBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Account Add/Remove Communication
 * @scenario
 * 1. Create Account with Individual Customer
 * 2. Navigate to Account tab
 * 3. Add Communication
 * 4. Verify that Communication is present in Communications table in Account tab
 * 5. Remove previously created Communication
 * 6. Verify that previously created Communication is not present in Communications table in Account tab
 * @details
 */
public class TestAccountCommunicationAddRemove extends AccountBaseTest {

    @Test
    @TestInfo(component = "CRM.Account")
    public void testAccountCommunicationAddRemove() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        NavigationPage.toMainTab(CustomerSummaryTab.ACCOUNT.get());
        String accountNumber = CustomerSummaryPage.labelAccountNumber.getValue();

        CustomerSummaryPage.linkRelatedCommunications.click();
        int communicationsCount = CommunicationActionTab.tableCommunications.getRowsCount();

        log.info("TEST: Add Communication for Account # " + accountNumber);
        account.addCommunication().perform(tdAccountIndividual.getTestData("Communication", "TestData"));
        CommunicationActionTab.tableCommunications.getRow(1).getCell(AccountConstants.AccountCommunicationsTable.ENTITY_REFERENCE_ID).verify.value(accountNumber);

        log.info("TEST: Remove Communication for Account # " + accountNumber);
        account.removeCommunication().perform(1);
        CommunicationActionTab.tableCommunications.verify.rowsCount(communicationsCount);
    }
}
