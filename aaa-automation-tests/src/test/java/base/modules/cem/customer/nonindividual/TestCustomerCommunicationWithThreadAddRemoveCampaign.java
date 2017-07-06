/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.admin.modules.cem.campaigns.Campaign;
import aaa.admin.modules.cem.campaigns.CampaignType;
import aaa.admin.modules.cem.campaigns.defaulttabs.CreateCampaignTab;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.actiontabs.CommunicationActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Add/Remove Communication with Thread
 * @scenario
 * 1. Create New Campaign in Admin application
 * 2. Create Non-Individual Customer
 * 3. Navigate to Campaign tab
 * 4. Add Communication
 * 5. Add Communication Thread
 * 6. Remove previously added Communication
 * 7. Verify that previously added Communication and Thread is present in Communications table
 * @details
 */
public class TestCustomerCommunicationWithThreadAddRemoveCampaign extends CustomerBaseTest {

    private Campaign campaign = new Campaign();
    private TestData tdCampaign = testDataManager.campaign.get(CampaignType.CAMPAIGNS);

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCommunicationWithThreadAddRemoveCampaign() {
        adminApp().open();

        campaign.create(tdCampaign.getTestData("DataGather", "TestData_NonIndividuals"));

        String campaignId = CreateCampaignTab.labelCampaignId.getValue();

        mainApp().open();
        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("STEP: Add Communication for Customer # " + customerNumber);
        NavigationPage.toMainTab(CustomerSummaryTab.CAMPAIGN.get());
        customer.addCommunication().perform(tdCustomerNonIndividual.getTestData("Communication", "TestData_Campaign").adjust(
                TestData.makeKeyPath(CommunicationActionTab.class.getSimpleName(),
                        CustomerMetaData.CommunicationActionTab.ENTITY_REFERENCE_ID.getLabel()),
                campaignId));
        CustomerSummaryPage.expandRelatedCommunications();

        log.info("STEP: Add Communication Thread for Customer # " + customerNumber);
        customer.addCommunicationThread().perform(tdCustomerNonIndividual.getTestData("CommunicationThread", "TestData_Campaign").adjust(
                TestData.makeKeyPath(CommunicationActionTab.class.getSimpleName(),
                        CustomerMetaData.CommunicationActionTab.ENTITY_REFERENCE_ID.getLabel()),
                campaignId), 1);
        int communicationsCount = CommunicationActionTab.tableCommunications.getRowsCount();

        log.info("TEST: Remove Communication for Customer # " + customerNumber);
        customer.removeCommunication().perform(2);
        CommunicationActionTab.tableCommunications.verify.rowsCount(communicationsCount);
    }
}
