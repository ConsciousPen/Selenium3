package aaa.helpers.mock.model.vehicle;

import java.time.LocalDateTime;
import aaa.utils.excel.bind.ExcelTableColumnElement;
import aaa.utils.excel.bind.ExcelTransient;

public class VehicleUbiDetailsResponse {
	@ExcelTransient
	private static final String DATE_PATTERN = "dd-MM-yy";

	@ExcelTableColumnElement(name = "ID")
	private String id;

	@ExcelTableColumnElement(dateFormatPatterns = DATE_PATTERN)
	private LocalDateTime ubiDeviceStatusDate;

	@ExcelTableColumnElement(dateFormatPatterns = DATE_PATTERN)
	private LocalDateTime ubiSafetyScoreDate;

	private String ubiEligibilityStatus;
	private String ubiDeviceStatus;
	private Integer ubiVoucherNumber;
	private String ubiSafetyScore;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDateTime getUbiDeviceStatusDate() {
		return ubiDeviceStatusDate;
	}

	public void setUbiDeviceStatusDate(LocalDateTime ubiDeviceStatusDate) {
		this.ubiDeviceStatusDate = ubiDeviceStatusDate;
	}

	public LocalDateTime getUbiSafetyScoreDate() {
		return ubiSafetyScoreDate;
	}

	public void setUbiSafetyScoreDate(LocalDateTime ubiSafetyScoreDate) {
		this.ubiSafetyScoreDate = ubiSafetyScoreDate;
	}

	public String getUbiEligibilityStatus() {
		return ubiEligibilityStatus;
	}

	public void setUbiEligibilityStatus(String ubiEligibilityStatus) {
		this.ubiEligibilityStatus = ubiEligibilityStatus;
	}

	public String getUbiDeviceStatus() {
		return ubiDeviceStatus;
	}

	public void setUbiDeviceStatus(String ubiDeviceStatus) {
		this.ubiDeviceStatus = ubiDeviceStatus;
	}

	public Integer getUbiVoucherNumber() {
		return ubiVoucherNumber;
	}

	public void setUbiVoucherNumber(Integer ubiVoucherNumber) {
		this.ubiVoucherNumber = ubiVoucherNumber;
	}

	public String getUbiSafetyScore() {
		return ubiSafetyScore;
	}

	public void setUbiSafetyScore(String ubiSafetyScore) {
		this.ubiSafetyScore = ubiSafetyScore;
	}

	@Override
	public String toString() {
		return "VehicleUbiDetailsResponse{" +
				"id='" + id + '\'' +
				", ubiDeviceStatusDate=" + ubiDeviceStatusDate +
				", ubiSafetyScoreDate=" + ubiSafetyScoreDate +
				", ubiEligibilityStatus='" + ubiEligibilityStatus + '\'' +
				", ubiDeviceStatus='" + ubiDeviceStatus + '\'' +
				", ubiVoucherNumber=" + ubiVoucherNumber +
				", ubiSafetyScore='" + ubiSafetyScore + '\'' +
				'}';
	}
}
