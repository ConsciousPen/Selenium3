/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.pages.workflow;

import org.openqa.selenium.By;

import aaa.admin.pages.workflow.ProcessManagementPage;

import aaa.admin.metadata.workflow.WorkFlowMetadata;
import aaa.admin.pages.AdminPage;
import aaa.common.components.Dialog;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class ProcessManagementPage extends AdminPage {

	public static AssetList assetListSearchForm = new AssetList(By.id("manualTaskDefinitionsFilterForm"), WorkFlowMetadata.TaskSearchByField.class);
	public static Button buttonAddManualTaskDefinition = new Button(By.id("processDefinitionsForm:createNewTaskDefinitionBtn"));
	public static Button buttonDeployProcessDefinition = new Button(By.id("processDefinitionsForm:deployProcessLinkID"));

	public static Button buttonSave = new Button(By.xpath("//*[@id='manualTaskDefinitionCommandForm:submitSave_footer' or @id='manualTaskDefinitionCommandForm:submitUpdate_footer']"));
	public static Button buttonSearch = new Button(By.id("manualTaskDefinitionsFilterForm:filterSearchBtn"));
	public static Table tableSearchResults = new Table(By.xpath("//div[@id='processDefinitionsForm:deployedProcDef']//table"));
	public static Dialog dialogDeployProcess = new Dialog("//div[@id='deployProcessDialog']");
	public static StaticElement uploadStatusMsg = new StaticElement(By.xpath(ProcessManagementPage.dialogDeployProcess.getLocator() + "//span[@class='ui-messages-info-summary']"));

	public static void search(TestData td) {
		assetListSearchForm.fill(td);
		buttonSearch.click();
	}
}
