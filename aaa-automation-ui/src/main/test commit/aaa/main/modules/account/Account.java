/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aaa.common.Workspace;
import aaa.main.modules.account.AccountActions.AddAffinityGroup;
import aaa.main.modules.account.AccountActions.AddCommunication;
import aaa.main.modules.account.AccountActions.AddCustomer;
import aaa.main.modules.account.AccountActions.AddDesignatedContact;
import aaa.main.modules.account.AccountActions.EliminateAffinityGroup;
import aaa.main.modules.account.AccountActions.EliminateCustomer;
import aaa.main.modules.account.AccountActions.EliminateDesignatedContact;
import aaa.main.modules.account.AccountActions.MoveCustomer;
import aaa.main.modules.account.AccountActions.MoveCustomerToNewAccount;
import aaa.main.modules.account.AccountActions.RemoveCommunication;
import aaa.main.modules.account.AccountActions.Update;
import aaa.main.modules.customer.views.DefaultView;

public class Account implements IAccount {

    protected static Logger log = LoggerFactory.getLogger(Account.class);

    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public AddCustomer addCustomer() {
        return new AccountActions.AddCustomer();
    }

    @Override
    public EliminateCustomer eliminateCustomer() {
        return new AccountActions.EliminateCustomer();
    }

    @Override
    public MoveCustomer moveCustomer() {
        return new AccountActions.MoveCustomer();
    }

    @Override
    public MoveCustomerToNewAccount moveCustomerToNewAccount() {
        return new AccountActions.MoveCustomerToNewAccount();
    }

    @Override
    public AddAffinityGroup addAffinityGroup() {
        return new AccountActions.AddAffinityGroup();
    }

    @Override
    public EliminateAffinityGroup eliminateAffinityGroup() {
        return new AccountActions.EliminateAffinityGroup();
    }

    @Override
    public AddCommunication addCommunication() {
        return new AccountActions.AddCommunication();
    }

    @Override
    public RemoveCommunication removeCommunication() {
        return new AccountActions.RemoveCommunication();
    }

    @Override
    public Update update() {
        return new AccountActions.Update();
    }

    @Override
    public AddDesignatedContact addDesignatedContact() {
        return new AccountActions.AddDesignatedContact();
    }

    @Override
    public EliminateDesignatedContact eliminateDesignatedContact() {
        return new AccountActions.EliminateDesignatedContact();
    }
}
