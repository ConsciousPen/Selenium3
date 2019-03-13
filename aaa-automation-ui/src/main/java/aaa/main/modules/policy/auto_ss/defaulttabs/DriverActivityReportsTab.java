/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.main.metadata.policy.AutoSSMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class DriverActivityReportsTab extends Tab {
	private static final Object lock = new Object();
	public static Table tableCLUEReports = new Table(By.id("policyDataGatherForm:clueReports"));
	public static Table tableMVRReports = new Table(By.id("policyDataGatherForm:mvrReportsDataTable"));
	public static Table tableInternalClaim = new Table(By.id("policyDataGatherForm:claimsReports"));
	public static StaticElement resultMsgMVRReports = new StaticElement(By.xpath(".//div[@id='policyDataGatherForm:mvrReports']/following-sibling::table//span"));
	public static StaticElement resultMsgCLUEReports = new StaticElement(By.xpath("//table[@id='policyDataGatherForm:clueReports']/ancestor::div[2]/following-sibling::table//tr[2]"));
	
	public DriverActivityReportsTab() {
		super(AutoSSMetaData.DriverActivityReportsTab.class);
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
