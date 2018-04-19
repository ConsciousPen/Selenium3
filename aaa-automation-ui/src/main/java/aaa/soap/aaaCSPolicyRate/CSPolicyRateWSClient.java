
package aaa.soap.aaaCSPolicyRate;

import aaa.rest.IRestClient;
import aaa.soap.AAAMarshaller;
import aaa.soap.aaaCSPolicyRate.com.exigenservices.CSPolicyRatePort;
import aaa.soap.aaaCSPolicyRate.com.exigenservices.CSPolicyRateService;
import aaa.soap.aaaCSPolicyRate.com.exigenservices.ErrorInfo;
import aaa.soap.aaaCSPolicyRate.com.exigenservices.RatePolicyRequest;
import toolkit.rest.RestServiceUtil;

import static aaa.admin.modules.IAdmin.log;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 */
public class CSPolicyRateWSClient implements IRestClient {

	private CSPolicyRateService csPolicyRateService;
	private CSPolicyRatePort csPolicyRatePort;

	public CSPolicyRateWSClient() {
		csPolicyRateService = new CSPolicyRateService();
		csPolicyRatePort = csPolicyRateService.getCSPolicyRatePort();
	}

	public RatePolicyRequest getCSPolicyRateServiceResponse(RatePolicyRequest ratePolicyRequest){
		log.info("SOAP: REQUEST");
		log.info(AAAMarshaller.modelToXml(ratePolicyRequest));
		RatePolicyRequest response = null;
		try {
			response = csPolicyRatePort.rateQuote(ratePolicyRequest);
		} catch (ErrorInfo errorInfo) {
			errorInfo.printStackTrace();
		}
		log.info("SOAP: RESPONSE");
		assert response != null;
		log.info(AAAMarshaller.modelToXml(response));
		return response;
	}

	@Override
	public RestServiceUtil getRestClient() {
		throw new UnsupportedOperationException("Permitted for Soap services");
	}

}

