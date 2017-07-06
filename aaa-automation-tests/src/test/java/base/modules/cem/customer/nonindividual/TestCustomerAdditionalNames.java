package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.main.enums.CustomerConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 *
 * @author Yauheni Maliarevich
 * @name Test Customers Additional Names
 * @scenario 1. Create non individual customer
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
    public void testCutomerAdditionalNames() {
        mainApp().open();

        createCustomerNonIndividual();
        customer.addCustomerAdditionalNames().perform(tdSpecific.getTestData("TestData"));

        GeneralTab.table.getRow(CustomerConstants.AdditionalNamesTable.FIRST_ROW).getCell(CustomerConstants
                .AdditionalNamesTable.NAME_DBA).verify
                .value(tdSpecific.getValue("TestData",CustomerMetaData.GeneralTab.class.getSimpleName(),
                        CustomerMetaData.GeneralTab.AdditionalNameDetailsSection.class.getSimpleName(),
                        CustomerMetaData.GeneralTab.AdditionalNameDetailsSection.NAME_DBA.getLabel()));
    }
}
