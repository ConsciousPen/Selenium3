/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.taxesfees.strategy.tax.defaulttabs;

import java.util.Map;

import org.openqa.selenium.By;

import aaa.admin.metadata.taxesfees.TaxesFeesMetaData;
import aaa.common.DefaultTab;
import aaa.common.Tab;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.ListBox;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Table;

public class AddTaxStrategyTab extends DefaultTab {
    protected static StaticElement actionControlsParent = new StaticElement(By.xpath("//div[contains(@class, 'rf-pp-cntr')][parent::body]"));
    public static Button buttonCancel = new Button(By.id("taxStrategyForm:cancelBtnStrategy"));
    public static Button buttonSaveRule = new Button(By.id("taxStrategyForm:saveRuleBtn"));
    public static Button buttonDiscard = new Button(By.id("taxStrategyForm:clearRuleBtn"));
    public static Button buttonAddRule = new Button(By.id("taxStrategyForm:newBtnStrategy"));
    public static TextBox textBoxAmountPercent = new TextBox(By.id("taxStrategyForm:amountPercent"));
    public static Table tableTaxRules = new Table(By.id("taxStrategyForm:body_ruleResults"));

    public AddTaxStrategyTab() {
        super(TaxesFeesMetaData.AddFeeTaxStrategyTab.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        return super.fillTab(td);
    }

    public static class TaxesFeesAddTaxStrategyRulesGroupAssetList extends MultiAssetList {
        public TaxesFeesAddTaxStrategyRulesGroupAssetList(By locator, Class<? extends MetaData> metaDataClass) {
            super(locator, metaDataClass);
        }

        public TaxesFeesAddTaxStrategyRulesGroupAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
            super(parent, locator, metaDataClass);
        }

        @Override
        protected void selectSection(int index) {

        }

        @Override
        protected void addSection(int arg0, int arg1) {
            if (buttonAddRule.isPresent()) {
                buttonAddRule.click();
            }
        }

        @Override
        protected void setSectionValue(int index, TestData value) {
            for (Map.Entry<String, BaseElement<?, ?>> entry : getAssetCollection().entrySet()) {

                TestData tdTempRuleGroup = value.getTestData("Group Details");
                String keyName = TaxesFeesMetaData.AddFeeTaxStrategyTab.RulesGroup.RulesGroupDetails.APPLIES_TO_LEVEL.getLabel();
                String keyCover = TaxesFeesMetaData.AddFeeTaxStrategyTab.RulesGroup.RulesGroupDetails.APPLIES_TO_LEVEL_SELECT.getLabel();
                String keyGroup = TaxesFeesMetaData.AddFeeTaxStrategyTab.RulesGroup.GROUP_DETAILS.getLabel();
                if (value.getTestData(keyGroup).containsKey(keyCover)) {

                    new ComboBox(By.id("taxStrategyForm:ruleInfo_applyLevel")).setValue(tdTempRuleGroup.getValue(keyName));
                    new Button(By.id("taxStrategyForm:selectCoveragesBtn")).click();
                    new Button(actionControlsParent, By.xpath(".//input[@value='Search']")).click();
                    new ListBox(actionControlsParent, By.xpath(".//select[contains(@id,'available')]")).setValue(tdTempRuleGroup.getList(keyCover));
                    new Button(By.id("searchForm_coveragesSearch:add_coveragesSearch")).click();
                    new Button(actionControlsParent, By.xpath(".//*[contains(@value,'Update') or contains(text(),'Update')]")).click();

                    value.adjust(keyGroup, tdTempRuleGroup.mask(keyCover, keyName));
                }
                entry.getValue().fill(value);

                buttonSaveRule.click();
            }
        }
    }
}
