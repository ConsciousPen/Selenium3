/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.product.productfactory.policy.defaulttabs;

import static toolkit.verification.CustomAssertions.assertThat;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFButton;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFCheckBox;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFStaticElement;
import aaa.admin.metadata.product.ProductMetaData;
import aaa.admin.pages.product.CommonProductFactoryPage;
import aaa.common.Tab;
import toolkit.datax.TestData;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.ElementWaiter;
import toolkit.webdriver.controls.waiters.Waiters;

public class RulesTab extends PFDefaultTab {
    final int WAIT_FOR_ELEMENT_TIMEOUT = 60000;
    AssetList assetListSearch;
    AssetList assetListRuleProperties;

    public static PFStaticElement frame = new PFStaticElement(By.xpath("//iframe[@class='fullframe has-drawer-left-always-open']"));
    public static PFStaticElement sectionAutocomplete = new PFStaticElement(By.xpath("//ul[@class='md-autocomplete-suggestions']"));

    public static Table tableSearchResults = new Table(By.xpath("//table[@st-table='rulesCtrl.rules']")).applyConfiguration("RuleSearchResult");

    public static PFButton buttonAddNewRule = new PFButton(By.id("add-rule"));
    public static PFButton buttonDeleteRule = new PFButton(By.id("delete-rules"));
    public static PFButton buttonDeleteConfirm = new PFButton(By.className("md-confirm-button"));
    public static PFCheckBox chbSelectRule = new PFCheckBox(By.xpath("//table[@st-table='rulesCtrl.rules']//tbody//md-checkbox"));
    public static PFButton buttonEditRule = new PFButton(By.xpath("//button[@title='Open']"));
    public static PFButton buttonSaveChanges = new PFButton(By.id("save"));
    public static PFButton buttonSearchRule = new PFButton(By.xpath("//button[@class='md-raised md-button md-ink-ripple']"));

    public final Verify verify = this.new Verify();

    public RulesTab() {
        super(ProductMetaData.RulesTab.class);
        assetListSearch = new AssetList(By.xpath("//*"), ProductMetaData.RulesTab.RuleSearch.class);
        assetListRuleProperties = new AssetList(By.xpath("//*"), ProductMetaData.RulesTab.RuleProperties.class);
    }

    @Override
    public Tab fillTab(TestData td) {

        if (td.containsKey(getMetaKey()) && !td.getTestData(getMetaKey()).getKeys().isEmpty()) {
            BrowserController.get().driver().switchTo().frame(frame.getWebElement());

            for (TestData testData : td.getTestDataList(getMetaKey())) {
                switch (testData.getValue("Action")) {
                    case "Add":
                        addRule(testData);
                        break;
                    case "Delete":
                        deleteRule(testData);
                        break;
                    case "Edit":
                        editRule(testData);
                        break;
                    default:
                        break;
                }
            }

            BrowserController.get().driver().switchTo().defaultContent();
        }
        return this;
    }

    private void addRule(TestData testData) {
        buttonAddNewRule.waitForAccessible(WAIT_FOR_ELEMENT_TIMEOUT);
        buttonAddNewRule.click(Waiters.DEFAULT.then(new ElementWaiter(buttonSaveChanges)));
        assetListRuleProperties.fill(testData);
        new PFButton(By.id("save")).click(Waiters.DEFAULT.then(new ElementWaiter(buttonAddNewRule)));
    }

    private void deleteRule(TestData testData) {
        search(testData);
        chbSelectRule.setValue(true);
        buttonDeleteRule.click();
        buttonDeleteConfirm.click();
    }

    private void editRule(TestData testData) {
        search(testData);
        tableSearchResults.getRow(1).getCell(2).click();
        buttonEditRule.click();
        assetListRuleProperties.fill(testData);
        buttonSaveChanges.click();
    }

    public void search(TestData testData) {
        assetListSearch.fill(testData);
        if (sectionAutocomplete.isPresent() && sectionAutocomplete.isVisible()) {
            sectionAutocomplete.getWebElement().sendKeys(Keys.ESCAPE);
        }
        buttonSearchRule.click();
    }

    @Override
    public Tab submitTab() {
        CommonProductFactoryPage.activateNavigation();
        if (linkNextTab.isPresent()) {
            linkNextTab.click();
        }
        return this;
    }

    public class Verify {
        /**
         * Rule should be uniquely determined by testData.
         */
        public void ruleExists(TestData testData, boolean exist) {
            BrowserController.get().driver().switchTo().frame(frame.getWebElement());
            search(testData);
            assertThat(tableSearchResults.getRowsCount() == 1).as("Rule is not exist as expected.").isEqualTo(exist);
            BrowserController.get().driver().switchTo().defaultContent();
        }

        /**
         * Rule should be uniquely determined by testData.
         */
        public void ruleStatus(TestData testData, boolean enabled) {
            BrowserController.get().driver().switchTo().frame(frame.getWebElement());
            search(testData);
            assertThat(!tableSearchResults.getRow(1).getAttribute("class").contains("rule-disabled")).as("Rule is not enabled as expected.").isEqualTo(enabled);
            BrowserController.get().driver().switchTo().defaultContent();
        }
    }

}
