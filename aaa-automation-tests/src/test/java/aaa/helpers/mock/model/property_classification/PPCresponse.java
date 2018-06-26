package aaa.helpers.mock.model.property_classification;

import java.time.LocalDate;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;

public class PPCresponse {
	@ExcelColumnElement(name = "ID")
	private String id;

	private Integer responseCode;
	private String responseDescription;
	private String returnCode;
	private String returnMessage;
	private String returnSource;
	private String reportName;
	private String feReturnSource;
	private Boolean availabilityIndicator;
	private String fePpcValue;
	private LocalDate feEffectiveDate;
	private String ppcValue;
	private String ppcValue1;
	private String ppcValue2;
	private Double ppcPercentage;
	private Double ppcPercentage1;
	private Double ppcPercentage2;
	private String altPPC;
	private String ppcCountyFIPS;
	private String ppcCountyFIPS1;
	private String ppcCountyFIPS2;
	private String ppcCountyName;
	private String ppcCountyName1;
	private String ppcCountyName2;
	private String stateSpecificCode;
	private String stateSpecificCode1;
	private String stateSpecificCode2;
	private String statePlacedIndic;
	private String fireDistrict;
	private String fireDistrict1;
	private String fireDistrict2;
	private String fireSubscriberDistrCode;
	private String fireSubscriberDistrCode1;
	private String fireSubscriberDistrCode2;
	private String fireStationDistBand;
	private String fireStationDistRange;
	private String respFireStation;
	private String waterSupplyCode;
	private String waterSupply;

	@ExcelColumnElement(name = "State")
	private String state;

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

	public String getFeReturnSource() {
		return feReturnSource;
	}

	public void setFeReturnSource(String feReturnSource) {
		this.feReturnSource = feReturnSource;
	}

	public Boolean getAvailabilityIndicator() {
		return availabilityIndicator;
	}

	public void setAvailabilityIndicator(Boolean availabilityIndicator) {
		this.availabilityIndicator = availabilityIndicator;
	}

	public String getFePpcValue() {
		return fePpcValue;
	}

	public void setFePpcValue(String fePpcValue) {
		this.fePpcValue = fePpcValue;
	}

	public LocalDate getFeEffectiveDate() {
		return feEffectiveDate;
	}

	public void setFeEffectiveDate(LocalDate feEffectiveDate) {
		this.feEffectiveDate = feEffectiveDate;
	}

	public String getPpcValue() {
		return ppcValue;
	}

	public void setPpcValue(String ppcValue) {
		this.ppcValue = ppcValue;
	}

	public String getPpcValue1() {
		return ppcValue1;
	}

	public void setPpcValue1(String ppcValue1) {
		this.ppcValue1 = ppcValue1;
	}

	public String getPpcValue2() {
		return ppcValue2;
	}

	public void setPpcValue2(String ppcValue2) {
		this.ppcValue2 = ppcValue2;
	}

	public Double getPpcPercentage() {
		return ppcPercentage;
	}

	public void setPpcPercentage(Double ppcPercentage) {
		this.ppcPercentage = ppcPercentage;
	}

	public Double getPpcPercentage1() {
		return ppcPercentage1;
	}

	public void setPpcPercentage1(Double ppcPercentage1) {
		this.ppcPercentage1 = ppcPercentage1;
	}

	public Double getPpcPercentage2() {
		return ppcPercentage2;
	}

	public void setPpcPercentage2(Double ppcPercentage2) {
		this.ppcPercentage2 = ppcPercentage2;
	}

	public String getAltPPC() {
		return altPPC;
	}

	public void setAltPPC(String altPPC) {
		this.altPPC = altPPC;
	}

	public String getPpcCountyFIPS() {
		return ppcCountyFIPS;
	}

	public void setPpcCountyFIPS(String ppcCountyFIPS) {
		this.ppcCountyFIPS = ppcCountyFIPS;
	}

	public String getPpcCountyFIPS1() {
		return ppcCountyFIPS1;
	}

	public void setPpcCountyFIPS1(String ppcCountyFIPS1) {
		this.ppcCountyFIPS1 = ppcCountyFIPS1;
	}

