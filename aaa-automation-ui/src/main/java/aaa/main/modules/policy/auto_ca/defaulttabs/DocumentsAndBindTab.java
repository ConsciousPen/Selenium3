/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ca.defaulttabs;

import org.openqa.selenium.By;

import aaa.common.Tab;
import aaa.common.components.Dialog;
import aaa.main.metadata.policy.AutoCaMetaData;
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
public class DocumentsAndBindTab extends Tab {
	public DocumentsAndBindTab() {
		super(AutoCaMetaData.DocumentsAndBindTab.class);
	}

	public Button btnPurchase = new Button(By.id("policyDataGatherForm:moveToBilling_footer"), Waiters.AJAX);
	public Dialog confirmPurchase = new Dialog("//div[@id='policyDataGatherForm:confirmPurchaseDialog_container']");

	@Override
	public Tab submitTab() {
		btnPurchase.click();
		confirmPurchase.confirm();
		return this;
	}
}
