/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.cem.campaigns;

import org.testng.annotations.Test;

import aaa.admin.metadata.cem.CemMetaData.SearchByField;
import aaa.admin.modules.cem.campaigns.defaulttabs.CreateCampaignTab;
import aaa.admin.pages.cem.CampaignPage;
import base.modules.cem.cem.CemBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Start Campaign
 * @scenario
 * 1. Navigate to Admin application
 * 2. Navigate to CEM tab
 * 3. Navigate to Campaigns subtab in left menu
 * 4. Add New Campaign for Individuals
 * 5. Archive previously created Campaign
 * 6. Verify that previously created Campaign is not present in Campaigns table
 * @details
 */
public class TestCustomerCampaignArchive extends CemBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCampaignArchive() {
        adminApp().open();

        log.info("STEP: Create New Campaign # ");
        campaign.create(tdCampaign.getTestData("DataGather", "TestData_Individuals"));

        String campaignId = CreateCampaignTab.labelCampaignId.getValue();

        campaign.navigate();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.CAMPAIGN_ID.getLabel()), campaignId));

        log.info("TEST: Archive Campaign # " + campaignId);
        campaign.archiveCampaign().perform(1);

        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.CAMPAIGN_ID.getLabel()), campaignId));

        CampaignPage.tableCampaigns.getRow(1).verify.present(false);
    }
}
