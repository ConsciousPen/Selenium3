package aaa.helpers.xml.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class BatchFileInfo {
	@XmlElement
	String batchFileName;

	@XmlElement
	String batchFileCreationDateTime;

	@XmlElement
	String batchFileCorrelationId;

	@XmlElement
	String batchFileLineItemCount;

	public String getBatchFileName() {
		return batchFileName;
	}
	public String getBatchFileCreationDateTime() {
		return batchFileCreationDateTime;
	}

	public String getBatchFileCorrelationId() {
		return batchFileCorrelationId;
	}

	public String getBatchFileLineItemCount() {
		return batchFileLineItemCount;
	}

	public BatchFileInfo setBatchFileName(String batchFileName) {
		this.batchFileName = batchFileName;
		return this;
	}

	public BatchFileInfo setBatchFileCreationDateTime(String batchFileCreationDateTime) {
		this.batchFileCreationDateTime = batchFileCreationDateTime;
		return this;
	}

	public BatchFileInfo setBatchFileCorrelationId(String batchFileCorrelationId) {
		this.batchFileCorrelationId = batchFileCorrelationId;
		return this;
	}

	public BatchFileInfo setBatchFileLineItemCount(String batchFileLineItemCount) {
		this.batchFileLineItemCount = batchFileLineItemCount;
		return this;
	}

	@Override
	public String toString() {
		return "BatchFileInfo{" +
				"batchFileName='" + batchFileName + '\'' +
				", batchFileCreationDateTime='" + batchFileCreationDateTime + '\'' +
				", batchFileCorrelationId='" + batchFileCorrelationId + '\'' +
				", batchFileLineItemCount='" + batchFileLineItemCount + '\'' +
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

		BatchFileInfo that = (BatchFileInfo) o;

		if (batchFileName != null ? !batchFileName.equals(that.batchFileName) : that.batchFileName != null) {
			return false;
		}
		if (batchFileCreationDateTime != null ? !batchFileCreationDateTime.equals(that.batchFileCreationDateTime) : that.batchFileCreationDateTime != null) {
			return false;
		}
		if (batchFileCorrelationId != null ? !batchFileCorrelationId.equals(that.batchFileCorrelationId) : that.batchFileCorrelationId != null) {
			return false;
		}
		return batchFileLineItemCount != null ? batchFileLineItemCount.equals(that.batchFileLineItemCount) : that.batchFileLineItemCount == null;
	}

	@Override
	public int hashCode() {
		int result = batchFileName != null ? batchFileName.hashCode() : 0;
		result = 31 * result + (batchFileCreationDateTime != null ? batchFileCreationDateTime.hashCode() : 0);
		result = 31 * result + (batchFileCorrelationId != null ? batchFileCorrelationId.hashCode() : 0);
		result = 31 * result + (batchFileLineItemCount != null ? batchFileLineItemCount.hashCode() : 0);
		return result;
	}
}
