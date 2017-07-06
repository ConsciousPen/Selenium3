/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.customer.actiontabs.CommunicationActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Add/Remove Communication with Thread from Contacts and Relationships
 * @scenario
 * 1. Create Non-Individual Customer
 * 2. Navigate to Contacts and Relationships tab
 * 3. Add Communication
 * 4. Add Communication Thread
 * 5. Remove previously added Communication
 * 6. Verify that previously added Communication and Thread is present in Communications table
 * @details
 */
public class TestCustomerCommunicationWithThreadAddRemoveContactsRelationships extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCommunicationWithThreadAddRemoveContactsRelationships() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("STEP: Add Communication for Customer # " + customerNumber);
        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        customer.addCommunication().perform(tdCustomerNonIndividual.getTestData("Communication", "TestData"));
        CustomerSummaryPage.expandRelatedCommunications();

        log.info("STEP: Add Communication Thread for Customer # " + customerNumber);
        customer.addCommunicationThread().perform(tdCustomerNonIndividual.getTestData("CommunicationThread", "TestData"), 1);
        int communicationsCount = CommunicationActionTab.tableCommunications.getRowsCount();

        log.info("TEST: Remove Communication for Customer # " + customerNumber);
        customer.removeCommunication().perform(2);
        CommunicationActionTab.tableCommunications.verify.rowsCount(communicationsCount);
    }
}
