/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.cem.campaigns.actiontabs;

import org.openqa.selenium.By;

import aaa.admin.metadata.cem.CemMetaData;
import aaa.common.ActionTab;
import aaa.common.Tab;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.table.Table;

public class UpdateCampaignActionTab extends ActionTab {

    public static Button buttonAddCampaignProducts = new Button(By.id("addProductButton"));
    public static Button buttonAddMarketingChannels = new Button(By.id("addChannelButton"));
    public static Button buttonDisassociateALL = new Button(By.id("disassociateAllLink"));

    public static Link linkCampaignProductsCollapsed = new Link(By.xpath("//div[text()='Campaign Products' and contains(@class, 'colps')]"));
    public static Link linkTargetCharacteristicsCollapsed = new Link(By.xpath("//div[text()='Target Characteristics' and contains(@class, 'colps')]"));
    public static Link linkMarketingChannelsCollapsed = new Link(By.xpath("//div[text()='Marketing Channels' and contains(@class, 'colps')]"));
    public static Link linkCampaignSchedulingCollapsed = new Link(By.xpath("//div[text()='Campaign Scheduling' and contains(@class, 'colps')]"));

    public static Table tableCampaignRelationshipInfo = new Table(By.xpath("//div[@id='campaignRelationshipsChain']//table"));
    /*public static String tableCampaignRelationshipInfo2 = "<body><h1>Hello world</h1></body>";
    @Language("HTML")
    String s = "";*/

    public UpdateCampaignActionTab() {
        super(CemMetaData.UpdateCampaignActionTab.class);
    }

    @Override
    public Tab fillTab(TestData td) {

        linkCampaignProductsCollapsed.click();
        linkTargetCharacteristicsCollapsed.click();
        linkMarketingChannelsCollapsed.click();
        linkCampaignSchedulingCollapsed.click();

        assetList.fill(td);

        return this;
    }
}
