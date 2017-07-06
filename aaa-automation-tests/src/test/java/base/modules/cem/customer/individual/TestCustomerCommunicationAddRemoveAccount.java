/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.CustomerConstants;
import aaa.main.modules.customer.actiontabs.CommunicationActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Add/Remove Communication from Account
 * @scenario
 * 1. Create Individual Customer
 * 2. Navigate to Customer Info tab
 * 3. Add Communication
 * 4. Verify that added Communication is present in Communications table in Communication tab
 * 5. Remove previously added Communication
 * 6. Verify that previously removed Communication is not present in Communications table
 * @details
 */
public class TestCustomerCommunicationAddRemoveAccount extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCommunicationAddRemoveAccount() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Add Communication for Customer # " + customerNumber);
        NavigationPage.toMainTab(CustomerSummaryTab.ACCOUNT.get());
        customer.addCommunication().perform(tdCustomerIndividual.getTestData("Communication", "TestData_Account"));
        CustomerSummaryPage.expandRelatedCommunications();
        CommunicationActionTab.tableCommunications.getRow(1).getCell(CustomerConstants.CustomerCommunicationsTable.ENTITY_REFERENCE_ID).verify.value(CustomerSummaryPage.labelAccountNumber.getValue());

        log.info("TEST: Remove Communication for Customer # " + customerNumber);
        customer.removeCommunication().perform(1);
        CommunicationActionTab.tableCommunications.verify.rowsCount(0);
    }
}
