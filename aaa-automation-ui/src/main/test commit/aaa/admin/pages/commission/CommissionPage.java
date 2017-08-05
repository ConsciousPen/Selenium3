/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.pages.commission;

import org.openqa.selenium.By;

import aaa.admin.metadata.commission.CommissionMetaData;
import aaa.admin.pages.AdminPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class CommissionPage extends AdminPage {
    public static AssetList assetListSearchForm = new AssetList(By.xpath("//form[contains(@id, 'SearchForm')]"), CommissionMetaData.SearchByField.class);

    public static Button buttonAddNewCommissionGroup = new Button(By.id("groupSearchForm:addGroupBtn_footer"));
    public static Button buttonAddNewCommissionStrategy = new Button(By.id("strategySearchForm:addStrategyBtn_footer"));
    public static Button buttonAddNewCommissionRule = new Button(By.id("strategyTopForm:addRuleBtn"));
    public static Button buttonAddCommissionBonus = new Button(By.id("bonusSearchForm:addBonusBtn_footer"));
    public static Button buttonAddCommissionReferral = new Button(By.id("referralSearchForm:addReferralBtn_footer"));
    public static Button buttonAddBulkAdjustment = new Button(By.xpath("//.[@id='adjustmentSearchForm:addAdjustmentBtn' or @id='adjustmentSearchForm:addAdjustmentBtn_footer']"));
    public static Button buttonAddBulkAdjustmentRule = new Button(By.id("adjustmentTopForm:addRuleBtn"));
    public static Button buttonSaveCommissionStrategy = new Button(By.id("strategyTopForm:saveBtn_footer"));
    public static Button buttonSaveBulkAdjustment = new Button(By.xpath("//.[@id='adjustmentTopForm:saveBtn' or @id='adjustmentTopForm:saveBtn_footer']"));
    public static Button buttonExpireCommissionGroup = new Button(By.id("groupSearchForm:expireGroupBtn"));
    public static Button buttonExpireCommissionStrategy = new Button(By.id("strategySearchForm:expireStrategyBtn"));
    public static Button buttonExpireCommissionBonus = new Button(By.id("bonusSearchForm:expireBonusBtn"));
    public static Button buttonExpireCommissionReferral = new Button(By.id("referralSearchForm:expireReferralBtn"));

    public static Table tableCommissionGroup = new Table(By.id("groupSearchForm:body_groupSearchTable"));
    public static Table tableCommissionStrategy = new Table(By.id("strategySearchForm:body_strategySearchTable"));
    public static Table tableCommissionBonus = new Table(By.id("bonusSearchForm:body_bonusSearchTable"));
    public static Table tableCommissionReferral = new Table(By.id("referralSearchForm:body_referralSearchTable"));
    public static Table tableBulkAdjustment = new Table(By.id("adjustmentSearchForm:body_AdjustmentSearchTable"));

    public static void search(TestData td) {
        assetListSearchForm.fill(td);
        buttonSearch.click();
    }
}
