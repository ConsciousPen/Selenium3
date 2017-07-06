/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.cem.campaigns;

import org.testng.annotations.Test;

import aaa.admin.metadata.cem.CemMetaData.SearchByField;
import aaa.admin.modules.cem.campaigns.CampaignEnum;
import aaa.admin.modules.cem.campaigns.defaulttabs.CreateCampaignTab;
import aaa.admin.pages.cem.CampaignPage;
import aaa.main.enums.CEMConstants;
import base.modules.cem.cem.CemBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer End Campaign
 * @scenario
 * 1. Navigate to Admin application
 * 2. Navigate to CEM tab
 * 3. Navigate to Campaigns subtab in left menu
 * 4. Add New Campaign for Individuals
 * 5. Start previously created Campaign
 * 6. Suspend previously created Campaign
 * 7. Verify that previously created Campaign is suspended
 * @details
 */
public class TestCustomerCampaignSuspend extends CemBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCampaignSuspend() {
        adminApp().open();

        log.info("STEP: Create New Campaign # ");
        campaign.create(tdCampaign.getTestData("DataGather", "TestData_Individuals"));

        String campaignId = CreateCampaignTab.labelCampaignId.getValue();

        campaign.navigate();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.CAMPAIGN_ID.getLabel()), campaignId));

        log.info("STEP: Start Campaign # " + campaignId);
        campaign.startCampaign().perform(1);

        log.info("TEST: Suspend Campaign # " + campaignId);
        campaign.suspendCampaign().perform(tdCampaign.getTestData("SuspendCampaign", "TestData"), 1);

        CampaignPage.tableCampaigns.getRow(1).getCell(CEMConstants.CEMCampaignsTable.STATUS).verify.value(CampaignEnum.CampaignStatus.SUSPENDED.get());
    }
}
