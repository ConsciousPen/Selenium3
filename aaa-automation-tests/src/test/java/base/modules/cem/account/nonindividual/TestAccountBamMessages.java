/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.account.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.modules.account.actiontabs.AffinityGroupsTab;
import aaa.main.modules.account.actiontabs.DesignatedContactsTab;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import base.modules.cem.account.AccountBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Remigijus Giedraitis
 * @name Test Account BAM Messages
 * @scenario
 * 1. Create Account with Non-Individual Customer
 * 2. Navigate to Account tab
 * 3. Start Add Contact and Cancel
 * 4. Add Contact
 * 5. Start Add Customer and Cancel
 * 6. Add Customer
 * 7. Start Add Affinity Group and Cancel
 * 8. Add Affinity Group
 * 9. Start Update and Cancel
 * 10. Update Account
 * 11. Eliminate Customer from Account
 * 12. Verify BAM messages
 * @details https://jira.exigeninsurance.com/browse/EISDEV-135342
 */
public class TestAccountBamMessages extends AccountBaseTest {

    @Test
    @TestInfo(component = "CRM.Account")
    public void testAccountBamMessages() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toMainTab(CustomerSummaryTab.ACCOUNT.get());
        String accountNumber = CustomerSummaryPage.labelAccountNumber.getValue();

        log.info("STEP: Start Add Contact and Cancel for Account # " + accountNumber);
        CustomerSummaryPage.buttonAddContact.click();
        DesignatedContactsTab.buttonTopCancel.click();
        Page.dialogConfirmation.confirm();

        log.info("STEP: Add Contact for Account # " + accountNumber);
        account.addDesignatedContact().perform(tdAccountNonIndividual.getTestData("DesignatedContact", "TestData"));

        log.info("STEP: Start Add Customer and Cancel for Account # " + accountNumber);
        account.addCustomer().start();
        account.addCustomer().getView().fillUpTo(tdCustomerNonIndividual.getTestData("DataGather", "TestData"), GeneralTab.class);
        GeneralTab.buttonTopCancel.click();
        Page.dialogConfirmation.confirm();

        log.info("STEP: Add Customer for Account # " + accountNumber);
        account.addCustomer().perform(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));

        log.info("STEP: Start Add Affinity Group and Cancel for Account # " + accountNumber);
        account.addAffinityGroup().start();
        AffinityGroupsTab.buttonTopCancel.click();
        Page.dialogConfirmation.confirm();

        log.info("STEP: Add Affinity Group for Account # " + accountNumber);
        account.addAffinityGroup().perform(tdAccountNonIndividual.getTestData("AffinityGroups", "TestData"));

        log.info("STEP: Start Update and Cancel for Account # " + accountNumber);
        account.update().start();
        AffinityGroupsTab.buttonTopCancel.click();
        Page.dialogConfirmation.confirm();

        log.info("STEP: Update Account # " + accountNumber);
        account.update().perform(tdSpecific.getTestData("TestData"));

        log.info("STEP: Eliminate Customer for Account # " + accountNumber);
        account.eliminateCustomer().perform(1);

        log.info("TEST: Verify BAM Messages for Account # " + accountNumber);
        String accountCreatedMessage = String.format("Create Account %1s", accountNumber);
        String accountUpdateMessage = String.format("Update Account / Customer for Account %1s", accountNumber);
        String customerRemoveMessage = String.format(
                "Remove Customer %1s from Account %1s", customerNumber, accountNumber);

        CustomAssert.enableSoftMode();

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1, customerRemoveMessage);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(2, accountUpdateMessage);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(3, accountUpdateMessage);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(4, accountUpdateMessage);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(5, accountUpdateMessage);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(6, accountUpdateMessage);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(7, accountUpdateMessage);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(8, accountUpdateMessage);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(9, accountUpdateMessage);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(10, accountCreatedMessage);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }
}
