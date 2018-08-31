/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class PropertyInfoTab extends Tab {

	public Table tblClaimsList = new Table(By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListAAAHOLossInfo']//table"));

	public PropertyInfoTab() {
		super(HomeSSMetaData.PropertyInfoTab.class);
	}

	@Override
	public Tab submitTab() {
		buttonNext.click();
		return this;
	}

	public AssetList getDwellingAddressAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(), AssetList.class);
	}

	public AssetList getAdditionalAddressAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ADDITIONAL_ADDRESS.getLabel(), AssetList.class);
	}

	public AssetList getPublicProtectionClassAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel(), AssetList.class);
	}

	public AssetList getFireReportAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.FIRE_REPORT.getLabel(), AssetList.class);
	}

	public AssetList getPropertyValueAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PROPERTY_VALUE.getLabel(), AssetList.class);
	}

	public AssetList getConstructionAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(), AssetList.class);
	}

	public AssetList getAdditionalQuestionsAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ADDITIONAL_QUESTIONS.getLabel(), AssetList.class);
	}

	public AssetList getInteriorAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.INTERIOR.getLabel(), AssetList.class);
	}

	public AssetList getDetachedStructuresAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.DETACHED_STRUCTURES.getLabel(), AssetList.class);
	}

	public AssetList getFireProtectiveDDAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.FIRE_PROTECTIVE_DD.getLabel(), AssetList.class);
	}

	public AssetList getTheftProtectiveTPDDAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.THEFT_PROTECTIVE_DD.getLabel(), AssetList.class);
	}

	public AssetList getHomeRenovationAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.HOME_RENOVATION.getLabel(), AssetList.class);
	}

	public AssetList getPetsOrAnimalsAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PETS_OR_ANIMALS.getLabel(), AssetList.class);
	}

	public AssetList getStovesAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.STOVES.getLabel(), AssetList.class);
	}

	public AssetList getRecreationalEquipmentAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.RECREATIONAL_EQUIPMENT.getLabel(), AssetList.class);
	}

	public MultiInstanceAfterAssetList getClaimHistoryAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel(), MultiInstanceAfterAssetList.class);
	}

	public AssetList getRentalInformationAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.RENTAL_INFORMATION.getLabel(), AssetList.class);
	}
}
