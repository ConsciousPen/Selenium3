/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.defaulttabs;

import aaa.common.Tab;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.webdriver.controls.composite.assets.AssetList;

/**
 * Implementation of a specific tab in a workspace. Tab classes from the default
 * workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB
 * LABEL>ActionTab (to prevent duplication). Modify this class if tab filling
 * procedure has to be customized, extra asset list to be added, custom testdata
 * key to be defined, etc.
 * 
 * @category Generated
 */
public class DocumentsTab extends Tab {
	public DocumentsTab() {
		super(HomeSSMetaData.DocumentsTab.class);
		for (String list : assetList.getAssetNames()) {
			assetList.getAsset(list, AssetList.class).config.applyConfiguration("UnderwritingTab");
		}
	}

	@Override
	public Tab submitTab() {
		buttonNext.click();
		return this;
	}
	
	public AssetList getDocumentsToBindAssetList() {
    	return getAssetList().getAsset(HomeSSMetaData.DocumentsTab.DOCUMENTS_TO_BIND.getLabel(), AssetList.class);
	}
}
