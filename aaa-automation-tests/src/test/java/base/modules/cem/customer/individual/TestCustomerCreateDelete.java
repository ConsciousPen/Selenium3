/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.common.pages.MainPage;
import aaa.common.pages.Page;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Individual Create/Delete
 * @scenario
 * 1. Create Individual Customer
 * 2. Get Customer number
 * 3. Delete Customer
 * 4. Perform quick search for previously created Customer number
 * 5. Verify that after search message "Search item not found" is present
 * @details
 */
public class TestCustomerCreateDelete extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCreateDelete() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        log.info("Created Customer #" + customerNumber);

        customer.deleteCustomer().perform();
        MainPage.QuickSearch.search(customerNumber);
        Page.dialogConfirmation.labelMessage.verify.value("Search item not found");
    }
}
