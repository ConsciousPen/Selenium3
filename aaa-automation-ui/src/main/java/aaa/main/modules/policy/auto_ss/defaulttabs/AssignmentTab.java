/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.defaulttabs;

import aaa.common.Tab;
import aaa.main.metadata.policy.AutoSSMetaData;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.Button;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class AssignmentTab extends Tab {
	public Button btnContinue = new Button(By.xpath("//input[@id='policyDataGatherForm:continueBtn_AAAAssignmentContinueAction_footer' or @id='policyDataGatherForm:nextInquiry_footer']"));
	public Button btnAssign = new Button(By.xpath("//input[@value='Assign']"));

	public AssignmentTab() {
		super(AutoSSMetaData.AssignmentTab.class);
	}

	@Override
	public Tab submitTab() {
		if (btnAssign.isPresent() && btnAssign.isVisible() && btnAssign.isEnabled()) {
			btnAssign.click();
		}
		if (btnContinue.isPresent() && btnContinue.isVisible()) {
			btnContinue.click();
		}
		return this;
	}
}
