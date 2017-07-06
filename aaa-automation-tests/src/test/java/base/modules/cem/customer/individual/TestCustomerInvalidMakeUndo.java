/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.main.enums.CustomerConstants;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Make/Undo Invalid
 * @scenario
 * 1. Create Individual Unqualified Customer
 * 2. Perform Make Invalid action
 * 3. Verify that Lead Status = Invalid
 * 4. Perform Undo Invalid action
 * 5. Verify that Lead Status = Unqualified
 * @details
 */
public class TestCustomerInvalidMakeUndo extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerInvalidMakeUndo() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData_WithoutBusinessEntityTab").adjust(
                tdCustomerIndividual.getTestData("DataGather", "Adjustment_Unqualified")));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        CustomerSummaryPage.labelCustomerLeadStatus.verify.value(CustomerConstants.UNQUALIFIED);

        log.info("TEST: Make Invalid Customer # " + customerNumber);
        customer.makeInvalid().perform();
        CustomerSummaryPage.labelCustomerLeadStatus.verify.value(CustomerConstants.INVALID);

        log.info("TEST: Undo Invalid Customer # " + customerNumber);
        customer.undoInvalid().perform();
        CustomerSummaryPage.labelCustomerLeadStatus.verify.value(CustomerConstants.UNQUALIFIED);
    }
}
