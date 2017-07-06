/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.views;

import aaa.common.Workspace;
import aaa.main.modules.customer.defaulttabs.BusinessEntityTab;
import aaa.main.modules.customer.defaulttabs.CustomerTypeTab;
import aaa.main.modules.customer.defaulttabs.DivisionsTab;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.main.modules.customer.defaulttabs.RelationshipTab;

public class DefaultView extends Workspace {
    public DefaultView() {
        registerTab(CustomerTypeTab.class);
        registerTab(GeneralTab.class);
        registerTab(BusinessEntityTab.class);
        registerTab(DivisionsTab.class);
        registerTab(RelationshipTab.class);
    }
}
