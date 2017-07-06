/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.views;

import aaa.common.Workspace;
import aaa.main.modules.customer.actiontabs.EmployeeInfoTab;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.main.modules.customer.defaulttabs.RelationshipTab;

public class UpdateParticipantView extends Workspace {
    public UpdateParticipantView() {
        registerTab(GeneralTab.class);
        registerTab(RelationshipTab.class);
        registerTab(EmployeeInfoTab.class);
    }
}
