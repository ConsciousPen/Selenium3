/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.defaulttabs;

import aaa.toolkit.webdriver.customcontrols.AdvancedTable;
import org.openqa.selenium.By;

import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import toolkit.webdriver.controls.Button;
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
public class VehicleTab extends Tab {
	
	public static AdvancedTable tableVehicleList = new AdvancedTable(By.id("policyDataGatherForm:dataGatherView_ListVehicle"));
	public static AdvancedTable tableAdditionalInterestList = new AdvancedTable(By.id("policyDataGatherForm:dataGatherView_ListAAAAdditionalInterest"));
    public static Button buttonAddVehicle = new Button(By.xpath("//input[@id='policyDataGatherForm:addVehicle']"));

    public VehicleTab() {
		super(AutoSSMetaData.VehicleTab.class);

		assetList = new MultiInstanceAfterAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {
			@Override
			protected void addSection(int index, int size) {
				if (index > 0)
					((Button) getAssetCollection().get("Add Vehicle")).click();
			}
		};
	}

	public AssetList getOwnershipAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.VehicleTab.OWNERSHIP.getLabel(), AssetList.class);
	}

	public AssetList getAdditionalInterestInfoAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.VehicleTab.ADDITIONAL_INTEREST_INFORMATION.getLabel(), AssetList.class);
	}

	@Override
	public Tab submitTab() {
		buttonNext.click();
		return this;
	}
}
