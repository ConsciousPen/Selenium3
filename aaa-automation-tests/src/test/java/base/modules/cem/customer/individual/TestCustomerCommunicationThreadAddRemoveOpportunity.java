/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.CustomerConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.actiontabs.CommunicationActionTab;
import aaa.main.modules.customer.actiontabs.OpportunityActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Add/Remove Communication Thread from Opportunity
 * @scenario
 * 1. Create Individual Customer
 * 2. Add Opportunity
 * 3. Navigate to Opportunity tab
 * 4. Add Communication
 * 5. Add Communication Thread
 * 6. Verify that added Communication is present in Communications table
 * 7. Remove previously added Communication
 * 8. Verify that previously removed Communication is not present in Communications table
 * @details
 */
public class TestCustomerCommunicationThreadAddRemoveOpportunity extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCommunicationThreadAddRemoveOpportunity() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toMainTab(CustomerSummaryTab.OPPORTUNITY.get());
        customer.addOpportunity().perform(tdCustomerIndividual.getTestData("Opportunity", "TestData"));
        String opportunityId = OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.ID).getValue();

        log.info("STEP: Add Communication for Customer # " + customerNumber);
        NavigationPage.toMainTab(CustomerSummaryTab.OPPORTUNITY.get());
        customer.addCommunication().perform(tdCustomerIndividual.getTestData("Communication", "TestData_Opportunity").adjust(
                TestData.makeKeyPath(CommunicationActionTab.class.getSimpleName(),
                        CustomerMetaData.CommunicationActionTab.ENTITY_REFERENCE_ID.getLabel()),
                opportunityId));
        CustomerSummaryPage.expandRelatedCommunications();
        int communicationsCount = CommunicationActionTab.tableCommunications.getRowsCount();

        log.info("TEST: Add Communication Thread for Customer # " + customerNumber);
        customer.addCommunicationThread().perform(tdCustomerIndividual.getTestData("CommunicationThread", "TestData_Opportunity").adjust(
                TestData.makeKeyPath(CommunicationActionTab.class.getSimpleName(),
                        CustomerMetaData.CommunicationActionTab.ENTITY_REFERENCE_ID.getLabel()),
                opportunityId), 1);
        CommunicationActionTab.tableCommunications.verify.rowsCount(communicationsCount + 1);
        CommunicationActionTab.tableCommunications.getRow(1).getCell(CustomerConstants.CustomerCommunicationsTable.ENTITY_REFERENCE_ID).verify.value(opportunityId);
        CommunicationActionTab.tableCommunications.getRow(2).getCell(CustomerConstants.CustomerCommunicationsTable.ENTITY_REFERENCE_ID).verify.value(opportunityId);

        log.info("TEST: Remove Communication Thread for Customer # " + customerNumber);
        customer.removeCommunication().perform(1);
        CommunicationActionTab.tableCommunications.verify.rowsCount(communicationsCount);
    }
}
