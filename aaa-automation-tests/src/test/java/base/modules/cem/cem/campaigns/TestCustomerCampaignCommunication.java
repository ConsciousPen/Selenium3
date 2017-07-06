/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.cem.campaigns;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.admin.metadata.cem.CemMetaData.SearchByField;
import aaa.admin.modules.cem.campaigns.CampaignEnum.CampaignStatus;
import aaa.admin.modules.cem.campaigns.defaulttabs.CreateCampaignTab;
import aaa.admin.pages.cem.CampaignPage;
import aaa.common.enums.SearchEnum.SearchBy;
import aaa.common.enums.SearchEnum.SearchFor;
import aaa.common.pages.MainPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.CEMConstants;
import aaa.main.modules.customer.Customer;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.customer.actiontabs.CommunicationActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import base.modules.cem.cem.CemBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Yauheni Martman
 * @name Test Automatic communication generation on Start Campaign
 * @scenario
 * 1. Create New Customer A with email
 * 2. Create New Campaign C with Marketing channels
 * 3. Open Customer A
 * 4. Verify Communication tab have opened communication channel.
 * 5. Suspend Campaign C
 * 6. Restart Campaign C
 * 7. Open Customer A
 * 8. Verify Communication tab have new (second) opened communication channel.
 * @details
 */
public class TestCustomerCampaignCommunication extends CemBaseTest {

    CustomerType customerType = CustomerType.NON_INDIVIDUAL;
    TestData tdCustomer = testDataManager.customer.get(customerType);
    Customer customer = new Customer();

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCampaignCommunication() {
        mainApp().open();

        log.info("STEP: Create New Customer A with email");
        TestData customerData = tdCustomer.getTestData("DataGather", "TestData");
        customer.createViaUI(customerData);
        customer.update().perform(tdCustomer.getTestData("DataGather", "TestData").adjust(
                tdCustomer.getTestData("ContactsDetails", "Adjustment_NewEmail")));

        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();
        log.info("Created customer id: " + customerId);

        adminApp().open();

        log.info("STEP: Create New Campaign C with Marketing channels");
        campaign.create(tdCampaign.getTestData("DataGather", "TestData_Communications"));

        String campaignId = CreateCampaignTab.labelCampaignId.getValue();

        campaign.navigate();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.CAMPAIGN_ID.getLabel()), campaignId));
        CampaignPage.tableCampaigns.getRow(1).getCell(CEMConstants.CEMCampaignsTable.STATUS).verify.value(CampaignStatus.ACTIVE.get());

        log.info("STEP: Open Customer A");
        mainApp().open();
        MainPage.QuickSearch.buttonSearchPlus.click();
        SearchPage.search(SearchFor.CUSTOMER, SearchBy.CUSTOMER, customerId);

        Map<Object, String> verifiedRow = new HashMap<>();
        verifiedRow.put("Entity Reference ID", campaignId);
        verifiedRow.put("Direction", "Outbound");
        verifiedRow.put("Channel", "Email");
        verifiedRow.put("Entity Type", "Campaign");

        log.info("STEP: Verify Communication tab have opened communication channel");
        CustomerSummaryPage.linkCommunicationTab.click();
        synchonizeCustomerCommunicationTab(100, 1);
        CommunicationActionTab.tableCommunications.getRow(1).verify.values(verifiedRow);
        String communicationId = CommunicationActionTab.tableCommunications.getRow(1).getCell(CEMConstants.CEMCommunicationsTable.ID).getValue();
        CommunicationActionTab.tableCommunications.getRow(1).getCell(CEMConstants.CEMCommunicationsTable.ID).controls.links.getFirst().click();
        CommunicationActionTab.tableCommunicationInfoDetails.getColumn(CEMConstants.CEMCommunicationInfoDetailsTable.COMMUNICATION_DESCRIPTION).getCell(1).verify
                .value("Marketing outbound campaign");

        log.info("STEP: Suspend Campaign C # " + campaignId);
        adminApp().open();
        campaign.navigate();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(SearchByField.class.getSimpleName(), SearchByField.CAMPAIGN_ID.getLabel()), campaignId));
        campaign.suspendCampaign().perform(tdCampaign.getTestData("SuspendCampaign", "TestData"), 1);
        CampaignPage.tableCampaigns.getRow(1).getCell(CEMConstants.CEMCampaignsTable.STATUS).verify.value(CampaignStatus.SUSPENDED.get());

        log.info("STEP: Restart Campaign C # " + campaignId);
        campaign.restartCampaign().perform(tdCampaign.getTestData("RestartCampaign", "TestData"), 1);
        campaign.navigate();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(SearchByField.class.getSimpleName(), SearchByField.CAMPAIGN_ID.getLabel()), campaignId));
        CampaignPage.tableCampaigns.getRow(1).getCell(CEMConstants.CEMCampaignsTable.STATUS).verify.value(CampaignStatus.ACTIVE.get());

        log.info("STEP: Open Customer A");
        mainApp().open();
        SearchPage.search(SearchFor.CUSTOMER, SearchBy.CUSTOMER, customerId);

        log.info("STEP: Verify Communication tab have new (second) opened communication channel");
        CustomerSummaryPage.linkCommunicationTab.click();
        synchonizeCustomerCommunicationTab(100, 2);

        verifiedRow = new HashMap<>();
        verifiedRow.put("Entity Reference ID", campaignId);
        verifiedRow.put("Direction", "Outbound");
        verifiedRow.put("Channel", "Email");
        verifiedRow.put("Entity Type", "Campaign");

        CommunicationActionTab.tableCommunications.getRow(1).verify.values(verifiedRow);
        CustomAssert.assertFalse("Not created new communication",
                communicationId.equals(CommunicationActionTab.tableCommunications.getRow(1).getCell(CEMConstants.CEMCommunicationsTable.ID).getValue()));
        CommunicationActionTab.tableCommunications.getRow(1).getCell(CEMConstants.CEMCommunicationsTable.ID).controls.links.getFirst().click();
        CommunicationActionTab.tableCommunicationInfoDetails.getColumn(CEMConstants.CEMCommunicationInfoDetailsTable.COMMUNICATION_DESCRIPTION).getCell(1).verify.value("Marketing outbound campaign");
    }
}
