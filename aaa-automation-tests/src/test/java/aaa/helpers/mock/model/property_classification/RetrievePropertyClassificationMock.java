package aaa.helpers.mock.model.property_classification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import aaa.helpers.mock.model.AbstractMock;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public class RetrievePropertyClassificationMock extends AbstractMock {
	@ExcelTransient
	public static final String FILE_NAME = "RetrievePropertyClassificationMockData.xls";

	private List<FirelineRequest> firelineRequests;
	private List<FirelineResponse> firelineResponses;
	private List<PPCREequest> ppcRequests;
	private List<PPCResponse> ppcResponses;

	public List<FirelineRequest> getFirelineRequests() {
		return Collections.unmodifiableList(firelineRequests);
	}

	public void setFirelineRequests(List<FirelineRequest> firelineRequests) {
		this.firelineRequests = new ArrayList<>(firelineRequests);
	}

	public List<FirelineResponse> getFirelineResponses() {
		return Collections.unmodifiableList(firelineResponses);
	}

	public void setFirelineResponses(List<FirelineResponse> firelineResponses) {
		this.firelineResponses = new ArrayList<>(firelineResponses);
	}

	public List<PPCREequest> getPpcRequests() {
		return Collections.unmodifiableList(ppcRequests);
	}

	public void setPpcRequests(List<PPCREequest> ppcRequests) {
		this.ppcRequests = new ArrayList<>(ppcRequests);
	}

	public List<PPCResponse> getPpcResponses() {
		return Collections.unmodifiableList(ppcResponses);
	}

	public void setPpcResponses(List<PPCResponse> ppcResponses) {
		this.ppcResponses = new ArrayList<>(ppcResponses);
	}

	@Override
	public String getFileName() {
		return FILE_NAME;
	}

	@Override
	public String toString() {
		return "RetrievePropertyClassificationMock{" +
				"firelineRequests=" + firelineRequests +
				", firelineResponses=" + firelineResponses +
				", ppcRequests=" + ppcRequests +
				", ppcResponses=" + ppcResponses +
				'}';
	}
}
