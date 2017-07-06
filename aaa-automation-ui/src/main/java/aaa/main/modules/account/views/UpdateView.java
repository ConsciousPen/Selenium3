/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.account.views;

import aaa.common.Workspace;
import aaa.main.modules.account.actiontabs.AcctInfoTab;
import aaa.main.modules.account.actiontabs.AffinityGroupsTab;
import aaa.main.modules.account.actiontabs.DesignatedContactsTab;

public class UpdateView extends Workspace {
    public UpdateView() {
        registerTab(AcctInfoTab.class);
        registerTab(DesignatedContactsTab.class);
        registerTab(AffinityGroupsTab.class);
    }
}
