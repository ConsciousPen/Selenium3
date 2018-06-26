package aaa.helpers.mock.model.vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import aaa.helpers.mock.model.AbstractMock;

public class VehicleUBIDetailsMock extends AbstractMock {
	private List<VehicleUbiDetailsRequest> vehicleUbiDetailsRequests;
	private List<VehicleUbiDetailsResponse> vehicleUbiDetailsResponses;

	public List<VehicleUbiDetailsRequest> getVehicleUbiDetailsRequests() {
		return Collections.unmodifiableList(vehicleUbiDetailsRequests);
	}

	public void setVehicleUbiDetailsRequests(List<VehicleUbiDetailsRequest> vehicleUbiDetailsRequests) {
		this.vehicleUbiDetailsRequests = new ArrayList<>(vehicleUbiDetailsRequests);
	}

	public List<VehicleUbiDetailsResponse> getVehicleUbiDetailsResponses() {
		return Collections.unmodifiableList(vehicleUbiDetailsResponses);
	}

	public void setVehicleUbiDetailsResponses(List<VehicleUbiDetailsResponse> vehicleUbiDetailsResponses) {
		this.vehicleUbiDetailsResponses = new ArrayList<>(vehicleUbiDetailsResponses);
	}
}
