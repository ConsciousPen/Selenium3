package aaa.helpers.openl.model.pup;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.ExcelTableElement;
import aaa.utils.excel.bind.ExcelTransient;

public class PUPOpenLFile extends OpenLFile<PUPOpenLPolicy> {
	@ExcelTransient
	public static final int PUP_REC_EQUIPMENT_INFO_HEADER_ROW_NUMBER = 3;

	@ExcelTransient
	public static final int PUP_RISK_ITEM_HEADER_ROW_NUMBER = 3;

	@ExcelTransient
	public static final String PUP_POLICY_SHEET_NAME = "Batch- PupPolicy";

	@ExcelTransient
	public static final String PUP_DWELLING_SHEET_NAME = "Batch- PupDwelling";

	@ExcelTransient
	public static final String PUP_REC_EQUIPMENT_INFO_SHEET_NAME = "Batch- PupRecEquipmentInfo";

	@ExcelTransient
	public static final String PUP_RISK_ITEM_SHEET_NAME = "Batch- PupRiskItem";

	@ExcelTransient
	public static final String PUP_COVERAGE_SHEET_NAME = "Batch- PupCoverage";

	@ExcelTransient
	public static final String PUP_ADDRESS_SHEET_NAME = "Batch- PupAddress";

	@ExcelTableElement(sheetName = PUP_POLICY_SHEET_NAME, headerRowNumber = POLICY_HEADER_ROW_NUMBER)
	private List<PUPOpenLPolicy> policies;

	@ExcelTransient
	@ExcelTableElement(sheetName = PUP_DWELLING_SHEET_NAME, headerRowNumber = DWELLING_HEADER_ROW_NUMBER)
	private List<OpenLDwelling> dwelling;

	@ExcelTransient
	@ExcelTableElement(sheetName = PUP_REC_EQUIPMENT_INFO_SHEET_NAME, headerRowNumber = PUP_REC_EQUIPMENT_INFO_HEADER_ROW_NUMBER)
	private List<OpenLRecEquipmentInfo> recEquipmentInfo;

	@ExcelTransient
	@ExcelTableElement(sheetName = PUP_RISK_ITEM_SHEET_NAME, headerRowNumber = PUP_RISK_ITEM_HEADER_ROW_NUMBER)
	private List<OpenLRiskItem> riskItems;

	@ExcelTransient
	@ExcelTableElement(sheetName = PUP_COVERAGE_SHEET_NAME, headerRowNumber = COVERAGE_HEADER_ROW_NUMBER)
	private List<PUPOpenLCoverage> coverages;

	@ExcelTransient
	@ExcelTableElement(sheetName = PUP_ADDRESS_SHEET_NAME, headerRowNumber = ADDRESS_HEADER_ROW_NUMBER)
	private List<PUPOpenLAddress> address;

	public List<OpenLDwelling> getDwelling() {
		return new ArrayList<>(dwelling);
	}

	public void setDwelling(List<OpenLDwelling> dwelling) {
		this.dwelling = new ArrayList<>(dwelling);
	}

	public List<OpenLRecEquipmentInfo> getRecEquipmentInfo() {
		return new ArrayList<>(recEquipmentInfo);
	}

	public void setRecEquipmentInfo(List<OpenLRecEquipmentInfo> recEquipmentInfo) {
		this.recEquipmentInfo = new ArrayList<>(recEquipmentInfo);
	}

	public List<OpenLRiskItem> getRiskItems() {
		return new ArrayList<>(riskItems);
	}

	public void setRiskItems(List<OpenLRiskItem> riskItems) {
		this.riskItems = new ArrayList<>(riskItems);
	}

	public List<PUPOpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<PUPOpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	public List<PUPOpenLAddress> getAddress() {
		return new ArrayList<>(address);
	}

	public void setAddress(List<PUPOpenLAddress> address) {
		this.address = new ArrayList<>(address);
	}

	@Override
	public List<PUPOpenLPolicy> getPolicies() {
		return new ArrayList<>(policies);
	}

	public void setPolicies(List<PUPOpenLPolicy> policies) {
		this.policies = new ArrayList<>(policies);
	}
}
