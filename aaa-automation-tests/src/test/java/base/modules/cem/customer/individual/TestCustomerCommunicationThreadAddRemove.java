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
 * @name Test Customer Add/Remove Communication Thread
 * @scenario
 * 1. Create Individual Customer
 * 2. Navigate to Communication tab
 * 3. Add Communication
 * 4. Add Communication Thread
 * 5. Verify that added Communication Thread is present in Communications table
 * 6. Remove previously added Communication Thread
 * 7. Verify that previously removed Communication Thread is not present in Communications table
 * @details
 */
public class TestCustomerCommunicationThreadAddRemove extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCommunicationThreadAddRemove() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("STEP: Add Communication for Customer # " + customerNumber);
        NavigationPage.toMainTab(CustomerSummaryTab.COMMUNICATION.get());
        customer.addCommunication().perform(tdCustomerIndividual.getTestData("Communication", "TestData"));
        int communicationsCount = CommunicationActionTab.tableCommunications.getRowsCount();

        log.info("TEST: Add Communication Thread for Customer # " + customerNumber);
        customer.addCommunicationThread().perform(tdCustomerIndividual.getTestData("CommunicationThread", "TestData"), 1);
        CommunicationActionTab.tableCommunications.verify.rowsCount(communicationsCount + 1);
        CommunicationActionTab.tableCommunications.getRow(1).getCell(CustomerConstants.CustomerCommunicationsTable.ENTITY_REFERENCE_ID).verify.value(
                CustomerSummaryPage.labelCustomerNumber.getValue());
        CommunicationActionTab.tableCommunications.getRow(2).getCell(CustomerConstants.CustomerCommunicationsTable.ENTITY_REFERENCE_ID).verify.value(
                CustomerSummaryPage.labelCustomerNumber.getValue());

        log.info("TEST: Remove Communication Thread for Customer # " + customerNumber);
        customer.removeCommunication().perform(1);
        CommunicationActionTab.tableCommunications.verify.rowsCount(communicationsCount);
    }
}
