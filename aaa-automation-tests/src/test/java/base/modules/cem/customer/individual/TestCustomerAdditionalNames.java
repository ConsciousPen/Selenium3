package base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.main.enums.CustomerConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 *
 * @author Yauheni Maliarevich
 * @name Test Customers Additional Names
 * @scenario 1. Create individual customer
 * 2. Click Update Customer
 * 3. Click Add Additional Name
 * 4. Fill additional name fields
 * 5. Click Add All
 * 6. Verify that additional name data is correct
 * @details
 */


public class TestCustomerAdditionalNames extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerAdditionalNames() {
        mainApp().open();

        createCustomerIndividual();
        customer.addCustomerAdditionalNames().perform(tdSpecific.getTestData("TestData"));

        CustomAssert.enableSoftMode();

        GeneralTab.table.getRow(CustomerConstants.AdditionalNamesTable.FIRST_ROW).getCell(CustomerConstants
                .AdditionalNamesTable.SALUTATION).verify
                .value(tdSpecific.getValue("TestData",CustomerMetaData.GeneralTab.class.getSimpleName(),
                        CustomerMetaData.GeneralTab.AdditionalNameDetailsSection.class.getSimpleName(),
                        CustomerMetaData.GeneralTab.AdditionalNameDetailsSection.SALUTATION.getLabel()));

        GeneralTab.table.getRow(CustomerConstants.AdditionalNamesTable.FIRST_ROW).getCell(CustomerConstants
                .AdditionalNamesTable.FIRST_NAME).verify
                .value(tdSpecific.getValue("TestData",CustomerMetaData.GeneralTab.class.getSimpleName(),
                        CustomerMetaData.GeneralTab.AdditionalNameDetailsSection.class.getSimpleName(),
                        CustomerMetaData.GeneralTab.AdditionalNameDetailsSection.FIRST_NAME.getLabel()));

        GeneralTab.table.getRow(CustomerConstants.AdditionalNamesTable.FIRST_ROW).getCell(CustomerConstants
                .AdditionalNamesTable.MIDDLE_NAME).verify
                .value(tdSpecific.getValue("TestData",CustomerMetaData.GeneralTab.class.getSimpleName(),
                        CustomerMetaData.GeneralTab.AdditionalNameDetailsSection.class.getSimpleName(),
                        CustomerMetaData.GeneralTab.AdditionalNameDetailsSection.MIDDLE_NAME.getLabel()));

        GeneralTab.table.getRow(CustomerConstants.AdditionalNamesTable.FIRST_ROW).getCell(CustomerConstants
                .AdditionalNamesTable.LAST_NAME).verify
                .value(tdSpecific.getValue("TestData",CustomerMetaData.GeneralTab.class.getSimpleName(),
                        CustomerMetaData.GeneralTab.AdditionalNameDetailsSection.class.getSimpleName(),
                        CustomerMetaData.GeneralTab.AdditionalNameDetailsSection.LAST_NAME.getLabel()));

        GeneralTab.table.getRow(CustomerConstants.AdditionalNamesTable.FIRST_ROW).getCell(CustomerConstants
                .AdditionalNamesTable.SUFFIX).verify
                .value(tdSpecific.getValue("TestData",CustomerMetaData.GeneralTab.class.getSimpleName(),
                        CustomerMetaData.GeneralTab.AdditionalNameDetailsSection.class.getSimpleName(),
                        CustomerMetaData.GeneralTab.AdditionalNameDetailsSection.SUFFIX.getLabel()));

        GeneralTab.table.getRow(CustomerConstants.AdditionalNamesTable.FIRST_ROW).getCell(CustomerConstants
                .AdditionalNamesTable.DESIGNATION).verify
                .value(tdSpecific.getValue("TestData",CustomerMetaData.GeneralTab.class.getSimpleName(),
                        CustomerMetaData.GeneralTab.AdditionalNameDetailsSection.class.getSimpleName(),
                        CustomerMetaData.GeneralTab.AdditionalNameDetailsSection.DESIGNATION.getLabel()));

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }
}
