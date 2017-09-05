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

	public void setBatchFileName(String batchFileName) {
		this.batchFileName = batchFileName;
	}

	public String getBatchFileCreationDateTime() {
		return batchFileCreationDateTime;
	}

	public void setBatchFileCreationDateTime(String batchFileCreationDateTime) {
		this.batchFileCreationDateTime = batchFileCreationDateTime;
	}

	public String getBatchFileCorrelationId() {
		return batchFileCorrelationId;
	}

	public void setBatchFileCorrelationId(String batchFileCorrelationId) {
		this.batchFileCorrelationId = batchFileCorrelationId;
	}

	public String getBatchFileLineItemCount() {
		return batchFileLineItemCount;
	}

	public void setBatchFileLineItemCount(String batchFileLineItemCount) {
		this.batchFileLineItemCount = batchFileLineItemCount;
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
}
