/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.billing.account.views;

import aaa.common.Workspace;
import aaa.main.modules.billing.account.actiontabs.TransferPaymentBenefitsActionTab;

public class TransferPaymentBenefitsView extends Workspace {
    public TransferPaymentBenefitsView() {
        super();
        registerTab(TransferPaymentBenefitsActionTab.class);
    }
}
