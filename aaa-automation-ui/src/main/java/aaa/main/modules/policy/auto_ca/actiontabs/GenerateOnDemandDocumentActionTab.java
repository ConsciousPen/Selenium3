/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ca.actiontabs;

import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.abstract_tabs.CommonDocumentActionTab;
import aaa.toolkit.webdriver.customcontrols.FillableDocumentsTable;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 *
 * @category Generated
 */
public class GenerateOnDemandDocumentActionTab extends CommonDocumentActionTab {
	public GenerateOnDemandDocumentActionTab() {
		super(AutoCaMetaData.GenerateOnDemandDocumentActionTab.class);
	}

	@Override
	public FillableDocumentsTable getDocumentsControl() {
		return getAssetList().getAsset(AutoCaMetaData.GenerateOnDemandDocumentActionTab.ON_DEMAND_DOCUMENTS);
	}
}
