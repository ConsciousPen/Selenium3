/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ca_choice;

import aaa.main.modules.policy.PolicyType;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestQuoteViewRatingDetailsCompCollSymbolsPresence extends aaa.modules.policy.templates.RatingDetailsCompCollSymbolsPresence {
    /**
     * PAS-535
     * @author Viktor Petrenko
     * @name View Rating details UI update.
     * @scenario
     * 0. Create customer and Auto CA policy with 2 Vehicles
     * 1. Initiate quote creation
     * 2. Go to the vehicle tab
     * 3. Add second vehicle
     * 4. Rate quote
     * 5. Open rating detail view
     * @details
     */

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }

    @Test
    @TestInfo(component = "Policy.AutoCA")
    public void testQuoteRatingViewDetailsCompCollSymbolsArePresentAndNotEmpty() {
        super.verifyCompCollSymbolsPresence();
    }
}
