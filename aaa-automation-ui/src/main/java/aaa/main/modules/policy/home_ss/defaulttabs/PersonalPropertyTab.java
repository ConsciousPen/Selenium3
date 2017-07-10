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
public class PersonalPropertyTab extends Tab {
    public PersonalPropertyTab() {
        super(HomeSSMetaData.PersonalPropertyTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }
    
    public AssetList getJewelryAssetList() {
    	return getAssetList().getControl(HomeSSMetaData.PersonalPropertyTab.JEWELRY_TAB.getLabel(), AssetList.class);
	}
    public AssetList getFineArtsAssetList() {
    	return getAssetList().getControl(HomeSSMetaData.PersonalPropertyTab.FINE_ARTS_TAB.getLabel(), AssetList.class);
	}
    public AssetList getPortableElectronicEquipmentAssetList() {
    	return getAssetList().getControl(HomeSSMetaData.PersonalPropertyTab.PORTABLE_ELECTRONIC_TAB.getLabel(), AssetList.class);
	}
    public AssetList getSportsEquipmentAssetList() {
    	return getAssetList().getControl(HomeSSMetaData.PersonalPropertyTab.SPORTS_EQUIPMENT_TAB.getLabel(), AssetList.class);
	}
    public AssetList getSecuritiesAssetList() {
    	return getAssetList().getControl(HomeSSMetaData.PersonalPropertyTab.SEQURITIES_TAB.getLabel(), AssetList.class);
	}
    public AssetList getFursAssetList() {
    	return getAssetList().getControl(HomeSSMetaData.PersonalPropertyTab.FURS_TAB.getLabel(), AssetList.class);
	}
    public AssetList getFirearmsAssetList() {
    	return getAssetList().getControl(HomeSSMetaData.PersonalPropertyTab.FIREARMS_TAB.getLabel(), AssetList.class);
	}
    public AssetList getSilverwareAssetList() {
    	return getAssetList().getControl(HomeSSMetaData.PersonalPropertyTab.SILVERWARE_TAB.getLabel(), AssetList.class);
	}
}
