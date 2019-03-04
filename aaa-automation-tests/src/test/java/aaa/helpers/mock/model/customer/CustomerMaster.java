package aaa.helpers.mock.model.customer;

import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = "POLICIES")
public class CustomerMaster {
	@ExcelColumnElement(name = "ID")
	private String id;

	@ExcelColumnElement(name = "productCode")
	private String productCode;

	@ExcelColumnElement(name = "productType")
	private String productType;

	@ExcelColumnElement(name = "termStartDate")
	private String termStartDate;

	@ExcelColumnElement(name = "termEndDate")
	private String termEndDate;

	@ExcelColumnElement(name = "policyType")
	private String policyType;

	@ExcelColumnElement(name = "policyNumber")
	private String policyNumber;

	@ExcelColumnElement(name = "policyStatus")
	private String policyStatus;

	@ExcelColumnElement(name = "insurerName")
	private String insurerName;

	@ExcelColumnElement(name = "sourceSystem")
	private String sourceSystem;

	@ExcelColumnElement(name = "rolesPlayed")
	private String rolesPlayed;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getTermStartDate() {
		return termStartDate;
	}

	public void setTermStartDate(String termStartDate) {
		this.termStartDate = termStartDate;
	}

	public String getTermEndDate() {
		return termEndDate;
	}

	public void setTermEndDate(String termEndDate) {
		this.termEndDate = termEndDate;
	}

	public String getPolicyType() {
		return policyType;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public String getPolicyStatus() {
		return policyStatus;
	}

	public void setPolicyStatus(String policyStatus) {
		this.policyStatus = policyStatus;
	}

	public String getInsurerName() {
		return insurerName;
	}

	public void setInsurerName(String insurerName) {
		this.insurerName = insurerName;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getRolesPlayed() {
		return rolesPlayed;
	}

	public void setRolesPlayed(String rolesPlayed) {
		this.rolesPlayed = rolesPlayed;
	}

	@Override
	public String toString() {
		return "CustomerMaster{" +
				"id='" + id + '\'' +
				", productCode='" + productCode + '\'' +
				", productType='" + productType + '\'' +
				", termStartDate='" + termStartDate + '\'' +
				", termEndDate='" + termEndDate + '\'' +
				", policyType='" + policyType + '\'' +
				", policyNumber='" + policyNumber + '\'' +
				", policyStatus='" + policyStatus + '\'' +
				", insurerName='" + insurerName + '\'' +
				", sourceSystem='" + sourceSystem + '\'' +
				", rolesPlayed='" + rolesPlayed + '\'' +
				'}';
	}
}
