/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.defaulttabs;

import aaa.common.Tab;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.webdriver.controls.composite.assets.AssetList;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class MortgageesTab extends Tab {
    public MortgageesTab() {
        super(HomeSSMetaData.MortgageesTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }
    
    public AssetList getMortgageeInfoAssetList() {
    	return getAssetList().getControl(HomeSSMetaData.MortgageesTab.MORTGAGEE_INFORMATION.getLabel(), AssetList.class);
	}
    public AssetList getLegalPropetyAddressAssetList() {
    	return getAssetList().getControl(HomeSSMetaData.MortgageesTab.LEGAL_PROPERTY_ADDRESS.getLabel(), AssetList.class);
	}
    public AssetList getAdditionalInsuredAssetList() {
    	return getAssetList().getControl(HomeSSMetaData.MortgageesTab.ADDITIONAL_INSURED.getLabel(), AssetList.class);
	}
    public AssetList getAdditionalInterestAssetList() {
    	return getAssetList().getControl(HomeSSMetaData.MortgageesTab.ADDITIONAL_INTEREST.getLabel(), AssetList.class);
	}
    public AssetList getThirdPartyDesigneeAssetList() {
    	return getAssetList().getControl(HomeSSMetaData.MortgageesTab.THIRD_PARTY_DESIGNEE.getLabel(), AssetList.class);
	}
}
