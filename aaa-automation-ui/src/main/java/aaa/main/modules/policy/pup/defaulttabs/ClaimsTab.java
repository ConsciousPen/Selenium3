/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.policy.pup.defaulttabs;

import aaa.common.Tab;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import toolkit.webdriver.controls.composite.assets.AssetList;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class ClaimsTab extends Tab {
    public ClaimsTab() {
        super(PersonalUmbrellaMetaData.ClaimsTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }
    
    public AssetList getAutoViolationsClaimsAssetList() {
    	return getAssetList().getAsset(PersonalUmbrellaMetaData.ClaimsTab.AUTO_VIOLATIONS_CLAIMS.getLabel(), AssetList.class);
	}
    public AssetList getPropertyClaimsAssetList() {
    	return getAssetList().getAsset(PersonalUmbrellaMetaData.ClaimsTab.PROPERTY_CLAIMS.getLabel(), AssetList.class);
	}
}
