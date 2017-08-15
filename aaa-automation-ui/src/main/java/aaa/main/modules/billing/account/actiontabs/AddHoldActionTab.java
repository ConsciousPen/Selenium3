/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.billing.account.actiontabs;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;
import aaa.common.ActionTab;
import aaa.main.metadata.BillingAccountMetaData;

public class AddHoldActionTab extends ActionTab {

    public static Table tablePolicies = new Table(By.id("holdPoliciesForm:policyResults"));
    public static Table tableHoldsAndMoratoriums = new Table(By.id("holdPoliciesForm:holdsTable"));

    public static Button buttonAddUpdate = new Button(By.xpath("//input[(@value = 'Add/Update') and not(@class = 'hidden') and not(contains(@style,'none'))]"));

    public AddHoldActionTab() {
        super(BillingAccountMetaData.AddHoldActionTab.class);
    }
}
