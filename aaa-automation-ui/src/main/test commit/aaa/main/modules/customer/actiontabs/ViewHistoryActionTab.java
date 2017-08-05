/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.CustomerMetaData;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class ViewHistoryActionTab extends ActionTab {
	public static Link linkHistoryPanel = new Link(By.xpath("//div[@id='customerHistoryTogglePanel_0:header']"));

	public static Table tableGeneralInfo = new Table(By.id("indGeneralInfoCompareTable"));

	public ViewHistoryActionTab() {
		super(CustomerMetaData.ViewHistoryActionTab.class);
		assetList = new AssetList(By.xpath("//*"), metaDataClass);
	}

	@Override
	public Tab submitTab() {
		Page.dialogConfirmation.confirm();
		return this;
	}
}
