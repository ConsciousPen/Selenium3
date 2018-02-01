/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ca.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.main.metadata.policy.HomeCaMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class GeneralTab extends Tab {
    public GeneralTab() {
        super(HomeCaMetaData.GeneralTab.class);
    }

    public Button btnContinue =
            new Button(By.xpath("//input[@id='policyDataGatherForm:continueBtn_AAAGeneralPageContinueAction_footer' or @id='policyDataGatherForm:nextInquiry_footer']"), Waiters.AJAX);

    @Override
    public Tab submitTab() {
    	btnContinue.click();
        return this;
    }
    
    public AssetList getPolicyInfoAssetList() {
    	return getAssetList().getAsset(HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(), AssetList.class);
	}
    public AssetList getCurrentCarrierAssetList() {
    	return getAssetList().getAsset(HomeCaMetaData.GeneralTab.CURRENT_CARRIER.getLabel(), AssetList.class);
	}
}
