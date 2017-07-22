/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.actiontabs;

import org.openqa.selenium.By;
import aaa.common.ActionTab;
import aaa.main.metadata.policy.AutoSSMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class UpdateRulesOverrideActionTab extends ActionTab {

	public static Table tblRulesList = new Table(By.id("errorsForm:msgList"));
	public static Button btnUpdateOverride = new Button(By.id("errorsForm:updateOverrideRules"));
	public static Button btnCancel = new Button(By.id("errorsForm:cancel"));

	public UpdateRulesOverrideActionTab() {
		super(AutoSSMetaData.UpdateRulesOverrideActionTab.class);
	}

	public void updateOverride() {
		btnUpdateOverride.click();
	}

}
