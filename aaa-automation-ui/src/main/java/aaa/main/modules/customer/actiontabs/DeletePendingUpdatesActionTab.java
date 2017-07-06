/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.actiontabs;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.CustomerMetaData;

public class DeletePendingUpdatesActionTab extends ActionTab {
    public DeletePendingUpdatesActionTab() {
        super(CustomerMetaData.DeletePendingUpdatesActionTab.class);
    }

    @Override
    public Tab submitTab() {
        Page.dialogConfirmation.confirm();
        return this;
    }
}
