/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.abstract_tabs.CommonErrorTab;
import aaa.toolkit.webdriver.customcontrols.FillableErrorTable;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class ErrorTab extends CommonErrorTab {

	public Table tableErrors = new Table(By.xpath(".//form[@id='errorsForm']//table"));
	public Table tableBaseErrors = new Table(By.xpath(".//form[@id='errorsForm']//table[2]"));
	public Table tableTabFormErrors = new Table(By.xpath(".//div[@id='contents']//table//table"));

	public ErrorTab() {
		super(AutoSSMetaData.ErrorTab.class);
	}

	@Override
	public Tab submitTab() {
		buttonOverride.click();
		new DocumentsAndBindTab().submitTab();
		return this;
	}

	@Override
	public FillableErrorTable getErrorsControl() {
		return getAssetList().getAsset(AutoSSMetaData.ErrorTab.ERROR_OVERRIDE);
	}
}
