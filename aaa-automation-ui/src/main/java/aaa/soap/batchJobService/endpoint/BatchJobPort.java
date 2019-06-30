package aaa.soap.batchJobService.endpoint;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

@WebService(name = "BatchJobPort", targetNamespace = "http://exigen.com/aaa/batchjobws")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
		ObjectFactory.class
})

public interface BatchJobPort {
	/**
	 *
	 * @param jobGroupStatusRequest
	 * @return
	 *     returns aaa.soap.batchJobService.JobGroupStatusResponse
	 */
	@WebMethod(action = "http://exigen.com/aaa/getJobGroupStatus")
	@WebResult(name = "JobGroupStatusResponse", targetNamespace = "http://exigen.com/aaa/batchjobws", partName = "JobGroupStatusResponse")
	public JobGroupStatusResponse getJobGroupStatus(
			@WebParam(name = "JobGroupStatusRequest", targetNamespace = "http://exigen.com/aaa/batchjobws", partName = "JobGroupStatusRequest")
					JobGroupStatusRequest jobGroupStatusRequest);

	/**
	 *
	 * @param jobGroupStartRequest
	 * @return
	 *     returns aaa.soap.batchJobService.JobGroupStartResponse
	 */
	@WebMethod(action = "http://exigen.com/aaa/startJobGroup")
	@WebResult(name = "JobGroupStartResponse", targetNamespace = "http://exigen.com/aaa/batchjobws", partName = "JobGroupStartResponse")
	public JobGroupStartResponse startJobGroup(
			@WebParam(name = "JobGroupStartRequest", targetNamespace = "http://exigen.com/aaa/batchjobws", partName = "JobGroupStartRequest")
					JobGroupStartRequest jobGroupStartRequest);

}
