/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.billing.account;

import aaa.common.Workspace;
import aaa.main.modules.billing.account.BillingAccountActions.*;
import aaa.main.modules.billing.account.views.DefaultView;
import org.openqa.selenium.By;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.composite.table.Table;

public class BillingAccount implements IBillingAccount {
    public static Table tableInstallmentSavingInfo = new Table(By.id("updateForm:installmentFeeAmountSavedPanel"));
    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void create(TestData td) {
        getDefaultView().fill(td);
    }

    @Override
    public GenerateFutureStatement generateFutureStatement() {
        return new BillingAccountActions.GenerateFutureStatement();
    }

    @Override
    public AcceptPayment acceptPayment() {
        return new BillingAccountActions.AcceptPayment();
    }

    @Override
    public UnallocatePayment unallocatePayment() {
        return new BillingAccountActions.UnallocatePayment();
    }

    @Override
    public TransferPayment transferPayment() {
        return new BillingAccountActions.TransferPayment();
    }

    @Override
    public OtherTransactions otherTransactions() {
        return new BillingAccountActions.OtherTransactions();
    }

    @Override
    public DiscardBill discardBill() {
        return new BillingAccountActions.DiscardBill();
    }

    @Override
    public RegenerateBill regenerateBill() {
        return new BillingAccountActions.RegenerateBill();
    }

    @Override
    public DeclinePayment declinePayment() {
        return new BillingAccountActions.DeclinePayment();
    }

    @Override
    public MovePolicies movePolicies() {
        return new BillingAccountActions.MovePolicies();
    }

    @Override
    public WaiveFee waiveFee() {
        return new BillingAccountActions.WaiveFee();
    }

    @Override
    public Refund refund() {
        return new BillingAccountActions.Refund();
    }

    @Override
	public ApproveRefund approveRefund() {
		return new BillingAccountActions.ApproveRefund();
	}

	@Override
	public IssueRefund issueRefund() {
		// TODO Auto-generated method stub
		return new BillingAccountActions.IssueRefund();
	}
	
    @Override
    public Update update() {
        return new BillingAccountActions.Update();
    }

    @Override
    public AddHold addHold() {
        return new BillingAccountActions.AddHold();
    }

    @Override
    public RemoveHold removeHold() {
        return new BillingAccountActions.RemoveHold();
    }

    @Override
    public ViewModalPremium viewModalPremium() {
        return new BillingAccountActions.ViewModalPremium();
    }

    @Override
    public ChangePaymentPlan changePaymentPlan() {
        return new BillingAccountActions.ChangePaymentPlan();
    }

}
