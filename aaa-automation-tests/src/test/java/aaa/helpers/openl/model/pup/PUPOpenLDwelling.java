package aaa.helpers.openl.model.pup;

import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = PUPOpenLFile.PUP_DWELLING_SHEET_NAME, headerRowIndex = PUPOpenLFile.DWELLING_HEADER_ROW_NUMBER)
public class PUPOpenLDwelling {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	@RequiredField
	private Integer number;

	@RequiredField
	private PUPOpenLAddress address;

	@RequiredField
	private PUPOpenLRecEquipmentInfo recEquipmentInfo;

	private Integer firearmsCount;
	private Boolean retirementCommunityInd;
	private Integer viciousDogCount;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public PUPOpenLAddress getAddress() {
		return address;
	}

	public void setAddress(PUPOpenLAddress address) {
		this.address = address;
	}

	public PUPOpenLRecEquipmentInfo getRecEquipmentInfo() {
		return recEquipmentInfo;
	}

	public void setRecEquipmentInfo(PUPOpenLRecEquipmentInfo recEquipmentInfo) {
		this.recEquipmentInfo = recEquipmentInfo;
	}

	public Integer getFirearmsCount() {
		return firearmsCount;
	}

	public void setFirearmsCount(Integer firearmsCount) {
		this.firearmsCount = firearmsCount;
	}

	public Boolean getRetirementCommunityInd() {
		return retirementCommunityInd;
	}

	public void setRetirementCommunityInd(Boolean retirementCommunityInd) {
		this.retirementCommunityInd = retirementCommunityInd;
	}

	public Integer getViciousDogCount() {
		return viciousDogCount;
	}

	public void setViciousDogCount(Integer viciousDogCount) {
		this.viciousDogCount = viciousDogCount;
	}
}