	public String getPpcCountyFIPS2() {
		return ppcCountyFIPS2;
	}

	public void setPpcCountyFIPS2(String ppcCountyFIPS2) {
		this.ppcCountyFIPS2 = ppcCountyFIPS2;
	}

	public String getPpcCountyName() {
		return ppcCountyName;
	}

	public void setPpcCountyName(String ppcCountyName) {
		this.ppcCountyName = ppcCountyName;
	}

	public String getPpcCountyName1() {
		return ppcCountyName1;
	}

	public void setPpcCountyName1(String ppcCountyName1) {
		this.ppcCountyName1 = ppcCountyName1;
	}

	public String getPpcCountyName2() {
		return ppcCountyName2;
	}

	public void setPpcCountyName2(String ppcCountyName2) {
		this.ppcCountyName2 = ppcCountyName2;
	}

	public String getStateSpecificCode() {
		return stateSpecificCode;
	}

	public void setStateSpecificCode(String stateSpecificCode) {
		this.stateSpecificCode = stateSpecificCode;
	}

	public String getStateSpecificCode1() {
		return stateSpecificCode1;
	}

	public void setStateSpecificCode1(String stateSpecificCode1) {
		this.stateSpecificCode1 = stateSpecificCode1;
	}

	public String getStateSpecificCode2() {
		return stateSpecificCode2;
	}

	public void setStateSpecificCode2(String stateSpecificCode2) {
		this.stateSpecificCode2 = stateSpecificCode2;
	}

	public String getStatePlacedIndic() {
		return statePlacedIndic;
	}

	public void setStatePlacedIndic(String statePlacedIndic) {
		this.statePlacedIndic = statePlacedIndic;
	}

	public String getFireDistrict() {
		return fireDistrict;
	}

	public void setFireDistrict(String fireDistrict) {
		this.fireDistrict = fireDistrict;
	}

	public String getFireDistrict1() {
		return fireDistrict1;
	}

	public void setFireDistrict1(String fireDistrict1) {
		this.fireDistrict1 = fireDistrict1;
	}

	public String getFireDistrict2() {
		return fireDistrict2;
	}

	public void setFireDistrict2(String fireDistrict2) {
		this.fireDistrict2 = fireDistrict2;
	}

	public String getFireSubscriberDistrCode() {
		return fireSubscriberDistrCode;
	}

	public void setFireSubscriberDistrCode(String fireSubscriberDistrCode) {
		this.fireSubscriberDistrCode = fireSubscriberDistrCode;
	}

	public String getFireSubscriberDistrCode1() {
		return fireSubscriberDistrCode1;
	}

	public void setFireSubscriberDistrCode1(String fireSubscriberDistrCode1) {
		this.fireSubscriberDistrCode1 = fireSubscriberDistrCode1;
	}

	public String getFireSubscriberDistrCode2() {
		return fireSubscriberDistrCode2;
	}

	public void setFireSubscriberDistrCode2(String fireSubscriberDistrCode2) {
		this.fireSubscriberDistrCode2 = fireSubscriberDistrCode2;
	}

	public String getFireStationDistBand() {
		return fireStationDistBand;
	}

	public void setFireStationDistBand(String fireStationDistBand) {
		this.fireStationDistBand = fireStationDistBand;
	}

	public String getFireStationDistRange() {
		return fireStationDistRange;
	}

	public void setFireStationDistRange(String fireStationDistRange) {
		this.fireStationDistRange = fireStationDistRange;
	}

	public String getRespFireStation() {
		return respFireStation;
	}

	public void setRespFireStation(String respFireStation) {
		this.respFireStation = respFireStation;
	}

	public String getWaterSupplyCode() {
		return waterSupplyCode;
	}

	public void setWaterSupplyCode(String waterSupplyCode) {
		this.waterSupplyCode = waterSupplyCode;
	}

	public String getWaterSupply() {
		return waterSupply;
	}

	public void setWaterSupply(String waterSupply) {
		this.waterSupply = waterSupply;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
