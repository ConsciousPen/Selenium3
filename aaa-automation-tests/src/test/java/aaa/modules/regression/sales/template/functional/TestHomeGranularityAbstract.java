package aaa.modules.regression.sales.template.functional;

import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import aaa.helpers.db.queries.HomeGranularityQueries;
import toolkit.db.DBService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TestHomeGranularityAbstract extends PolicyBaseTest {

    protected void pas23203_validateCensusBlockGroupAndLatLong(String address, String censusBlockGroup) {
        mainApp().open();
        createCustomerIndividual();
        TestData policyTd = getPolicyTD();
        policyTd.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(),
                HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
                HomeCaMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel()), address);
        String policyNumber = createPolicy(policyTd);
        validateCensusBlockGroupAndLatLong(policyNumber, censusBlockGroup);

    }

    protected void validateCensusBlockGroupAndLatLong(String policyNumber, String censusBlockGroup) {
        Map<String,String> censusBlockGroupAndLatLongFromDb = DBService.get().getRow(String.format(HomeGranularityQueries.SELECT_CENSUS_BLOCK_GROUP,policyNumber));
        String CensusBlockGroupDbValue = censusBlockGroupAndLatLongFromDb.get("CENSUSBLOCK");
        assertThat(CensusBlockGroupDbValue).as("Wrong Census Block Group, Expecting %1%s but found %2%s", censusBlockGroup, CensusBlockGroupDbValue).isEqualTo(censusBlockGroup);
    }
}
