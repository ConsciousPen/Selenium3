/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissiongroup;

import aaa.admin.modules.IAdmin;
import aaa.admin.modules.commission.commissiongroup.CommissionGroupActions.DeleteCommissionGroup;
import aaa.admin.modules.commission.commissiongroup.CommissionGroupActions.EditCommissionGroup;
import aaa.admin.modules.commission.commissiongroup.CommissionGroupActions.ExpireCommissionGroup;
import aaa.common.Workspace;
import toolkit.datax.TestData;

public interface ICommissionGroup extends IAdmin {

    Workspace getDefaultView();

    void create(TestData td);

    void initiate();

    EditCommissionGroup edit();

    ExpireCommissionGroup expire();

    DeleteCommissionGroup delete();
}
