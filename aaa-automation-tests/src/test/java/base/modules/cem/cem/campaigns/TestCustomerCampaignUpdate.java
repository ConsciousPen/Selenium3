/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.cem.campaigns;

import org.testng.annotations.Test;

import aaa.admin.metadata.cem.CemMetaData.SearchByField;
import aaa.admin.modules.cem.campaigns.defaulttabs.CreateCampaignTab;
import aaa.admin.pages.cem.CampaignPage;
import aaa.main.enums.CEMConstants;
import base.modules.cem.cem.CemBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Update Campaign
 * @scenario
 * 1. Navigate to Admin application
 * 2. Navigate to CEM tab
 * 3. Navigate to Campaigns subtab in left menu
 * 4. Add New Campaign for Individuals
 * 5. Update previously created Campaign
 * 6. Verify that previously created Campaign is updated
 * @details
 */
public class TestCustomerCampaignUpdate extends CemBaseTest {

    @Test(groups = {"7.2_All_UC_Enter/UpdateCampaignInformation", "7.2_All_UC_Campaign-BasicSearch"})
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCampaignUpdate() {
        adminApp().open();

        log.info("STEP: Create New Campaign # ");
        campaign.create(tdCampaign.getTestData("DataGather", "TestData_Individuals"));

        String campaignId = CreateCampaignTab.labelCampaignId.getValue();

        campaign.navigate();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.CAMPAIGN_ID.getLabel()), campaignId));

        log.info("TEST: Update Campaign # " + campaignId);
        campaign.updateCampaign().perform(tdSpecific.getTestData("TestData"), 1);

        String campaignName = CreateCampaignTab.labelCampaignName.getValue();
        String updatedCampaignId = CreateCampaignTab.labelCampaignId.getValue();

        campaign.navigate();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.CAMPAIGN_ID.getLabel()), updatedCampaignId));

        CampaignPage.tableCampaigns.getRow(1).getCell(CEMConstants.CEMCampaignsTable.CAMPAIGN_NAME).verify.value(campaignName);
    }
}
