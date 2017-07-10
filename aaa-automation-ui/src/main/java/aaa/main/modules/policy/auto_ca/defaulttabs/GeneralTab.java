/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ca.defaulttabs;

import aaa.common.Tab;
import aaa.main.metadata.policy.AutoCaMetaData;
import toolkit.webdriver.controls.composite.assets.AssetList;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class GeneralTab extends Tab {
    public GeneralTab() {
        super(AutoCaMetaData.GeneralTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }
    
    public AssetList getNamedInsuredInfoAssetList() {
    	return getAssetList().getControl(AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), AssetList.class);
	}
    public AssetList getValidateAddressDialogAssetList() {
    	return getAssetList().getControl(AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), AssetList.class)
    			.getControl(AutoCaMetaData.GeneralTab.NamedInsuredInformation.VALIDATE_ADDRESS_DIALOG.getLabel(), AssetList.class);
	}
    public AssetList getAAAProductOwnedAssetList() {
    	return getAssetList().getControl(AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AssetList.class);
	}
    public AssetList getCurrentCarrierInfoAssetList() {
    	return getAssetList().getControl(AutoCaMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(), AssetList.class);
	}
    public AssetList getPolicyInfoAssetList() {
    	return getAssetList().getControl(AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AssetList.class);
	}
    public AssetList getThirdPartyDesigneeInfoAssetList() {
    	return getAssetList().getControl(AutoCaMetaData.GeneralTab.THIRD_PARTY_DESIGNEE_INFORMATION.getLabel(), AssetList.class);
	}
}
