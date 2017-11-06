package aaa.helpers.soap;


import aaa.soap.autopolicy.GetAutoPolicyDetailsWSClient;
import aaa.soap.autopolicy.models.wsdl.ErrorInfo;
import aaa.soap.autopolicy.models.wsdl.GetAutoPolicyDetailResponse;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.LinkedHashMap;
import java.util.Map;

public class GetAutoPolicyDetailsHelper {
    /**
     * initialization of the CXF WS client
     * JacksonJsonProvider – additional data read provider to handle JSON format returned from WS,
     */
    public static GetAutoPolicyDetailsWSClient getAutoPolicyDetailsWSClient = new GetAutoPolicyDetailsWSClient();

    public static GetAutoPolicyDetailResponse getAutoPolicyResponse(String policyNumber) throws ErrorInfo, DatatypeConfigurationException {

        Map<String, Object> testDataReportsTab = new LinkedHashMap<String, Object>();
        testDataReportsTab.put("Policy Number", policyNumber);
        TestData std = new SimpleDataProvider(testDataReportsTab);

        return getAutoPolicyDetailsWSClient.getAutoPolicyDetailResponse(std);
    }

}
