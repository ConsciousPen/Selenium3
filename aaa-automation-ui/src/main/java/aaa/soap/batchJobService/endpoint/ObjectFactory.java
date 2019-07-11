
package aaa.soap.batchJobService.endpoint;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.exigen.aaa.batchjobws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

	/**
	 * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.exigen.aaa.batchjobws
	 *
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link JobGroupStartRequest }
	 *
	 */
	public JobGroupStartRequest createJobGroupStartRequest() {
		return new JobGroupStartRequest();
	}

	/**
	 * Create an instance of {@link JobGroupStatusResponse }
	 *
	 */
	public JobGroupStatusResponse createJobGroupStatusResponse() {
		return new JobGroupStatusResponse();
	}

	/**
	 * Create an instance of {@link JobGroupStartResponse }
	 *
	 */
	public JobGroupStartResponse createJobGroupStartResponse() {
		return new JobGroupStartResponse();
	}

	/**
	 * Create an instance of {@link JobGroupStatusRequest }
	 *
	 */
	public JobGroupStatusRequest createJobGroupStatusRequest() {
		return new JobGroupStatusRequest();
	}

}
