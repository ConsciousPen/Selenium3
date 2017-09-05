package aaa.helpers.xml.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class BatchFileSummary {
	@XmlElement
	private BatchJobInfo batchJobInfo;

	@XmlElement
	private BatchFileInfo batchFileInfo;

	public BatchJobInfo getBatchJobInfo() {
		return batchJobInfo;
	}

	public void setBatchJobInfo(BatchJobInfo batchJobInfo) {
		this.batchJobInfo = batchJobInfo;
	}

	public BatchFileInfo getBatchFileInfo() {
		return batchFileInfo;
	}

	public void setBatchFileInfo(BatchFileInfo batchFileInfo) {
		this.batchFileInfo = batchFileInfo;
	}

	@Override
	public String toString() {
		return "BatchFileSummary{" +
				"batchJobInfo=" + batchJobInfo +
				", batchFileInfo=" + batchFileInfo +
				'}';
	}
}
