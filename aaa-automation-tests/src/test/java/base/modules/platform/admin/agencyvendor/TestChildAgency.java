package base.modules.platform.admin.agencyvendor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import aaa.admin.metadata.agencyvendor.AgencyMetaData;
import aaa.admin.metadata.agencyvendor.AgencyMetaData.AgencyInfoTab;
import aaa.admin.modules.agencyvendor.AgencyVendorType;
import aaa.admin.modules.agencyvendor.IAgencyVendor;
import aaa.admin.modules.agencyvendor.agency.defaulttabs.ChildrenTab;
import aaa.admin.pages.agencyvendor.AgencyVendorPage;
import aaa.admin.pages.agencyvendor.ChildrenPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.AdminConstants;
import aaa.main.enums.CustomerConstants;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * @author Jelena Dembovska
 * @name Test Child agency assigning
 * @scenario
 * Preconditions:
 * 0. Create two agencies (will be defined as parent and child)
 * 1. Open agency supposed to be parent - check there no any children
 * 2. Search functionality testing
 * 2.1 Input non-existent agency name - search item not found
 * 2.2 Populate agency name with just created agency - one item is returned, check all fields corresponds to defined values
 * 2.3 Make a search for combination of agency name + agency code - one item is returned
 * 2.4 Make a search for partly agency name, checking "contains" in search capability - at least one (more than zero) item is returned
 * 2.5 Search for combination of agency name + incorrect agency code - search item not found
 * 2.6 Populate specific search criteria - one item is returned
 * 3. On "Children" tab attach agency as a child
 * 4. Check: agency is added successfully and listed as a child agency
 * 5. Check cancellation dialog: press "Cancel", but reject cancellation
 * 6. Confirm changes (agency is added), press "Done"
 * 7. Reopen parent agency, check Children - child agency is attached and listed.
 * 8. Remove child agency, check agency is removed
 * 9. Press "Cancel" to discard removing
 * 10. Reopen parent agency, check child agency is still assigned
 * @details
 * JIRA ID: 7.2_All_UC_Add/EditChildAgency(s)
 * BP ID: UC2958468
 * JIRA ID: 7.2_All_UC_Agency(S)SearchingAndSelecting
 * BP ID: UC2954353
 */

public class TestChildAgency extends BaseTest {

    private IAgencyVendor agency = AgencyVendorType.AGENCY.get();
    private TestData tdAgency = testDataManager.agency.get(AgencyVendorType.AGENCY);
    private TestData tdAgencyChild = tdAgency.getTestData("TestChildAgency", "TestData_Child");
    private TestData tdAgencyParent = tdAgency.getTestData("TestChildAgency", "TestData_Parent");

    private Map<String, String> mapValues = new HashMap<>();

