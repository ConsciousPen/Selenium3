package aaa.soap.autopolicy;

import static aaa.admin.modules.IAdmin.log;
import java.time.format.DateTimeFormatter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.rest.IRestClient;
import aaa.soap.AAAMarshaller;
import aaa.soap.autopolicy.models.wsdl.ErrorInfo;
import aaa.soap.autopolicy.models.wsdl.GetAutoPolicyDetail;
import aaa.soap.autopolicy.models.wsdl.GetAutoPolicyDetailRequest;
import aaa.soap.autopolicy.models.wsdl.GetAutoPolicyDetailResponse;
import toolkit.datax.TestData;
import toolkit.rest.RestServiceUtil;

public class GetAutoPolicyDetailsWSClient implements IRestClient {
	private GetAutoPolicyDetailService service;
	private GetAutoPolicyDetail autoPolicyDetail;

	public GetAutoPolicyDetailsWSClient() {
		service = new GetAutoPolicyDetailService();
		autoPolicyDetail = service.getGetAutoPolicyDetailSOAPPort();
	}

	public GetAutoPolicyDetailResponse getAutoPolicyDetailResponse(TestData testData) throws DatatypeConfigurationException {
		GetAutoPolicyDetailRequest vr = getAutoPolicyDetailRequest(testData);
		log.info("SOAP: REQUEST");
		log.info(AAAMarshaller.modelToXml(vr));
		GetAutoPolicyDetailResponse result = null;
		try {
			result = autoPolicyDetail.getAutoPolicyDetail(vr);
		} catch (ErrorInfo errorInfo) {
			errorInfo.printStackTrace();
		}
		log.info("SOAP: RESPONSE");
		log.info(AAAMarshaller.modelToXml(result));
		return result;
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
		throw new UnsupportedOperationException("Permitted for Soap services");
	}

}
