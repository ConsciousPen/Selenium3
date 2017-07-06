/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.account.views;

import aaa.common.Workspace;
import aaa.main.modules.account.actiontabs.MoveCustomerConfirmationActionTab;
import aaa.main.modules.account.actiontabs.MoveCustomerSearchActionTab;

public class MoveCustomerView extends Workspace {
    public MoveCustomerView() {
        registerTab(MoveCustomerSearchActionTab.class);
        registerTab(MoveCustomerConfirmationActionTab.class);
    }
}
