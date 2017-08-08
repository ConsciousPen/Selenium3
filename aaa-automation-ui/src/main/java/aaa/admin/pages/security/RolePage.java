/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.pages.security;

import org.openqa.selenium.By;

import aaa.admin.metadata.security.RoleMetaData;
import aaa.admin.pages.AdminPage;
import aaa.common.components.Dialog;
import aaa.main.enums.ActionConstants;
import aaa.admin.constants.AdminConstants;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class RolePage extends AdminPage {
	public static AssetList assetListSearchForm = new AssetList(By.xpath("//form[contains(@id, 'SearchForm')]"), RoleMetaData.SearchByField.class);

	public static Button buttonAddNewRole = new Button(By.id("roleSearchForm:add-role"));
	public static Button buttonReturn = new Button(By.id("roleForm:cancel_footer"));
	public static Table tableSecurityRole = new Table(By.id("roleSearchForm:roleSearch_roleDomain"));
	public static Table tableRolesSearchResult = new Table(By.id("roleSearchForm:body_rolesSearchResult"));
	public static Dialog dialogRemoveRoleConfirmation = new Dialog("//div[@id='deleteRoleConfirmDialogDialog_container']");
	public static StaticElement labelItemNotFound = new StaticElement(By.xpath("//form[@id='roleSearchForm']"));
	public static StaticElement rolesPrivileges = new StaticElement(By.xpath("//*[@id='roleForm:privileges']"));

	public static void search(TestData td) {
		assetListSearchForm.fill(td);
		buttonSearch.click();
	}

	public static void change(TestData td) {
		search(td);
		tableRolesSearchResult.getRow(1).getCell(AdminConstants.AdminRolesSearchResultTable.ACTION).controls.links.get(ActionConstants.CHANGE).click();
	}

	public static void inquiry(TestData td) {
		search(td);
		tableRolesSearchResult.getRow(1).getCell(AdminConstants.AdminRolesSearchResultTable.ROLE_NAME).controls.links.getFirst().click();
	}

	public static void delete(TestData td) {
		search(td);
		tableRolesSearchResult.getRow(1).getCell(AdminConstants.AdminRolesSearchResultTable.ACTION).controls.links.get(ActionConstants.DELETE).click();
		dialogRemoveRoleConfirmation.buttonYes.click();
	}

}
