/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.actiontabs;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.policy.AutoSSMetaData;
import toolkit.webdriver.controls.Button;

import static org.openqa.selenium.By.id;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class GenerateOnDemandDocumentActionTab extends ActionTab {
    public GenerateOnDemandDocumentActionTab() {
        super(AutoSSMetaData.GenerateOnDemandDocumentActionTab.class);
    }

    Button buttonOk = new Button(id("policyDataGatherForm:generateDocLink"));
    Button buttonCancel = new Button(id("policyDataGatherForm:adhocCancel"));
    Button buttonPreviewDocuments = new Button(id("policyDataGatherForm:previewDocLink"));

    @Override
    public Tab submitTab() {
        buttonOk.click();
        return this;
    }
}
