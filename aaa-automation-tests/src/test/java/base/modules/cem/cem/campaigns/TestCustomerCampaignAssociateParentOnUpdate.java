/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.cem.campaigns;

import org.testng.annotations.Test;

import aaa.admin.metadata.cem.CemMetaData;
import aaa.admin.metadata.cem.CemMetaData.SearchByField;
import aaa.admin.metadata.cem.CemMetaData.UpdateCampaignActionTab;
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
 * 4. Add Campaign A for Individuals
 * 5. Add Campaign B for Individuals
 * 6. Perform Update action for Campaign B
 * 7. a) Fill in ID of Campaign A
 * 7. b) Click Associate
 * 7. c) Finish creating Campaign B
 * 8. Click on Campaign B ID in Campaign tab
 * 9. Verify that Campaign A is parent of Campaign B
 * @details https://jira.exigeninsurance.com/browse/EISDEV-134953
 */
public class TestCustomerCampaignAssociateParentOnUpdate extends CemBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCampaignAssociateParentOnUpdate() {
        adminApp().open();

        log.info("STEP: Create New Campaign # ");
        campaign.create(tdCampaign.getTestData("DataGather", "TestData_Individuals"));

        String parentCampaignId = CreateCampaignTab.labelCampaignId.getValue();

        log.info("STEP: Create New Campaign # ");
        campaign.create(tdCampaign.getTestData("DataGather", "TestData_Individuals"));

        String campaignId = CreateCampaignTab.labelCampaignId.getValue();

        campaign.navigate();

        log.info("Test: Associate Parent Campaign on Update # " + parentCampaignId);
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.CAMPAIGN_ID.getLabel()), campaignId));

        campaign.updateCampaign().perform(tdSpecific.getTestData("TestData").adjust(TestData.makeKeyPath(
                UpdateCampaignActionTab.class.getSimpleName(), CemMetaData.UpdateCampaignActionTab.ID.getLabel()),
                parentCampaignId).resolveLinks(), 1);

        campaign.navigate();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.CAMPAIGN_ID.getLabel()), campaignId));

        CampaignPage.tableCampaigns.getRow(1).getCell(CEMConstants.CEMCampaignsTable.CAMPAIGN_ID).controls.links.getFirst().click();

        CreateCampaignTab.tableCampaignInfo.getRow(1).getCell(CEMConstants.CEMCampaignInfoTable.RELATIONSHIP).verify.value("Parent");
        CreateCampaignTab.tableCampaignInfo.getRow(1).getCell(CEMConstants.CEMCampaignsTable.CAMPAIGN_ID).verify.value(parentCampaignId);
    }
}