    @Test(groups = {"7.2_All_UC_Add/EditChildAgency(S)", "7.2_All_UC_Agency(S)SearchingAndSelecting"})
    @TestInfo(component = "Platform.Admin")
    public void testChildAgency() {

        storeValues();

        adminApp().open();

        //0. Create two agencies (will be defined as parent and child)
        log.info("TEST: Preconditions: Create Agency");

        agency.create(tdAgencyChild);
        agency.create(tdAgencyParent);

        CustomAssert.enableSoftMode();

        //1. Open agency supposed to be parent - check there no any children
        AgencyVendorPage.search(AgencyVendorPage.getSearchTestData(AgencyMetaData.SearchByField.AGENCY_NAME.getLabel(), mapValues.get("parent agency name")));
        CustomAssert.assertEquals(1, AgencyVendorPage.tableAgencies.getRowsCount());

        agency.update().start(1);
        NavigationPage.toViewTab(NavigationEnum.AgencyVendorTab.CHILDREN.get());

        CustomAssert.assertEquals(0, ChildrenPage.tableAddedChildren.getRowsCount());

        ChildrenPage.buttonAddChild.click();

        //2. Search functionality testing
        //2.1 Input non-existent agency name - search item not found
        ChildrenPage.search(ChildrenPage.getSearchTestData(AgencyMetaData.SearchByField.AGENCY_NAME.getLabel(), "some random text"));
        CustomAssert.assertEquals("Search item not found", ChildrenPage.searchMessage());

        //2.2 Populate agency name with just created agency - one item is returned, check all fields corresponds to defined values
        ChildrenPage.buttonClear.click();
        ChildrenPage.search(ChildrenPage.getSearchTestData(AgencyMetaData.SearchByField.AGENCY_NAME.getLabel(), mapValues.get("child agency name")));
        ChildrenPage.tableSearchFormResults.getRow(1).getCell(AdminConstants.AdminSearchFormResultsTable.AGENCY_NAME).verify.value(mapValues.get("child agency name"));
        ChildrenPage.tableSearchFormResults.getRow(1).getCell(AdminConstants.AdminAgenciesTable.AGENCY_CODE).verify.value(mapValues.get("child agency code"));
        ChildrenPage.tableSearchFormResults.getRow(1).getCell(AdminConstants.AdminSearchFormResultsTable.ADDRESS).verify.value("Address Line 1, Walnut Creek, CA, 94596");
        CustomAssert.assertEquals(1, ChildrenPage.tableSearchFormResults.getRowsCount());

        //2.3 Make a search for combination of agency name + agency code - one item is returned
        ChildrenPage.buttonClear.click();
        ChildrenPage.search(ChildrenPage.getSearchTestData(new SimpleDataProvider()
                .adjust(AgencyMetaData.SearchByField.AGENCY_NAME.getLabel(), mapValues.get("child agency name"))
                .adjust(AgencyMetaData.SearchByField.AGENCY_CODE.getLabel(), mapValues.get("child agency code"))));
        CustomAssert.assertEquals(1, ChildrenPage.tableSearchFormResults.getRowsCount());

        //2.4 Make a search for partly agency name, checking "contains" in search capability - at least one (more than zero) item is returned  
        ChildrenPage.buttonClear.click();
        ChildrenPage.search(ChildrenPage.getSearchTestData(AgencyMetaData.SearchByField.AGENCY_NAME.getLabel(), "Name_"));
        CustomAssert.assertTrue("Search should return non-zero results", ChildrenPage.tableSearchFormResults.getRowsCount() > 0);

        //2.5 Search for combination of agency name + incorrect agency code - search item not found
        ChildrenPage.buttonClear.click();
        ChildrenPage.search(ChildrenPage.getSearchTestData(new SimpleDataProvider()
                .adjust(AgencyMetaData.SearchByField.AGENCY_NAME.getLabel(), "incorrect name")
                .adjust(AgencyMetaData.SearchByField.AGENCY_CODE.getLabel(), mapValues.get("child agency code"))));
        CustomAssert.assertEquals("Search item not found", ChildrenPage.searchMessage());

        //2.6 Populate specific search criteria - one item is returned
        ChildrenPage.buttonClear.click();
        ChildrenPage.search(ChildrenPage.getSearchTestData(new SimpleDataProvider()
                .adjust(AgencyMetaData.SearchByField.AGENCY_NAME.getLabel(), mapValues.get("child agency name"))
                .adjust(AgencyMetaData.SearchByField.AGENCY_CODE.getLabel(), mapValues.get("child agency code"))
                .adjust(AgencyMetaData.SearchByField.CITY.getLabel(), "Walnut Creek")
                .adjust(AgencyMetaData.SearchByField.STATE_PROVINCE.getLabel(), "CA")));
        CustomAssert.assertEquals(1, ChildrenPage.tableSearchFormResults.getRowsCount());

        ChildrenPage.buttonCancelSearchDialog.click();

        //3. On "Children" tab attach agency as a child
        TestData testData_addChild = tdAgency.getTestData("TestChildAgency", "AddChild")
                .adjust(TestData.makeKeyPath(AgencyMetaData.ChildrenTab.class.getSimpleName(), "Add Child Agency", "Agency Code"),
                        mapValues.get("child agency code"))
                .adjust(TestData.makeKeyPath(AgencyMetaData.ChildrenTab.class.getSimpleName(), "Add Child Agency", "Agency Name"),
                        mapValues.get("child agency name"));

        new ChildrenTab().fillTab(testData_addChild);

        //4. Check: agency is added successfully and listed as a child agency
        CustomAssert.assertEquals(1, ChildrenPage.tableAddedChildren.getRowsCount());
        Map<String, String> filterMap = Table.buildQuery("Agency Code->" + mapValues.get("child agency code") + "|Agency Name->" + mapValues.get("child agency name"));
        Map<Object, String> filterMapObj = Collections.<Object, String> unmodifiableMap(filterMap);
        ChildrenPage.tableAddedChildren.getRow(1).verify.values(filterMapObj);

        //5. Check cancellation dialog: press "Cancel", but reject cancellation
        ChildrenTab.buttonCancel.click();
        Assert.assertEquals(Page.dialogConfirmation.isPresent(), true);
        Page.dialogConfirmation.labelMessage.verify.value(CustomerConstants.ARE_YOU_SURE_YOU_WANT_TO_CONTINUE);
        Page.dialogConfirmation.reject();
        Assert.assertEquals(Page.dialogConfirmation.isPresent(), false);

        //6. Confirm changes (agency is added), press "Done"
        ChildrenTab.buttonDone.click();

        //7. Reopen parent agency, check Children - child agency is attached and listed.
        agency.update().start(1);
        NavigationPage.toViewTab(NavigationEnum.AgencyVendorTab.CHILDREN.get());
        CustomAssert.assertEquals(1, ChildrenPage.tableAddedChildren.getRowsCount());

        //8. Remove child agency, check agency is removed
        ChildrenPage.tableAddedChildren.getRow(1).getCell(AdminConstants.AdminAddedChildrenTable.ACTION).controls.links.get(ActionConstants.DELETE).click();
        Page.dialogConfirmation.confirm();

        CustomAssert.assertEquals(0, ChildrenPage.tableAddedChildren.getRowsCount());

        //9. Press "Cancel" to discard removing 
        ChildrenTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        //10. Reopen parent agency, check child agency is still assigned
        agency.update().start(1);
        NavigationPage.toViewTab(NavigationEnum.AgencyVendorTab.CHILDREN.get());
        CustomAssert.assertEquals(1, ChildrenPage.tableAddedChildren.getRowsCount());

        ChildrenTab.buttonDone.click();

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();

    }

    private void storeValues() {

        mapValues.put("parent agency name", tdAgencyParent.getValue(AgencyInfoTab.class.getSimpleName(), AgencyInfoTab.AGENCY_NAME.getLabel()));
        mapValues.put("parent agency code", tdAgencyParent.getValue(AgencyInfoTab.class.getSimpleName(), AgencyInfoTab.AGENCY_CODE.getLabel()));

        mapValues.put("child agency name", tdAgencyChild.getValue(AgencyInfoTab.class.getSimpleName(), AgencyInfoTab.AGENCY_NAME.getLabel()));
        mapValues.put("child agency code", tdAgencyChild.getValue(AgencyInfoTab.class.getSimpleName(), AgencyInfoTab.AGENCY_CODE.getLabel()));

    }

}
