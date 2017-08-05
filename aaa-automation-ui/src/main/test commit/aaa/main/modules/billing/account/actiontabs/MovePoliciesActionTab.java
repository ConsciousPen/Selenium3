/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.billing.account.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.BillingAccountMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.composite.table.Table;

public class MovePoliciesActionTab extends ActionTab {
    public static Table tablePolicies = new Table(By.id("policyMoveForm:policyResults"));

    public MovePoliciesActionTab() {
        super(BillingAccountMetaData.MovePoliciesActionTab.class);
    }

    @Override
    public Tab fillTab(TestData td) {

        String policiesLabel = BillingAccountMetaData.MovePoliciesActionTab.POLICIES.getLabel();

        for (String policyNyumber : td.getList(getMetaKey(), policiesLabel)) {
            tablePolicies.getRow(PolicyConstants.PoliciesTable.POLICY, policyNyumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
        }

        return super.fillTab(td.mask(TestData.makeKeyPath(getMetaKey(), policiesLabel)));
    }
}
