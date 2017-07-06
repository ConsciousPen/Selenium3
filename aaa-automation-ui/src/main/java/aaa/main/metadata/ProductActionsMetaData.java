/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.metadata;

import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public final class ProductActionsMetaData {

    public static final class EndorseActionTab extends MetaData {
        public static final AttributeDescriptor ENDORSEMENT_DATE = declare("Endorsement Date", TextBox.class);
        public static final AttributeDescriptor ENDORSEMENT_REASON = declare("Endorsement Reason", ComboBox.class);
    }

    public static final class RenewActionTab extends MetaData {
        public static final AttributeDescriptor RENEWAL_DATE = declare("Renewal Date", TextBox.class);
        public static final AttributeDescriptor REASON_FOR_RENEWAL_WITH_LAPSE = declare("Reason for Renewal with Lapse", ComboBox.class);
    }
}
