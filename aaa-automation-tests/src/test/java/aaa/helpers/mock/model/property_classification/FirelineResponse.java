package aaa.helpers.mock.model.property_classification;

import aaa.utils.excel.bind.annotation.ExcelColumnElement;

public class FirelineResponse {
	@ExcelColumnElement(name = "ID")
	private String id;

	private Integer responseCode;
	private String responseDescription;
	private String returnCode;
	private String returnMessage;
	private String returnSource;
	private String reportName;
	private String globeXSatMapURL;
	private String fireLineRingMapURL;
	private String fips;
	private String countyName;
	private String matchType;
	private String accessType;
	private Integer adjFuelRating;
	private String wfRiskAssessment;
	private String gaugeMapURL;
	private String geocodeSrc;
	private String fuelType;
	private Double fuelPercentage;
	private String slopeType;
	private Double slopePercentage;
	private Integer adjRateScoreType1;
	private Double adjRateScorePercentage;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseDescription() {
		return responseDescription;
	}

	public void setResponseDescription(String responseDescription) {
		this.responseDescription = responseDescription;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

	public String getReturnSource() {
		return returnSource;
	}

	public void setReturnSource(String returnSource) {
		this.returnSource = returnSource;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getGlobeXSatMapURL() {
		return globeXSatMapURL;
	}

	public void setGlobeXSatMapURL(String globeXSatMapURL) {
		this.globeXSatMapURL = globeXSatMapURL;
	}

	public String getFireLineRingMapURL() {
		return fireLineRingMapURL;
	}

	public void setFireLineRingMapURL(String fireLineRingMapURL) {
		this.fireLineRingMapURL = fireLineRingMapURL;
	}

	public String getFips() {
		return fips;
	}

	public void setFips(String fips) {
		this.fips = fips;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public String getMatchType() {
		return matchType;
	}

	public void setMatchType(String matchType) {
		this.matchType = matchType;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public Integer getAdjFuelRating() {
		return adjFuelRating;
	}

	public void setAdjFuelRating(Integer adjFuelRating) {
		this.adjFuelRating = adjFuelRating;
	}

	public String getWfRiskAssessment() {
		return wfRiskAssessment;
	}

	public void setWfRiskAssessment(String wfRiskAssessment) {
		this.wfRiskAssessment = wfRiskAssessment;
	}

	public String getGaugeMapURL() {
		return gaugeMapURL;
	}

	public void setGaugeMapURL(String gaugeMapURL) {
		this.gaugeMapURL = gaugeMapURL;
	}

	public String getGeocodeSrc() {
		return geocodeSrc;
	}

	public void setGeocodeSrc(String geocodeSrc) {
		this.geocodeSrc = geocodeSrc;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	public Double getFuelPercentage() {
		return fuelPercentage;
	}

	public void setFuelPercentage(Double fuelPercentage) {
		this.fuelPercentage = fuelPercentage;
	}

	public String getSlopeType() {
		return slopeType;
	}

	public void setSlopeType(String slopeType) {
		this.slopeType = slopeType;
	}

	public Double getSlopePercentage() {
		return slopePercentage;
	}

	public void setSlopePercentage(Double slopePercentage) {
		this.slopePercentage = slopePercentage;
	}

	public Integer getAdjRateScoreType1() {
		return adjRateScoreType1;
	}

	public void setAdjRateScoreType1(Integer adjRateScoreType1) {
		this.adjRateScoreType1 = adjRateScoreType1;
	}

	public Double getAdjRateScorePercentage() {
		return adjRateScorePercentage;
	}

	public void setAdjRateScorePercentage(Double adjRateScorePercentage) {
		this.adjRateScorePercentage = adjRateScorePercentage;
	}
}
