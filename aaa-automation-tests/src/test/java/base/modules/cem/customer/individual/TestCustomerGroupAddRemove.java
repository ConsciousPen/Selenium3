/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.admin.metadata.cem.CemMetaData;
import aaa.admin.modules.cem.groupsinformation.GroupInformation;
import aaa.admin.modules.cem.groupsinformation.GroupInformationType;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Group Add Remove
 * @scenario
 * 1. Create Groups in Admin
 * 2. Save group ID
 * 3. Navigate to Main
 * 4. Create indivudual customer
 * 5. Select Update action from drop-down
 * 6. Click Add Group button
 * 7. Add your created group id
 * 8. Verify that group is present in table
 * 9. Click on "Remove this group info" button
 * 10. Verify that group is not present in table
 * @details
 */
public class TestCustomerGroupAddRemove extends CustomerBaseTest {

    private GroupInformationType groupsInformationType = GroupInformationType.GROUPS_INFORMATION;
    private GroupInformation groupsInformation = new GroupInformation();
    private TestData tdGroupsInformation = testDataManager.groupsInformation.get(groupsInformationType);

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerGroupAddRemove() {
        adminApp().open();

        String groupId = tdGroupsInformation.getValue("GroupsInformation", "TestData",
                CemMetaData.CreateGroupsInformationTab.class.getSimpleName(),
                CemMetaData.CreateGroupsInformationTab.GROUP_ID.getLabel());

        groupsInformation.create(tdGroupsInformation.getTestData("GroupsInformation", "TestData"));

        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Add Remove Group for Customer # " + customerNumber);

        customer.update().perform(tdCustomerIndividual.getTestData("AddGroup", "TestData").adjust(TestData.makeKeyPath(
                GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.GROUP_SEARCH.getLabel(), CustomerMetaData.GeneralTab.AddGroup.GROUP_ID.getLabel()),
                groupId).resolveLinks());

        customer.inquiry().start();

        CustomerSummaryPage.tableGroup.verify.present(true);

        GeneralTab.buttonCancel.click();

        customer.removeGroup().perform();

        customer.inquiry().start();

        CustomerSummaryPage.tableGroup.verify.present(true);
    }
}
