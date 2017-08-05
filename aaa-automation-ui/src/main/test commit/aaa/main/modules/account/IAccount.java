/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.account;

import aaa.common.Workspace;

public interface IAccount {

    Workspace getDefaultView();

    AccountActions.AddCustomer addCustomer();

    AccountActions.EliminateCustomer eliminateCustomer();

    AccountActions.MoveCustomer moveCustomer();

    AccountActions.MoveCustomerToNewAccount moveCustomerToNewAccount();

    AccountActions.AddAffinityGroup addAffinityGroup();

    AccountActions.EliminateAffinityGroup eliminateAffinityGroup();

    AccountActions.AddCommunication addCommunication();

    AccountActions.RemoveCommunication removeCommunication();

    AccountActions.Update update();

    AccountActions.AddDesignatedContact addDesignatedContact();

    AccountActions.EliminateDesignatedContact eliminateDesignatedContact();
}
