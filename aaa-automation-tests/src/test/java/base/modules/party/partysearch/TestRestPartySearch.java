package base.modules.party.partysearch;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.helpers.rest.PartySearchRestHelper;
import aaa.modules.RestBaseTest;
import aaa.rest.RESTServiceUser;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestRestPartySearch extends RestBaseTest {

    @Test
    @TestInfo(component = "Platform.REST", testCaseId = "IPBQA-22422")
    public void testPartySearch() {

        mainApp().open();

        CustomerWithRelationship customer1 = createCustomerWithRelationship(tdPartySearchRest.getTestData("PSRCustomer1"), tdPartySearchRest.getTestData("PSRCustomerRel1"));
        CustomerWithRelationship customer2 = createCustomerWithRelationship(tdPartySearchRest.getTestData("PSRCustomer2"), tdPartySearchRest.getTestData("PSRCustomerRel2"));
        CustomerWithRelationship customer3 = createCustomerWithRelationship(tdPartySearchRest.getTestData("PSRCustomer3"), tdPartySearchRest.getTestData("PSRCustomerRel3"));
        CustomerWithRelationship customer4 = createCustomerWithRelationship(tdPartySearchRest.getTestData("PSRCustomer4"), tdPartySearchRest.getTestData("PSRCustomerRel4"));
        CustomerWithRelationship customer5 = createCustomerWithRelationship(tdPartySearchRest.getTestData("PSRCustomer5"), tdPartySearchRest.getTestData("PSRCustomerRel5"));
        CustomerWithRelationship customer6 = createCustomerWithRelationship(tdPartySearchRest.getTestData("PSRCustomer6"), tdPartySearchRest.getTestData("PSRCustomerRel6"));
        CustomerWithRelationship customer7 = createCustomerWithRelationship(tdPartySearchRest.getTestData("PSRCustomer7"), tdPartySearchRest.getTestData("PSRCustomerRel7"));
        CustomerWithRelationship customer8 = createCustomerWithRelationship(tdPartySearchRest.getTestData("PSRCustomer8"), tdPartySearchRest.getTestData("PSRCustomerRel8"));

        Map<String, String> partiesMapper = new HashMap<>();
        partiesMapper.put("customer1", customer1.getCustomer().getCustomerNumber());
        partiesMapper.put("customer2", customer2.getCustomer().getCustomerNumber());
        partiesMapper.put("customer3", customer3.getCustomer().getCustomerNumber());
        partiesMapper.put("customer4", customer4.getCustomer().getCustomerNumber());
        partiesMapper.put("customer5", customer5.getCustomer().getCustomerNumber());
        partiesMapper.put("customer6", customer6.getCustomer().getCustomerNumber());
        partiesMapper.put("customer7", customer7.getCustomer().getCustomerNumber());
        partiesMapper.put("customer8", customer8.getCustomer().getCustomerNumber());
        partiesMapper.put("relationship1", customer1.getRelatedEntity().getCustomerNumber());
        partiesMapper.put("relationship2", customer2.getRelatedEntity().getCustomerNumber());
        partiesMapper.put("relationship3", customer3.getRelatedEntity().getCustomerNumber());
        partiesMapper.put("relationship4", customer4.getRelatedEntity().getCustomerNumber());
        partiesMapper.put("relationship5", customer5.getRelatedEntity().getCustomerNumber());
        partiesMapper.put("relationship6", customer6.getRelatedEntity().getCustomerNumber());
        partiesMapper.put("relationship7", customer7.getRelatedEntity().getCustomerNumber());
        partiesMapper.put("relationship8", customer8.getRelatedEntity().getCustomerNumber());

        CustomAssert.enableSoftMode();

        PartySearchRestHelper.assertPartySearchResponse(tdPartySearchRest.getTestData("MainTestsSteps1_9"), RESTServiceUser.ALL_RIGHTS, partiesMapper);

        PartySearchRestHelper.assertPartySearchResponse(tdPartySearchRest.getTestData("MainTestsSteps10_17"), RESTServiceUser.PARTY_SEARCH_RS_USER_1, partiesMapper);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }
}
