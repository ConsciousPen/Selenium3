/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.admin.metadata.cem.CemMetaData.SearchByField;
import aaa.admin.modules.cem.campaigns.Campaign;
import aaa.admin.modules.cem.campaigns.CampaignType;
import aaa.admin.modules.cem.campaigns.defaulttabs.CreateCampaignTab;
import aaa.common.enums.NavigationEnum.AdminAppLeftMenu;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.CustomerConstants;
import aaa.main.modules.customer.actiontabs.OpportunityActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Associate Campaign On Create Opportunity
 * @scenario
 * 1. Create Non Individual Customer
 * 2. Create Campaign
 * 3. Navigate to Customer tab
 * 4. Click on Opportunity tab
 * 5. Add Associate campaign to Opportunity
 * 6. Verify that, Opportunity status is In Pipeline
 * 7. Remove associated campaign
 * 8. Verify that Opportunity status is Draft
 * @details
 */
public class TestCustomerAssociateCampaignOnCreateOpportunity extends CustomerBaseTest {

    private Campaign campaign = new Campaign();
    private TestData tdCampaign = testDataManager.campaign.get(CampaignType.CAMPAIGNS);

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerAssociateCampaignOnCreateOpportunity() {
        adminApp().open();

        campaign.create(tdCampaign.getTestData("DataGather", "TestData_NonIndividuals"));

        String campaignId = CreateCampaignTab.labelCampaignId.getValue();

        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.CEM_CAMPAIGNS.get());
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.CAMPAIGN_ID.getLabel()), campaignId));
        campaign.startCampaign().perform(1);

        mainApp().open();
        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Add Associate Campaign Opportunity tab  " + customerNumber);

        NavigationPage.toMainTab(CustomerSummaryTab.OPPORTUNITY.get());
        log.info("STEP: Add Associate Campaign to Opportunity tab ");
        customer.addAssociateCampaignOnOpportunity().perform(tdCustomerNonIndividual.getTestData("Opportunity", "TestData"), campaignId);

        OpportunityActionTab.tableOpportunity.verify.rowsCount(1);
        OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.STATUS).verify.value("In Pipeline");

        log.info("STEP: Remove Associated Campaign to Opportunity tab " + campaignId);
        customer.removeAssociateCampaignOnOpportunity().perform(1);
        OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.STATUS).verify.value("Draft");
    }
}
