/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.views;

import aaa.common.Workspace;
import aaa.main.modules.customer.actiontabs.OpportunityActionTab;

public class OpportunityView extends Workspace {
    public OpportunityView() {
        registerTab(OpportunityActionTab.class);
    }
}
