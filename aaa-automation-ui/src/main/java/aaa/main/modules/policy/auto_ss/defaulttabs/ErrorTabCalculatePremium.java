/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.defaulttabs;

import aaa.main.modules.policy.abstract_tabs.CommonErrorTab;
import aaa.common.Tab;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.toolkit.webdriver.customcontrols.FillableErrorTable;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class ErrorTabCalculatePremium extends CommonErrorTab {

	public ErrorTabCalculatePremium() {
		super(AutoSSMetaData.ErrorTabCalculatePremium.class);
	}

	@Override
	public Tab submitTab() {
		buttonOverride.click();
		new PremiumAndCoveragesTab().submitTab();
		return this;
	}

	@Override
	public FillableErrorTable getErrorsControl() {
		return getAssetList().getAsset(AutoSSMetaData.ErrorTabCalculatePremium.ERROR_OVERRIDE);
	}
}
