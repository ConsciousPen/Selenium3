package aaa.helpers.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class BatchJobInfo {
	@XmlElement
	private String batchJobEnvironmentType;

	@XmlElement
	private String batchJobApplicationName;

	@XmlElement
	private String batchJobName;

	@XmlElement
	private String batchJobServerName;

	@XmlElement
	private String batchJobIPAddress;

	@XmlElement
	private String batchJobCorrelationId;

	public String getBatchJobEnvironmentType() {
		return batchJobEnvironmentType;
	}

	public void setBatchJobEnvironmentType(String batchJobEnvironmentType) {
		this.batchJobEnvironmentType = batchJobEnvironmentType;
	}

	public String getBatchJobApplicationName() {
		return batchJobApplicationName;
	}

	public void setBatchJobApplicationName(String batchJobApplicationName) {
		this.batchJobApplicationName = batchJobApplicationName;
	}

	public String getBatchJobName() {
		return batchJobName;
	}

	public void setBatchJobName(String batchJobName) {
		this.batchJobName = batchJobName;
	}

	public String getBatchJobServerName() {
		return batchJobServerName;
	}

	public void setBatchJobServerName(String batchJobServerName) {
		this.batchJobServerName = batchJobServerName;
	}

	public String getBatchJobIPAddress() {
		return batchJobIPAddress;
	}

	public void setBatchJobIPAddress(String batchJobIPAddress) {
		this.batchJobIPAddress = batchJobIPAddress;
	}

	public String getBatchJobCorrelationId() {
		return batchJobCorrelationId;
	}

	public void setBatchJobCorrelationId(String batchJobCorrelationId) {
		this.batchJobCorrelationId = batchJobCorrelationId;
	}

	@Override
	public String toString() {
		return "BatchJobInfo{" +
				"batchJobEnvironmentType='" + batchJobEnvironmentType + '\'' +
				", batchJobApplicationName='" + batchJobApplicationName + '\'' +
				", batchJobName='" + batchJobName + '\'' +
				", batchJobServerName='" + batchJobServerName + '\'' +
				", batchJobIPAddress='" + batchJobIPAddress + '\'' +
				", batchJobCorrelationId='" + batchJobCorrelationId + '\'' +
				'}';
	}
}
