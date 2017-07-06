/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissionreferral.views;

import aaa.admin.modules.commission.commissionreferral.defaulttabs.AddCommissionReferralTab;
import aaa.common.Workspace;

public class DefaultView extends Workspace {

    public DefaultView() {
        super();
        registerTab(AddCommissionReferralTab.class);
    }
}
