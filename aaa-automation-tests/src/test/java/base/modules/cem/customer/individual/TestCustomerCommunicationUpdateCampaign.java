/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.admin.modules.cem.campaigns.Campaign;
import aaa.admin.modules.cem.campaigns.CampaignType;
import aaa.admin.modules.cem.campaigns.defaulttabs.CreateCampaignTab;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.CustomerConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.actiontabs.CommunicationActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Update Communication from Campaign
 * @scenario
 * 1. Create New Campaign in Admin application
 * 2. Create Individual Customer
 * 3. Navigate to Campaign tab
 * 3. Add Communication
 * 4. Update previously added Communication
 * 5. Verify that previously added Communication is updated
 * @details
 */
public class TestCustomerCommunicationUpdateCampaign extends CustomerBaseTest {

    private Campaign campaign = new Campaign();
    private TestData tdCampaign = testDataManager.campaign.get(CampaignType.CAMPAIGNS);

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCommunicationUpdateCampaign() {
        adminApp().open();

        campaign.create(tdCampaign.getTestData("DataGather", "TestData_Individuals"));

        String campaignId = CreateCampaignTab.labelCampaignId.getValue();

        mainApp().open();
        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Add Communication for Customer # " + customerNumber);
        NavigationPage.toMainTab(CustomerSummaryTab.CAMPAIGN.get());
        customer.addCommunication().perform(tdCustomerIndividual.getTestData("Communication", "TestData_Campaign").adjust(
                TestData.makeKeyPath(CommunicationActionTab.class.getSimpleName(),
                        CustomerMetaData.CommunicationActionTab.ENTITY_REFERENCE_ID.getLabel()),
                campaignId));

        log.info("TEST: Update Communication for Customer # " + customerNumber);
        CustomerSummaryPage.expandRelatedCommunications();
        customer.updateCommunication().perform(1, tdSpecific.getTestData("TestData"));
        CommunicationActionTab.tableCommunications.getRow(1).getCell(CustomerConstants.CustomerCommunicationsTable.CHANNEL).verify.value(tdSpecific.getTestData("TestData")
                .getTestData(CustomerMetaData.CommunicationActionTab.class.getSimpleName())
                .getValue(CustomerMetaData.CommunicationActionTab.COMMUNICATION_CHANNEL.getLabel()));
    }
}
