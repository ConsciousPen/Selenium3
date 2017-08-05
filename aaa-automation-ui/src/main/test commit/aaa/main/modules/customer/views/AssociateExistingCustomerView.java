/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.views;

import aaa.common.Workspace;
import aaa.main.modules.customer.actiontabs.AssociateExistingCustomerSearchActionTab;
import aaa.main.modules.customer.actiontabs.EmployeeInfoTab;
import aaa.main.modules.customer.actiontabs.SelectGroupRelationshipTypeActionTab;

public class AssociateExistingCustomerView extends Workspace {
    public AssociateExistingCustomerView() {
        registerTab(SelectGroupRelationshipTypeActionTab.class);
        registerTab(AssociateExistingCustomerSearchActionTab.class);
        registerTab(EmployeeInfoTab.class);
    }
}
