/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.views;

import aaa.common.Workspace;
import aaa.main.modules.customer.actiontabs.ViewHistoryActionTab;
import aaa.main.modules.customer.defaulttabs.GeneralTab;

public class ViewHistoryView extends Workspace {
    public ViewHistoryView() {
        registerTab(ViewHistoryActionTab.class);
        registerTab(GeneralTab.class);
    }
}
