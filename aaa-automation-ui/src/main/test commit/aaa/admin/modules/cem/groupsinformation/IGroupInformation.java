/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.cem.groupsinformation;

import aaa.admin.modules.IAdmin;
import aaa.common.Workspace;
import toolkit.datax.TestData;

public interface IGroupInformation extends IAdmin {

    Workspace getDefaultView();

    void create(TestData td);

    void initiate();

    GroupInformationActions.EditGroupsInformation editGroupsInformation();

    GroupInformationActions.DeleteGroupsInformation deleteGroupsInformation();
}
