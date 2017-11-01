package aaa.soap.autopolicy;

import aaa.rest.IRestClient;
import aaa.soap.autopolicy.models.aaancnu_wsdl_getautopolicydetail_version2.ErrorInfo;
import aaa.soap.autopolicy.models.aaancnu_wsdl_getautopolicydetail_version2.GetAutoPolicyDetail;
import aaa.soap.autopolicy.models.aaancnu_wsdl_getautopolicydetail_version2.GetAutoPolicyDetailRequest;
import aaa.soap.autopolicy.models.aaancnu_wsdl_getautopolicydetail_version2.GetAutoPolicyDetailResponse;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.rest.RestServiceUtil;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
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
