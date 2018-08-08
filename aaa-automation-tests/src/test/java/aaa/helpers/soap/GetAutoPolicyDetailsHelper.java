package aaa.helpers.soap;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import aaa.soap.autopolicy.GetAutoPolicyDetailsWSClient;
import aaa.soap.autopolicy.models.wsdl.ErrorInfo;
import aaa.soap.autopolicy.models.wsdl.GetAutoPolicyDetailResponse;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

public class GetAutoPolicyDetailsHelper {
    /**
     * initialization of the CXF WS client
     * JacksonJsonProvider – additional data read provider to handle JSON format returned from WS,
     */

    public GetAutoPolicyDetailResponse getAutoPolicyResponse(String policyNumber) throws ErrorInfo, DatatypeConfigurationException {
        Map<String, Object> testDataReportsTab = new LinkedHashMap<String, Object>();
        testDataReportsTab.put("Policy Number", policyNumber);
        TestData std = new SimpleDataProvider(testDataReportsTab);

        return new GetAutoPolicyDetailsWSClient().getAutoPolicyDetailResponse(std);
    }

}
