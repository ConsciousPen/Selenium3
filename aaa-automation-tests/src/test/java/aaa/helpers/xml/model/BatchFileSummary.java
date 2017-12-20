package aaa.helpers.xml.model;

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

	public BatchFileInfo getBatchFileInfo() {
		return batchFileInfo;
	}

	public BatchFileSummary setBatchJobInfo(BatchJobInfo batchJobInfo) {
		this.batchJobInfo = batchJobInfo;
		return this;
	}

	public BatchFileSummary setBatchFileInfo(BatchFileInfo batchFileInfo) {
		this.batchFileInfo = batchFileInfo;
		return this;
	}

	@Override
	public String toString() {
		return "BatchFileSummary{" +
				"batchJobInfo=" + batchJobInfo +
				", batchFileInfo=" + batchFileInfo +
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

		BatchFileSummary that = (BatchFileSummary) o;

		if (batchJobInfo != null ? !batchJobInfo.equals(that.batchJobInfo) : that.batchJobInfo != null) {
			return false;
		}
		return batchFileInfo != null ? batchFileInfo.equals(that.batchFileInfo) : that.batchFileInfo == null;
	}

	@Override
	public int hashCode() {
		int result = batchJobInfo != null ? batchJobInfo.hashCode() : 0;
		result = 31 * result + (batchFileInfo != null ? batchFileInfo.hashCode() : 0);
		return result;
	}
}
