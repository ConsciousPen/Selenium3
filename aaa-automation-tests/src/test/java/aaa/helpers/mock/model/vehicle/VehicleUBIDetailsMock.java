package aaa.helpers.mock.model.vehicle;

import java.util.List;
import aaa.helpers.mock.model.UpdatableMock;

public class VehicleUBIDetailsMock implements UpdatableMock {
	private List<VehicleUbiDetailsRequest> vehicleUbiDetailsRequests;
	private List<VehicleUbiDetailsResponse> vehicleUbiDetailsResponses;

	@Override
	public boolean update(UpdatableMock mock) {
		//TODO-dchubkov: to be implemented...
		return false;
	}
}
