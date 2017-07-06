/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.cem.campaigns.actiontabs;

import org.openqa.selenium.By;

import aaa.admin.metadata.cem.CemMetaData;
import aaa.common.ActionTab;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class SuspendCampaignActionTab extends ActionTab {
    public SuspendCampaignActionTab() {
        super(CemMetaData.SuspendCampaignActionTab.class);
        assetList = new AssetList(By.id("suspendCampaignModalPanel_container"), metaDataClass);
    }
}
