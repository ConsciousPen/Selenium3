/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.account.actiontabs;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.AccountMetaData;

public class AffinityGroupsTab extends ActionTab {
    public AffinityGroupsTab() {
        super(AccountMetaData.AffinityGroupsTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonDone.click();
        return this;
    }
}
