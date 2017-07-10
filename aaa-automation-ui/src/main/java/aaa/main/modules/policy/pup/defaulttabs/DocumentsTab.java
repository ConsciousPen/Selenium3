/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.policy.pup.defaulttabs;

import aaa.common.Tab;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
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
		super(PersonalUmbrellaMetaData.DocumentsTab.class);
		for (String list : assetList.getAssetNames()) {
			assetList.getControl(list, AssetList.class).config.applyConfiguration("UnderwritingTab");
		}
	}

	@Override
	public Tab submitTab() {
		buttonNext.click();
		return this;
	}
	
	public AssetList getDocumentsForPrintingAssetList() {
    	return getAssetList().getControl(PersonalUmbrellaMetaData.DocumentsTab.DOCUMENTS_FOR_PRINTING.getLabel(), AssetList.class);
	}
	public AssetList getRequiredToBindAssetList() {
    	return getAssetList().getControl(PersonalUmbrellaMetaData.DocumentsTab.REQUIRED_TO_BIND.getLabel(), AssetList.class);
	}
	public AssetList getRequiredToIssueAssetList() {
    	return getAssetList().getControl(PersonalUmbrellaMetaData.DocumentsTab.REQUIRED_TO_ISSUE.getLabel(), AssetList.class);
	}
}
