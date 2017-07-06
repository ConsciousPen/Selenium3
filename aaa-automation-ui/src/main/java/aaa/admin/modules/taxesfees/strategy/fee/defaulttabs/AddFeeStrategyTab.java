/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.taxesfees.strategy.fee.defaulttabs;

import org.openqa.selenium.By;

import aaa.admin.metadata.taxesfees.TaxesFeesMetaData;
import aaa.common.DefaultTab;
import aaa.common.Tab;
import toolkit.webdriver.controls.composite.table.Table;

public class AddFeeStrategyTab extends DefaultTab {

    public static Table tableTaxRules = new Table(By.id("taxStrategyForm:body_ruleResults"));

    public AddFeeStrategyTab() {
        super(TaxesFeesMetaData.AddFeeStrategyTab.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
