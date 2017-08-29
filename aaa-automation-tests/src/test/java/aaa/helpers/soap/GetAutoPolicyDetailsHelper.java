package aaa.helpers.soap;


import aaa.soap.getAutoPolicyDetails.GetAutoPolicyDetailService;
import aaa.soap.getAutoPolicyDetails.GetAutoPolicyDetailsWSClient;
import aaa.soap.getAutoPolicyDetails.aaancnu_wsdl_getautopolicydetail_version2.ErrorInfo;
import aaa.soap.getAutoPolicyDetails.aaancnu_wsdl_getautopolicydetail_version2.GetAutoPolicyDetailResponse;
import toolkit.config.PropertyProvider;
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
    private static GetAutoPolicyDetailService getAutoPolicyDetailService;
    /**
     * wsUrl – path to WS created using environment properties file (src\test\resources\config\app\*.*)
     */
    private static String wsUrl = "http://" + PropertyProvider.getProperty("app.host") + PropertyProvider.getProperty("test.ws.port");


    public static GetAutoPolicyDetailResponse getAutoPolicyResponse(String policyNumber) throws ErrorInfo, DatatypeConfigurationException {
        boolean result = false;

        Map<String, Object> testDataReportsTab = new LinkedHashMap<String, Object>();
        testDataReportsTab.put("Policy Number", policyNumber);
        TestData std = new SimpleDataProvider(testDataReportsTab);

        return getAutoPolicyDetailsWSClient.getAutoPolicyDetailResponse(std);
    }

}
