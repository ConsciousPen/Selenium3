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


	public String getBatchJobApplicationName() {
		return batchJobApplicationName;
	}


	public String getBatchJobName() {
		return batchJobName;
	}


	public String getBatchJobServerName() {
		return batchJobServerName;
	}


	public String getBatchJobIPAddress() {
		return batchJobIPAddress;
	}


	public String getBatchJobCorrelationId() {
		return batchJobCorrelationId;
	}

	public BatchJobInfo setBatchJobEnvironmentType(String batchJobEnvironmentType) {
		this.batchJobEnvironmentType = batchJobEnvironmentType;
		return this;
	}

	public BatchJobInfo setBatchJobApplicationName(String batchJobApplicationName) {
		this.batchJobApplicationName = batchJobApplicationName;
		return this;
	}

	public BatchJobInfo setBatchJobName(String batchJobName) {
		this.batchJobName = batchJobName;
		return this;
	}

	public BatchJobInfo setBatchJobServerName(String batchJobServerName) {
		this.batchJobServerName = batchJobServerName;
		return this;
	}

	public BatchJobInfo setBatchJobIPAddress(String batchJobIPAddress) {
		this.batchJobIPAddress = batchJobIPAddress;
		return this;
	}

	public BatchJobInfo setBatchJobCorrelationId(String batchJobCorrelationId) {
		this.batchJobCorrelationId = batchJobCorrelationId;
		return this;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		BatchJobInfo that = (BatchJobInfo) o;

		if (batchJobEnvironmentType != null ? !batchJobEnvironmentType.equals(that.batchJobEnvironmentType) : that.batchJobEnvironmentType != null) {
			return false;
		}
		if (batchJobApplicationName != null ? !batchJobApplicationName.equals(that.batchJobApplicationName) : that.batchJobApplicationName != null) {
			return false;
		}
		if (batchJobName != null ? !batchJobName.equals(that.batchJobName) : that.batchJobName != null) {
			return false;
		}
		if (batchJobServerName != null ? !batchJobServerName.equals(that.batchJobServerName) : that.batchJobServerName != null) {
			return false;
		}
		if (batchJobIPAddress != null ? !batchJobIPAddress.equals(that.batchJobIPAddress) : that.batchJobIPAddress != null) {
			return false;
		}
		return batchJobCorrelationId != null ? batchJobCorrelationId.equals(that.batchJobCorrelationId) : that.batchJobCorrelationId == null;
	}

	@Override
	public int hashCode() {
		int result = batchJobEnvironmentType != null ? batchJobEnvironmentType.hashCode() : 0;
		result = 31 * result + (batchJobApplicationName != null ? batchJobApplicationName.hashCode() : 0);
		result = 31 * result + (batchJobName != null ? batchJobName.hashCode() : 0);
		result = 31 * result + (batchJobServerName != null ? batchJobServerName.hashCode() : 0);
		result = 31 * result + (batchJobIPAddress != null ? batchJobIPAddress.hashCode() : 0);
		result = 31 * result + (batchJobCorrelationId != null ? batchJobCorrelationId.hashCode() : 0);
		return result;
	}
}
