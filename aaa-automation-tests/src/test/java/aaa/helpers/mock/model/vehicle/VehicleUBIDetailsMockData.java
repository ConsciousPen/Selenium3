package aaa.helpers.mock.model.vehicle;

import java.util.List;
import aaa.utils.excel.bind.ExcelTableElement;

public class VehicleUBIDetailsMockData {
	@ExcelTableElement(sheetName = "VEHICLE_UBI_DETAILS_REQUEST")
	private List<VehicleUbiDetailsRequest> vehicleUbiDetailsRequests;

	@ExcelTableElement(sheetName = "VEHICLE_UBI_DETAILS_RESPONSE")
	private List<VehicleUbiDetailsResponse> vehicleUbiDetailsResponses;
}
