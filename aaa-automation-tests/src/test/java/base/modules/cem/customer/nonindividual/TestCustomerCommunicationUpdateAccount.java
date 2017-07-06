/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.CustomerConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.actiontabs.CommunicationActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Update Communication from Account
 * @scenario
 * 1. Create Non-Individual Customer
 * 2. Navigate to Account tab
 * 3. Add Communication
 * 4. Update previously added Communication
 * 5. Verify that previously added Communication is updated
 * @details
 */
public class TestCustomerCommunicationUpdateAccount extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCommunicationUpdateAccount() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Add Communication for Customer # " + customerNumber);
        NavigationPage.toMainTab(CustomerSummaryTab.ACCOUNT.get());
        customer.addCommunication().perform(tdCustomerNonIndividual.getTestData("Communication", "TestData_Account"));

        log.info("TEST: Update Communication for Customer # " + customerNumber);
        CustomerSummaryPage.expandRelatedCommunications();
        customer.updateCommunication().perform(1, tdSpecific.getTestData("TestData"));
        CommunicationActionTab.tableCommunications.getRow(1).getCell(CustomerConstants.CustomerCommunicationsTable.CHANNEL).verify.value(tdSpecific.getTestData("TestData")
                .getTestData(CustomerMetaData.CommunicationActionTab.class.getSimpleName())
                .getValue(CustomerMetaData.CommunicationActionTab.COMMUNICATION_CHANNEL.getLabel()));
    }
}
