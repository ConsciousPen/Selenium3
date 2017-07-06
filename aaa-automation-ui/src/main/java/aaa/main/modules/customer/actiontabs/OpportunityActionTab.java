/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.main.metadata.CustomerMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;

public class OpportunityActionTab extends ActionTab {
    public static Table tableOpportunity = new Table(By.id("opportunityForm:opportunityTable"));
    public static Button buttonCreateNewOpportunity = new Button(By.xpath("//a[.='Create New Opportunity']"));
    public static Button buttonAddOpportunity = new Button(By.id("editOpportunity:addProductButton"));

    public OpportunityActionTab() {
        super(CustomerMetaData.OpportunityActionTab.class);
    }
}
