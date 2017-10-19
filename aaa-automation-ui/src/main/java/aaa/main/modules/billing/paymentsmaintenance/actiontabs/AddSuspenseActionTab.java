/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.billing.paymentsmaintenance.actiontabs;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.PaymentsMaintenanceMetaData;

public class AddSuspenseActionTab extends ActionTab {
	public AddSuspenseActionTab() {
		super(PaymentsMaintenanceMetaData.AddSuspenseActionTab.class);
	}

	@Override
	public Tab submitTab() {
		buttonOk.click();
		return this;
	}
}
