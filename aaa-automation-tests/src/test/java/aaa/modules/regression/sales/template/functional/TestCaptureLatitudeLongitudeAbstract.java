package aaa.modules.regression.sales.template.functional;

import aaa.helpers.db.queries.VehicleQueries;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import aaa.main.modules.policy.PolicyType;
import aaa.helpers.db.queries.LatLongAddressQueries;
import toolkit.db.DBService;

import java.util.Map;

public class TestCaptureLatitudeLongitudeAbstract extends PolicyBaseTest {

   protected void  pas23203_validateLatLong(String zipCode, String address, boolean isPresent) {
       mainApp().open();
       createCustomerIndividual();
       TestData policyTd = getPolicyTD();
       //Override address and ZIP code. This address should return latitude and Longitude
       policyTd.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(),
               HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
               HomeCaMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel()), zipCode);
       policyTd.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(),
               HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
              HomeCaMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel()), address);

      String policyNumber = createPolicy(policyTd);
      validateLatLongInDB(policyNumber,isPresent);
   }

   protected void validateLatLongInDB(String policyNumber,boolean isPresent) {

      // Map<String,String> latLongValues = LatLongAddressQueries.getLatLongValuesFromSQL(policyNumber);
       Map<String,String> latLongValues = DBService.get().getRow(String.format(LatLongAddressQueries.SELECT_LATITUDE_LONGITUDE,policyNumber));
       String latitudeValue = latLongValues.get("LATITUDE");
       String longitudeValue = latLongValues.get("COLLSYMBOL");

   }
}
