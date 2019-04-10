/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.toolkit.webdriver.customcontrols.ActivityInformationMultiAssetList;
import aaa.toolkit.webdriver.customcontrols.AdvancedTable;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace. Tab classes from the default
 * workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB
 * LABEL>ActionTab (to prevent duplication). Modify this class if tab filling
 * procedure has to be customized, extra asset list to be added, custom testdata
 * key to be defined, etc.
 *
 * @category Generated
 */
public class DriverTab extends Tab {

	public static AdvancedTable tableDriverList = new AdvancedTable(By.id("policyDataGatherForm:dataGatherView_ListDriver"));
	public static AdvancedTable tableActivityInformationList = new AdvancedTable(By.id("policyDataGatherForm:dataGatherView_ListDrivingRecord"));

	public DriverTab() {
		super(AutoSSMetaData.DriverTab.class);
		assetList = new MultiInstanceAfterAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {
			@Override
			protected void addSection(int index, int size) {
				((Button) getAssetCollection().get(AutoSSMetaData.DriverTab.ADD_DRIVER.getLabel())).click();
			}

			@Override
			protected boolean sectionExists(int index) {
				return tableDriverList.getRow(index + 1).isPresent();
			}
		};
	}

	public ActivityInformationMultiAssetList getActivityInformationAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel(), ActivityInformationMultiAssetList.class);
	}

	@Override
	public Tab submitTab() {
		buttonNext.click();
		return this;
	}

	public static void viewDriver(int index) {
		tableDriverList.selectRow(index);
	}
	
	public void removeDriver(int index) {
		if (tableDriverList.isPresent() && tableDriverList.getRow(index).isPresent()) {
			tableDriverList.getRow(index).getCell(5).controls.links.get("Remove").click(Waiters.AJAX);
			Page.dialogConfirmation.confirm();
		}
	}
}
