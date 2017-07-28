/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.billing.paymentsmaintenance;

import aaa.main.modules.billing.paymentsmaintenance.PaymentsMaintenanceActions.AddBulkPayment;
import aaa.main.modules.billing.paymentsmaintenance.PaymentsMaintenanceActions.AddPaymentBatch;
import aaa.main.modules.billing.paymentsmaintenance.PaymentsMaintenanceActions.AddSuspense;
import aaa.main.modules.billing.paymentsmaintenance.PaymentsMaintenanceActions.ClearSuspense;

public class PaymentsMaintenance implements IPaymentsMaintenance {

    @Override
    public AddBulkPayment addBulkPayment() {
        return new PaymentsMaintenanceActions.AddBulkPayment();
    }

    @Override
    public AddSuspense addSuspense() {
        return new PaymentsMaintenanceActions.AddSuspense();
    }

    @Override
    public AddPaymentBatch addPaymentBatch() {
        return new PaymentsMaintenanceActions.AddPaymentBatch();
    }

    @Override
    public ClearSuspense clearSuspense() {
        return new PaymentsMaintenanceActions.ClearSuspense();
    }
}
