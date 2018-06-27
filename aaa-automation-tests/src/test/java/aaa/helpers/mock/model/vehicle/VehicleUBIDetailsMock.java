package aaa.helpers.mock.model.vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import aaa.helpers.mock.model.AbstractMock;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public class VehicleUBIDetailsMock extends AbstractMock {
	@ExcelTransient
	public static final String FILE_NAME = "VehicleUBIDetailsMockData.xls";

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

	@Override
	public String getFileName() {
		return FILE_NAME;
	}

	@Override
	public String toString() {
		return "VehicleUBIDetailsMock{" +
				"vehicleUbiDetailsRequests=" + vehicleUbiDetailsRequests +
				", vehicleUbiDetailsResponses=" + vehicleUbiDetailsResponses +
				'}';
	}
}
