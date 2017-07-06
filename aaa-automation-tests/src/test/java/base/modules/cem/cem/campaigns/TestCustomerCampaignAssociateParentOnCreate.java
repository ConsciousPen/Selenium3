/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.cem.campaigns;

import org.testng.annotations.Test;

import aaa.admin.metadata.cem.CemMetaData;
import aaa.admin.metadata.cem.CemMetaData.SearchByField;
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
 * 5. Initiate New Campaign B for Individuals
 * 5. a) Fill in ID of Campaign A
 * 5. b) Click Associate
 * 5. c) Finish creating Campaign B
 * 6. Click on Campaign B ID in Campaign tab
 * 7. Verify that Campaign A is parent of Campaign B
 * @details https://jira.exigeninsurance.com/browse/EISDEV-134953
 */
public class TestCustomerCampaignAssociateParentOnCreate extends CemBaseTest {

    @Test(groups = {"7.2_All_UC_AssociateWithAParent/ChildCampaign"})
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCampaignAssociateParentOnCreate() {
        adminApp().open();

        log.info("STEP: Create New Campaign # ");
        campaign.create(tdCampaign.getTestData("DataGather", "TestData_Individuals"));

        String parentCampaignId = CreateCampaignTab.labelCampaignId.getValue();

        campaign.navigate();

        log.info("Test: Associate Parent Campaign # " + parentCampaignId);
        campaign.create(tdSpecific.getTestData("TestData").adjust(TestData.makeKeyPath(CreateCampaignTab.class.getSimpleName(),
                CemMetaData.CreateCampaignTab.ID.getLabel()), parentCampaignId).resolveLinks());

        String campaignId = CreateCampaignTab.labelCampaignId.getValue();

        campaign.navigate();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.CAMPAIGN_ID.getLabel()), campaignId));

        CampaignPage.tableCampaigns.getRow(1).getCell(CEMConstants.CEMCampaignsTable.CAMPAIGN_ID).controls.links.getFirst().click();

        CreateCampaignTab.tableCampaignInfo.getRow(1).getCell(CEMConstants.CEMCampaignInfoTable.RELATIONSHIP).verify.value("Parent");
        CreateCampaignTab.tableCampaignInfo.getRow(1).getCell(CEMConstants.CEMCampaignsTable.CAMPAIGN_ID).verify.value(parentCampaignId);
    }
}
