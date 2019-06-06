/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ca.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.main.metadata.policy.AutoCaMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 *
 * @category Generated
 */
public class DriverActivityReportsTab extends Tab {
	private static final Object lock = new Object();
	public static Table tableCLUEReports = new Table(By.id("policyDataGatherForm:extLossReports"));
	public static Table tableMVRReports = new Table(By.id("policyDataGatherForm:mvrReports"));
	public static Table tableInternalClaim = new Table(By.id("policyDataGatherForm:claimsReports"));

	public DriverActivityReportsTab() {
		super(AutoCaMetaData.DriverActivityReportsTab.class);
	}

	@Override
	public Tab submitTab() {
		buttonNext.click();
		return this;
	}

	@Override
	public Tab fillTab(TestData td) {
		synchronized (lock) {
			super.fillTab(td);
		}
		return this;
	}
}
