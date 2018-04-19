package aaa.soap.aaaSSPolicyRate;

import aaa.soap.AAAMarshaller;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigenservices.ErrorInfo;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigenservices.RatePolicyRequest;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigenservices.SSPolicyRatePort;
import aaa.soap.aaaSSPolicyRate.version1_6.com.exigenservices.SSPolicyRateService;

import static aaa.admin.modules.IAdmin.log;

public class SSPolicyRateWSClient{

	private SSPolicyRateService ssPolicyRateService;
	private SSPolicyRatePort ssPolicyRate;

	public SSPolicyRateWSClient() {
		ssPolicyRateService = new SSPolicyRateService();
		ssPolicyRate = ssPolicyRateService.getSSPolicyRatePort();
	}

	public RatePolicyRequest getSSPolicyRateServiceResponse(RatePolicyRequest ratePolicyRequest){
		log.info("SOAP: REQUEST");
		log.info(AAAMarshaller.modelToXml(ratePolicyRequest));
		RatePolicyRequest response = null;
		try {
			response = ssPolicyRate.rateQuote(ratePolicyRequest);
		} catch (ErrorInfo errorInfo) {
			errorInfo.printStackTrace();
		}
		log.info("SOAP: RESPONSE");
		assert response != null;
		log.info(AAAMarshaller.modelToXml(response));
		return response;
	}

}
