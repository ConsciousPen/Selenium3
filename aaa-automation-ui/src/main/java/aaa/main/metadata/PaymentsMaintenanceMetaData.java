/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.metadata;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.controls.AllocationAmount;
import com.exigen.ipb.etcsa.controls.dialog.DialogMultiSelector;
import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialog;

import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public class PaymentsMaintenanceMetaData {

    public static final class AddBulkPaymentActionTab extends MetaData {
        public static final AttributeDescriptor BULK_AMOUNT = declare("Bulk Amount", TextBox.class);
        public static final AttributeDescriptor BULK_REFERENCE = declare("Bulk Reference #", TextBox.class);
        public static final AttributeDescriptor ALLOCATION = declare("Allocation", DialogMultiSelector.class,
                AllocationMultiSelector.class);
        public static final AttributeDescriptor ALLOCATED_AMOUNT = declare("Allocated Amount", AllocationAmount.class);

        public static final class AllocationMultiSelector extends MetaData {
            public static final AttributeDescriptor BILLING_ACCOUNT_NUMBER = declare("Billing Account #", TextBox.class);
            public static final AttributeDescriptor POLICY_NUMBER = declare("Policy #", TextBox.class, Waiters.AJAX(30000));
            public static final AttributeDescriptor BILLING_ACCOUNT_NAME_TYPE = declare("Billing Account Name Type", ComboBox.class);
            public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
            public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
            public static final AttributeDescriptor MORTGAGEE = declare("Mortgagee", TextBox.class);
            public static final AttributeDescriptor AGENCY = declare("Agency", TextBox.class);
            public static final AttributeDescriptor AGENCY_CODE = declare("Agency Code", TextBox.class);

            public static final AttributeDescriptor BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class,
                    Waiters.DEFAULT, false, By.id("bulkPaymentForm:searchForAllocationLink"));
        }
    }

    public static final class AddSuspenseActionTab extends MetaData {
        public static final AttributeDescriptor SUSPENSE_AMOUNT = declare("Suspense Amount", TextBox.class);
        public static final AttributeDescriptor SUSPENSE_REFERENCE = declare("Suspense Reference #", TextBox.class);
        public static final AttributeDescriptor PAYMENT_DESIGNATION = declare("Payment Designation", ComboBox.class);
    }

    public static final class AddPaymentBatchActionTab extends MetaData {
        public static final AttributeDescriptor NUMBER_OF_PAYMENTS = declare("# of Payments", TextBox.class);
        public static final AttributeDescriptor TOTAL_AMOUNT = declare("Total Amount", TextBox.class);
        public static final AttributeDescriptor BATCH_REFERENCE = declare("Batch Reference #", TextBox.class);
        public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
    }

    public static final class SearchSuspenseActionTab extends MetaData {
        public static final AttributeDescriptor SUSPENSE_REFERENCE = declare("Suspense Reference #", TextBox.class);
        public static final AttributeDescriptor PAYMENT_DESIGNATION = declare("Payment Designation", ComboBox.class);
        public static final AttributeDescriptor BILLING_ACCOUNT = declare("Billing Account", TextBox.class);
    }

    public static final class ClearSuspenseActionTab extends MetaData {
        public static final AttributeDescriptor ALLOCATION = declare("Allocation", DialogMultiSelector.class,
                AllocationMultiSelector.class);

        public static final class AllocationMultiSelector extends MetaData {
            public static final AttributeDescriptor BILLING_ACCOUNT_NUMBER = declare("Billing Account #", TextBox.class);
            public static final AttributeDescriptor POLICY_NUMBER = declare("Policy #", TextBox.class);
            public static final AttributeDescriptor BILLING_ACCOUNT_NAME_TYPE = declare("Billing Account Name Type", ComboBox.class);
            public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
            public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
            public static final AttributeDescriptor MORTGAGEE = declare("Mortgagee", TextBox.class);
            public static final AttributeDescriptor AGENCY = declare("Agency", TextBox.class);
            public static final AttributeDescriptor AGENCY_CODE = declare("Agency Code", TextBox.class);

            public static final AttributeDescriptor BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class,
                    Waiters.SLEEP(3000), false, By.id("suspenseForm:showPolicySearchPopupLnk"));
        }
    }
}
