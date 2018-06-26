package aaa.helpers.mock.model.property_classification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import aaa.helpers.mock.model.AbstractMock;

public class RetrievePropertyClassificationMock extends AbstractMock {
	private List<FirelineRequest> firelineRequests;
	private List<FirelineResponse> firelineResponses;
	private List<PPCREequest> ppcRequests;
	private List<PPCresponse> ppcResponses;

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

	public List<PPCresponse> getPpcResponses() {
		return Collections.unmodifiableList(ppcResponses);
	}

	public void setPpcResponses(List<PPCresponse> ppcResponses) {
		this.ppcResponses = new ArrayList<>(ppcResponses);
	}
}
