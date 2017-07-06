/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.agencyvendor;

import aaa.admin.modules.IAdmin;
import aaa.common.Workspace;
import toolkit.datax.TestData;

public interface IAgencyVendor extends IAdmin {

    Workspace getDefaultView();

    void create(TestData td);
    
    String createAgency(TestData td);

    void initiate();

    AgencyVendorActions.Update update();
}
