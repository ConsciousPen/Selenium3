/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.cem.campaigns;

import java.util.ArrayList;

import org.testng.annotations.Test;

import aaa.admin.metadata.cem.CemMetaData;
import aaa.admin.metadata.cem.CemMetaData.SearchByField;
import aaa.admin.metadata.cem.CemMetaData.UpdateCampaignActionTab;
import aaa.admin.modules.cem.campaigns.defaulttabs.CreateCampaignTab;
import aaa.admin.pages.cem.CampaignPage;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.CEMConstants;
import base.modules.cem.cem.CemBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Yauheni Martman
 * @name Test Customer End Campaign
 * @scenario
 * 1. Navigate to Admin application
 * Navigate to CEM tab
 * Navigate to Campaigns subtab in left menu
 * 2. Add Campaign 1 for Individuals
 * 3. Add Campaign 2 for Individuals
 * Perform Update action for Campaign 2
 * a) a) Fill in ID of Campaign 1
 * b) Click Associate
 * c) Click Disassociate campaign 1 in table.
 * 4. Click on Campaign 2 ID in Campaign tab
 * Verify that Campaign 2 has no parent and child campaigns
 * 5. Add Campaign 3 for Individuals
 * Perform Update action for Campaign 3
 * a) Fill in ID of Campaign 1
 * b) Click Associate
 * c) Repeat step a, b for Campaign 2
 * d)Click on Disassociate All button
 * e) Finish creating Campaign 3
 * 6. Click on Campaign 3 ID in Campaign tab
 * Verify that Campaign 3 has no parent and child campaigns
 * @details https://jira.exigeninsurance.com/browse/EISDEV-134960
 * https://jira.exigeninsurance.com/browse/EISDEV-134959
 */
public class TestCustomerCampaignDisassociateParentOnUpdate extends CemBaseTest {
    ArrayList<String> parentCampaigns = new ArrayList<>();

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCampaignDisassociateParentOnUpdate() {

        adminApp().open();

        //2. Add Campaign 1 for Individuals
        log.info("STEP: Create New Campaigns 1, 2, 3 # ");
        campaign.create(tdCampaign.getTestData("DataGather", "TestData_Individuals"));
        parentCampaigns.add(CreateCampaignTab.labelCampaignId.getValue());

        //3. Add Campaign 2 for Individuals
        campaign.create(tdCampaign.getTestData("DataGather", "TestData_Individuals"));
        String campaignId = CreateCampaignTab.labelCampaignId.getValue();
        parentCampaigns.add(campaignId);

        //Perform Update action for Campaign 2
        campaign.navigate();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.CAMPAIGN_ID.getLabel()), campaignId));

        campaign.updateCampaign().start(1);
        campaign.updateCampaign().getView().fill(tdSpecific.getTestData("TestData").adjust(TestData.makeKeyPath(
                UpdateCampaignActionTab.class.getSimpleName(), CemMetaData.UpdateCampaignActionTab.ID.getLabel()),
                parentCampaigns).resolveLinks());
        CreateCampaignTab.tableCampaignRelationshipInfo.verify.rowsCount(1);

        //Click Disassociate campaign 1 in table.
        log.info("Test: Disassociate campaing from action in table in Campaign 2 # ");
        CreateCampaignTab.tableCampaignRelationshipInfo.getRow(CEMConstants.CEMCampaignRelationshipInfoTable.CAMPAIGN_ID, parentCampaigns.get(0)).getCell(
                CEMConstants.CEMCampaignRelationshipInfoTable.ACTION).controls.links.get(ActionConstants.DISASSOCIATE).click();
        CreateCampaignTab.dialogDisassociate.confirm();

        CreateCampaignTab.tableCampaignRelationshipInfo.verify.present(false);
        CreateCampaignTab.buttonSave.click();

        //4. Verify that Campaign 2 has no parent and child campaigns
        log.info("Test: Click on Campaign 3 ID in Campaign tab");
        campaign.navigate();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.CAMPAIGN_ID.getLabel()), campaignId));
        CampaignPage.tableCampaigns.getRow(1).getCell(CEMConstants.CEMCampaignsTable.CAMPAIGN_ID).controls.links.getFirst().click();

        log.info("Test: Verify that Campaign 3 has no parent and child campaigns");
        CreateCampaignTab.tableCampaignInfo.verify.present(false);

        //5. Add Campaign 3 for Individuals
        campaign.create(tdCampaign.getTestData("DataGather", "TestData_Individuals"));
        campaignId = CreateCampaignTab.labelCampaignId.getValue();

        //Perform Update action for Campaign 3
        log.info("Test: Associate Campaign 1 and 2 with Campaign 3 on Update # ");
        campaign.navigate();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.CAMPAIGN_ID.getLabel()), campaignId));

        campaign.updateCampaign().start(1);
        campaign.updateCampaign().getView().fill(tdSpecific.getTestData("TestData").adjust(TestData.makeKeyPath(
                UpdateCampaignActionTab.class.getSimpleName(), CemMetaData.UpdateCampaignActionTab.ID.getLabel()),
                parentCampaigns).resolveLinks());
        CreateCampaignTab.tableCampaignRelationshipInfo.verify.rowsCount(2);

        //Click on Disassociate All button
        log.info("Test: Disassociate All Campaings in Campaign 3 # ");
        CreateCampaignTab.buttonDisassociateALL.click();
        CreateCampaignTab.dialogDisassociateAll.confirm();

        CreateCampaignTab.tableCampaignRelationshipInfo.verify.present(false);
        CreateCampaignTab.buttonSave.click();

        //6.Verify that Campaign 3 has no parent and child campaigns
        log.info("Test: Click on Campaign 3 ID in Campaign tab");
        campaign.navigate();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.CAMPAIGN_ID.getLabel()), campaignId));
        CampaignPage.tableCampaigns.getRow(1).getCell(CEMConstants.CEMCampaignsTable.CAMPAIGN_ID).controls.links.getFirst().click();

        log.info("Test: Verify that Campaign 3 has no parent and child campaigns");
        CreateCampaignTab.tableCampaignInfo.verify.present(false);
    }
}
