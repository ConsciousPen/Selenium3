/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.billing.account;

import aaa.common.Workspace;
import toolkit.datax.TestData;

public interface IBillingAccount {
    void create(TestData td);

    Workspace getDefaultView();

    BillingAccountActions.GenerateFutureStatement generateFutureStatement();

    BillingAccountActions.AcceptPayment acceptPayment();

    BillingAccountActions.TransferPayment transferPayment();

    BillingAccountActions.TransferPaymentBenefits transferPaymentBenefits();

    BillingAccountActions.OtherTransactions otherTransactions();

    BillingAccountActions.DiscardBill discardBill();

    BillingAccountActions.RegenerateBill regenerateBill();

    BillingAccountActions.DeclinePayment declinePayment();

    BillingAccountActions.MovePolicies movePolicies();

    BillingAccountActions.WaiveFee waiveFee();

    BillingAccountActions.Refund refund();

    BillingAccountActions.Update update();

    BillingAccountActions.AddHold addHold();

    BillingAccountActions.RemoveHold removeHold();

    BillingAccountActions.ViewModalPremium viewModalPremium();

    BillingAccountActions.UnallocatePayment unallocatePayment();
}
