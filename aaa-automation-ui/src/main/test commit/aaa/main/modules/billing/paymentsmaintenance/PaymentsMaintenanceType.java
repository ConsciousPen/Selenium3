/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.billing.paymentsmaintenance;

public enum PaymentsMaintenanceType {
    PAYMENTS_MAINTENANCE("Payments Maintenance", new PaymentsMaintenance());

    private String name;
    private IPaymentsMaintenance paymentsMaintenance;

    PaymentsMaintenanceType(String name, IPaymentsMaintenance paymentsMaintenance) {
        this.name = name;
        this.paymentsMaintenance = paymentsMaintenance;
    }

    public IPaymentsMaintenance get() {
        return paymentsMaintenance;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return paymentsMaintenance.getClass().getSimpleName();
    }
}
