/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.billing.account.actiontabs;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.BillingAccountMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;

public class TransferPaymentActionTab extends ActionTab {
	private static final String ALLOCATED_AMOUNTS_BOXES_ID = "transferPaymentForm:policyAllocationResults:%s:amountInput";
    public static TextBox textBoxAllocatedAmount = new TextBox(By.id(String.format(ALLOCATED_AMOUNTS_BOXES_ID ,0)));
    public static Table tablePolicies = new Table(By.id("transferPaymentForm:policyAllocationResults"));

    public TransferPaymentActionTab() {
        super(BillingAccountMetaData.TransferPaymentActionTab.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        String allocatedAmountLabel = BillingAccountMetaData.TransferPaymentActionTab.ALLOCATED_AMOUNT.getLabel();
        List<String> allocatedAmounts = (List<String>) td.getValue(TestData.Type.LIST_STRING, getMetaKey(), allocatedAmountLabel);
        td.mask(TestData.makeKeyPath(getMetaKey(), allocatedAmountLabel));
        super.fillTab(td);

		List<Row> rows = tablePolicies.getRows();
		if (rows.size() > allocatedAmounts.size()) {
			for (int i = allocatedAmounts.size(); i < rows.size(); i++) {
				allocatedAmounts.add(StringUtils.EMPTY);
			}
		}
		for (int i = 0; i < rows.size(); i++) {
			Row row = rows.get(i);
			String allocatedAmount = allocatedAmounts.get(i);
			row.getCell(allocatedAmountLabel).controls.textBoxes.getFirst().setValue(allocatedAmount);

		}

        return this;
    }
}
