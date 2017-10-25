package aaa.soap.getAutoPolicyDetails;

import aaa.rest.IModel;
import aaa.rest.IRestClient;

import aaa.soap.getAutoPolicyDetails.aaancnu_wsdl_getautopolicydetail_version2.*;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.rest.RestServiceUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;

public class GetAutoPolicyDetailsWSClient implements IRestClient {
    private GetAutoPolicyDetailService service;
    private GetAutoPolicyDetail autoPolicyDetail;

    public GetAutoPolicyDetailsWSClient() {
        service = new GetAutoPolicyDetailService();
        autoPolicyDetail = service.getGetAutoPolicyDetailSOAPPort();
    }

    public GetAutoPolicyDetailResponse getAutoPolicyDetailResponse(TestData testData) throws ErrorInfo, DatatypeConfigurationException {
        GetAutoPolicyDetailRequest vr = getAutoPolicyDetailRequest(testData);
        GetAutoPolicyDetailResponse result = null;
        try {
            result = autoPolicyDetail.getAutoPolicyDetail(vr);
        } catch (ErrorInfo errorInfo) {
            errorInfo.printStackTrace();
        }
        return result;
    }
    public static String modelToXml(IModel responseObj) {
        StringWriter sw = new StringWriter();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(responseObj.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(responseObj, sw);
        } catch (JAXBException e) {
            throw new IstfException("Unable to marshal object", e.getCause());
        }
        return sw.toString();
    }

    private GetAutoPolicyDetailRequest getAutoPolicyDetailRequest(TestData testData) throws DatatypeConfigurationException {

        GetAutoPolicyDetailRequest request = new GetAutoPolicyDetailRequest();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(TimeSetterUtil.getInstance().getCurrentTime().format(dateTimeFormatter));
        request.setPolicyNumber(testData.getValue("Policy Number"));
        request.setAsOfDate(xmlCal);
        request.setSourceSystem("PAS");
        request.setReturnCurrentWhenNotFnd(false);

        return request;
    }

    @Override
    public RestServiceUtil getRestClient() {
        return null;
    }

}
