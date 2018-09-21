package aaa.helpers.openl.model.pup;

import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = PUPOpenLFile.PUP_DWELLING_SHEET_NAME, headerRowIndex = PUPOpenLFile.DWELLING_HEADER_ROW_NUMBER)
public class OpenLDwelling {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	private Integer number;

	private PUPOpenLAddress address;
	private OpenLRecEquipmentInfo recEquipmentInfo;

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

	public OpenLRecEquipmentInfo getRecEquipmentInfo() {
		return recEquipmentInfo;
	}

	public void setRecEquipmentInfo(OpenLRecEquipmentInfo recEquipmentInfo) {
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
