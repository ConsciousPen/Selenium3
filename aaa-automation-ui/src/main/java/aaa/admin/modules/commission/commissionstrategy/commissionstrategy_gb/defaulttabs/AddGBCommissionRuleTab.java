/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissionstrategy.commissionstrategy_gb.defaulttabs;

import org.openqa.selenium.By;
import aaa.admin.metadata.commission.CommissionMetaData.AddGBCommissionRule;
import aaa.common.DefaultTab;
import aaa.common.Tab;
import aaa.common.pages.Page;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

public class AddGBCommissionRuleTab extends DefaultTab {
    public static final Button buttonAddPolicyYear = new Button(By.xpath("//button[contains(@id, 'addBandBtn')]"));
    public static final Button buttonOK = new Button(By.xpath("//input[contains(@id, 'addRuleDialogForm_groupBenefits') and @value = 'OK']"));
    public static final ComboBox comboBoxCommissionRuleType = new ComboBox(By.id("strategyTopForm:ruleStructure"));
    public static final Button buttonAddNewCommissionRule = new Button(By.id("strategyTopForm:showAddRulePopup"));

    private final MultiAssetList assetListPolicyYear = new MultiAssetList(By.xpath(Page.DEFAULT_DIALOG_LOCATOR), AddGBCommissionRule.AddCommissionRule.PolicyYear.class) {
        @Override
        protected void addSection(int index, int size) {
            if (index > 0)
                buttonAddPolicyYear.click();
        }

        @Override
        protected void selectSection(int index) {}
    };

    public AddGBCommissionRuleTab() {
        super(AddGBCommissionRule.AddCommissionRule.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {
            @Override
            protected void addSection(int index, int size) {
                comboBoxCommissionRuleType.setValue(td.getTestDataList(metaDataClass.getSimpleName()).get(index).getValue(AddGBCommissionRule.COMMISSION_TYPE.getLabel()));
                buttonAddNewCommissionRule.click();
            }

            @Override
            protected void selectSection(int index) {}

            @Override
            protected void setSectionValue(int index, TestData value) {
                super.setSectionValue(index, value);
                assetListPolicyYear.fill(value);
                buttonOK.click();
            }
        };
        return super.fillTab(td);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
