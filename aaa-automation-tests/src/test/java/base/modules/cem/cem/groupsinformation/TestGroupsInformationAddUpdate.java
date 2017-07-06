/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.cem.groupsinformation;

import org.testng.annotations.Test;

import aaa.admin.metadata.cem.CemMetaData;
import aaa.admin.metadata.cem.CemMetaData.EditGroupsInformationActionTab;
import aaa.admin.metadata.cem.CemMetaData.SearchByField;
import aaa.admin.pages.cem.CemPage;
import aaa.main.enums.CEMConstants;
import base.modules.cem.cem.CemBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Groups Informationt Add Update
 * @scenario
 * 1. Navigate to Admin application
 * 2. Navigate to CEM tab
 * 3. Navigate to Groups Information subtab in left menu
 * 4. Add New Groups Information
 * 5. Verify that Groups Information is added to table
 * 6. Update groups information, and change Groups Name
 * 5. Verify that Groups name was updated
 * @details
 */
public class TestGroupsInformationAddUpdate extends CemBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testGroupsInformationAddUpdate() {
        adminApp().open();

        log.info("TEST: Create Update New Major/Large Account");
        String groupId = tdGroupsInformation.getValue("GroupsInformation", "TestData",
                CemMetaData.CreateGroupsInformationTab.class.getSimpleName(),
                CemMetaData.CreateGroupsInformationTab.GROUP_ID.getLabel());

        groupsInformation.create(tdGroupsInformation.getTestData("GroupsInformation", "TestData"));

        groupsInformation.search(tdGroupsInformation.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.GROUP_ID.getLabel()), groupId));

        tdGroupsInformation.getValue("GroupsInformation", "TestData", CemMetaData.CreateGroupsInformationTab.class.getSimpleName(),
                CemMetaData.CreateGroupsInformationTab.GROUP_TYPE.getLabel());

        log.info("TEST: Update Major/Large Account");
        groupsInformation.editGroupsInformation().perform(tdGroupsInformation.getTestData("GroupsInformationUpdate", "TestData").adjust(TestData.makeKeyPath(
                EditGroupsInformationActionTab.class.getSimpleName(), EditGroupsInformationActionTab.GROUP_ID.getLabel()), groupId), 1);

        groupsInformation.search(tdGroupsInformation.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.GROUP_ID.getLabel()), groupId));

        CemPage.tableGroupsInformation.getRow(1).getCell(CEMConstants.CEMGroupsInformationTable.GROUP_TYPE).verify.value(tdGroupsInformation.getTestData(
                "GroupsInformationUpdate", "TestData", CemMetaData.EditGroupsInformationActionTab.class.getSimpleName())
                .getValue(CemMetaData.EditGroupsInformationActionTab.GROUP_TYPE.getLabel()));

        CemPage.tableGroupsInformation.getRow(1).verify.present();
    }
}
