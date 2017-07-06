/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.pages.commission;

import org.openqa.selenium.By;

import aaa.admin.metadata.commission.CommissionMetaData;
import aaa.admin.pages.AdminPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class CommissionTemplatePage extends AdminPage {
    public static AssetList assetListSearchForm = new AssetList(By.id("templateForm"), CommissionMetaData.SearchByField.class);
    public static Button buttonSearch = new Button(By.id("templateForm:searchBtn_footer"));
    public static Button buttonAddNewCommissionTemplate = new Button(By.id("templateForm:showAddTemplatePopup"));
    public static Button buttonOK = new Button(By.xpath("//input[contains(@id, 'addTemplateDialogForm_groupBenefits') and @value = 'OK']"));
    public static ComboBox comboboxCommissionTemplateType = new ComboBox(By.id("templateForm:templateTypeForAdd"));
    public static Table tableCommissionTemplate = new Table(By.xpath("//div[@id='templateForm:searchResultTable']//table[@class='table']"));

    public static void search(TestData td) {
        assetListSearchForm.fill(td);
        buttonSearch.click();
    }
}
