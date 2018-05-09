package aaa.helpers.mock.model.vehicle;

import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = "VEHICLE_UBI_DETAILS_REQUEST")
public class VehicleUbiDetailsRequest {
	@ExcelColumnElement(name = "ID")
	private String id;
	private String vin;
	private String riskStateCd;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getRiskStateCd() {
		return riskStateCd;
	}

	public void setRiskStateCd(String riskStateCd) {
		this.riskStateCd = riskStateCd;
	}

	@Override
	public String toString() {
		return "VehicleUbiDetailsRequest{" +
				"id='" + id + '\'' +
				", vin='" + vin + '\'' +
				", riskStateCd='" + riskStateCd + '\'' +
				'}';
	}
}
