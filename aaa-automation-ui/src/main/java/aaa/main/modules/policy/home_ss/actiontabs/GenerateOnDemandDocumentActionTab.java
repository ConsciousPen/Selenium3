/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class GenerateOnDemandDocumentActionTab extends ActionTab {
	
	public static Table tableOnDemandDocuments = new Table(By.xpath("//div[@id='policyDataGatherForm:componentView_AAAHODocGen_body']/div[1]//table")); 
	public static Button btnGenerateDocuments = new Button(By.id("policyDataGatherForm:generateDocLink"));
	public static Button btnCancel = new Button(By.id("policyDataGatherForm:adhocCancel"));
	public static Button btnPreviewDocuments = new Button(By.id("policyDataGatherForm:previewDocLink"));
	
    public GenerateOnDemandDocumentActionTab() {
        super(HomeSSMetaData.GenerateOnDemandDocumentActionTab.class);
    }
    
    public void generateDocuments(){
    	btnGenerateDocuments.click();
    }
    
    
}
