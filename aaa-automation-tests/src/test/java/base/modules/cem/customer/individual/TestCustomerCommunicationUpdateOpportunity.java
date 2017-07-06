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
 * @name Test Customer Update Communication from Opportunity
 * @scenario
 * 1. Create Individual Customer
 * 2. Navigate to Opportunity tab
 * 3. Add Opportunity
 * 4. Add Communication
 * 5. Update previously added Communication
 * 6. Verify that previously added Communication is updated
 * @details
 */
public class TestCustomerCommunicationUpdateOpportunity extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCommunicationUpdateOpportunity() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toMainTab(CustomerSummaryTab.OPPORTUNITY.get());
        customer.addOpportunity().perform(tdCustomerIndividual.getTestData("Opportunity", "TestData"));
        String opportunityId = OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.ID).getValue();

        log.info("TEST: Add Communication for Customer # " + customerNumber);
        customer.addCommunication().perform(tdCustomerIndividual.getTestData("Communication", "TestData_Opportunity").adjust(
                TestData.makeKeyPath(CommunicationActionTab.class.getSimpleName(),
                        CustomerMetaData.CommunicationActionTab.ENTITY_REFERENCE_ID.getLabel()),
                opportunityId));

        log.info("TEST: Update Communication for Customer # " + customerNumber);
        CustomerSummaryPage.expandRelatedCommunications();
        customer.updateCommunication().perform(1, tdSpecific.getTestData("TestData"));
        CommunicationActionTab.tableCommunications.getRow(1).getCell(CustomerConstants.CustomerCommunicationsTable.CHANNEL).verify.value(tdSpecific.getTestData("TestData")
                .getTestData(CustomerMetaData.CommunicationActionTab.class.getSimpleName())
                .getValue(CustomerMetaData.CommunicationActionTab.COMMUNICATION_CHANNEL.getLabel()));
    }
}
