/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.account.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.AccountMetaData;
import aaa.main.metadata.AccountMetaData.DesignatedContactsTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import base.modules.cem.account.AccountBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Account Designated Contact add/eliminate
 * @scenario
 * 1. Create Account with Individual Customer
 * 2. Navigate to Account tab
 * 3. Click on Add Contact button and add Contact Phone
 * 4. Verify that added contact phone appeared in Designated Contacts table
 * 5. Eliminate previously added contact
 * 6. Verify that previously eliminated contact is not present in Designated Contacts table
 * @details
 */
public class TestAccountDesignatedContactAddEliminate extends AccountBaseTest {

    @Test
    @TestInfo(component = "CRM.Account")
    public void testAccountDesignatedContactAddEliminate() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        NavigationPage.toMainTab(CustomerSummaryTab.ACCOUNT.get());
        String accountNumber = CustomerSummaryPage.labelAccountNumber.getValue();
        int designatedContactCount = CustomerSummaryPage.tableDesignatedContacts.getRowsCount();

        log.info("TEST: Add Designated Contact for Account # " + accountNumber);
        account.addDesignatedContact().perform(tdAccountIndividual.getTestData("DesignatedContact", "TestData"));
        CustomerSummaryPage.tableDesignatedContacts.getRow(1).getCell(3).verify.value(
                tdAccountIndividual.getTestData("DesignatedContact", "TestData", DesignatedContactsTab.class.getSimpleName()).getValue(
                        AccountMetaData.DesignatedContactsTab.CONTACT_PHONE.getLabel()));
        CustomerSummaryPage.tableDesignatedContacts.verify.rowsCount(designatedContactCount + 1);

        log.info("TEST: Eliminate Designated Contact for Account # " + accountNumber);
        account.eliminateDesignatedContact().perform(1);
        CustomerSummaryPage.tableDesignatedContacts.verify.rowsCount(designatedContactCount);
    }
}
