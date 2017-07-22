/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ca_choice;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import toolkit.utils.TestInfo;

/**
 * @author Jelena Dembovska
 * @name Test Billing functionality
 * @scenario
 * 1. Find customer or create new if customer does not exist;
 * 2. Create new Auto Policy;
 * 3. Move to Billing tab
 * 4. Make 4 different payments(Accept Payment with Payments types: Cash, Check, Credit Card, EFT);
 * 6. Verify payments are displayed in Payments & Other Transactions section.
 * 7. Make Refund with Payment type Check
 * 8. Verify Refund is displayed in Payments & Other Transactions section.
 * @details
 */

public class TestPolicyBilling extends aaa.modules.policy.templates.PolicyBilling {

	
    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }
    
    @Override
    @Test
    @TestInfo(component = "Policy.AutoCA.Billing")
    public void testBilling() {

        super.testBilling();
    }
    
}
