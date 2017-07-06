package base.modules.platform.admin.agencyvendor;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.admin.metadata.agencyvendor.AgencyMetaData;
import aaa.admin.metadata.agencyvendor.AgencyMetaData.AgencyInfoTab;
import aaa.admin.modules.agencyvendor.AgencyVendorType;
import aaa.admin.modules.agencyvendor.IAgencyVendor;
import aaa.admin.pages.agencyvendor.AgencyVendorPage;
import aaa.main.enums.AdminConstants;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;


/**
 * @author Jelena Dembovska
 * @name Test Agency search
 * @scenario
 * Preconditions:
 * 0. Create new agency with predefined values
 * Search test:
 * 1. Input non-existent agency name - search item not found
 * 2. Populate agency name with just created agency - one item is returned, check all fields are correspond to defined values
 * 3. Make a search for combination of agency name + agency code - one item is returned
 * 4. Make a search for partly agency name, checking "contains" in search capability - at least one (more than zero) item is returned     
 * 5. Search for combination of agency name + incorrect agency code - search item not found
 * 6. Populate specific search criteria - one item is returned
 * 

 * @details
 * JIRA ID: 7.2_All_UC_SearchAgency"
 * BP ID: UC2958446
 */

public class TestAgencySearch extends BaseTest {

	private IAgencyVendor agency = AgencyVendorType.AGENCY.get();
	private TestData tdAgency = testDataManager.agency.get(AgencyVendorType.AGENCY)
    		.getTestData("TestAgencySearch", "TestData");
    
	private Map<String, String> mapValues = new HashMap<>();
	
	
    @Test(groups = {"7.2_All_UC_SearchAgency"})
    @TestInfo(component = "Platform.Admin")
    public void testAgencySearch() {

    	storeValues();
    	
        adminApp().open();

        
        log.info("TEST: Preconditions: Create Agency");

        agency.create(tdAgency);
        
        CustomAssert.enableSoftMode();
        
      
        //1. Input non-existent agency name - search item not found
        AgencyVendorPage.search(AgencyVendorPage.getSearchTestData(AgencyMetaData.SearchByField.AGENCY_NAME.getLabel(), "some random text"));
        CustomAssert.assertEquals("Search item not found", AgencyVendorPage.searchMessage());
        
        //2. Populate agency name with just created agency - one item is returned, check all fields are correspond to defined values
        AgencyVendorPage.buttonClear.click();
        AgencyVendorPage.search(AgencyVendorPage.getSearchTestData(AgencyMetaData.SearchByField.AGENCY_NAME.getLabel(), mapValues.get("agency name")));
        AgencyVendorPage.tableAgencies.getRow(1).getCell(AdminConstants.AdminAgenciesTable.AGENCY_NAME).verify.value(mapValues.get("agency name"));
        AgencyVendorPage.tableAgencies.getRow(1).getCell(AdminConstants.AdminAgenciesTable.AGENCY_CODE).verify.value(mapValues.get("agency code"));
        AgencyVendorPage.tableAgencies.getRow(1).getCell(AdminConstants.AdminAgenciesTable.TAX_ID).verify.value(mapValues.get("tax id"));
        AgencyVendorPage.tableAgencies.getRow(1).getCell(AdminConstants.AdminAgenciesTable.STATE).verify.value("CA");
        AgencyVendorPage.tableAgencies.getRow(1).getCell(AdminConstants.AdminAgenciesTable.STATUS).verify.value("Active");
        CustomAssert.assertEquals(1, AgencyVendorPage.tableAgencies.getRowsCount());
        
        //3. Make a search for combination of agency name + agency code - one item is returned
        AgencyVendorPage.buttonClear.click();
        AgencyVendorPage.search(AgencyVendorPage.getSearchTestData(new SimpleDataProvider()
        	.adjust(AgencyMetaData.SearchByField.AGENCY_NAME.getLabel(), mapValues.get("agency name"))
        	.adjust(AgencyMetaData.SearchByField.AGENCY_CODE.getLabel(), mapValues.get("agency code"))));
        CustomAssert.assertEquals(1, AgencyVendorPage.tableAgencies.getRowsCount());
        
        //4. Make a search for partly agency name, checking "contains" in search capability - at least one (more than zero) item is returned  
        AgencyVendorPage.buttonClear.click();
        AgencyVendorPage.search(AgencyVendorPage.getSearchTestData(AgencyMetaData.SearchByField.AGENCY_NAME.getLabel(), "SearchAg"));
        CustomAssert.assertTrue("Search should return non-zero results", AgencyVendorPage.tableAgencies.getRowsCount() > 0);
        
        //5. Search for combination of agency name + incorrect agency code - search item not found
        AgencyVendorPage.buttonClear.click();
        AgencyVendorPage.search(AgencyVendorPage.getSearchTestData(new SimpleDataProvider()
        	.adjust(AgencyMetaData.SearchByField.AGENCY_NAME.getLabel(), "incorrect name")
        	.adjust(AgencyMetaData.SearchByField.AGENCY_CODE.getLabel(), mapValues.get("agency code"))));
        CustomAssert.assertEquals("Search item not found", AgencyVendorPage.searchMessage());
        
        //6. Populate specific search criteria - one item is returned
        AgencyVendorPage.buttonClear.click();
        AgencyVendorPage.search(AgencyVendorPage.getSearchTestData(new SimpleDataProvider()
    		.adjust(AgencyMetaData.SearchByField.AGENCY_NAME.getLabel(), mapValues.get("agency name"))
    		.adjust(AgencyMetaData.SearchByField.AGENCY_CODE.getLabel(), mapValues.get("agency code"))
    		.adjust(AgencyMetaData.SearchByField.TAX_ID.getLabel(), mapValues.get("tax id"))
    		.adjust(AgencyMetaData.SearchByField.STATUS.getLabel(), "Active")
    		.adjust(AgencyMetaData.SearchByField.STATE.getLabel(), "CA"))); 
        CustomAssert.assertEquals(1, AgencyVendorPage.tableAgencies.getRowsCount());
        
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
   
    }
    
    private void storeValues() {

        mapValues.put("agency name", tdAgency.getValue(AgencyInfoTab.class.getSimpleName(), AgencyInfoTab.AGENCY_NAME.getLabel()));
        mapValues.put("agency code", tdAgency.getValue(AgencyInfoTab.class.getSimpleName(), AgencyInfoTab.AGENCY_CODE.getLabel()));
        mapValues.put("tax id", tdAgency.getValue(AgencyInfoTab.class.getSimpleName(), AgencyInfoTab.TAX_ID.getLabel()));
  
    }
    
}
