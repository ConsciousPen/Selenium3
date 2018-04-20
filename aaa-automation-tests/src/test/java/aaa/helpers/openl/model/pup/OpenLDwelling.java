package aaa.helpers.openl.model.pup;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = PUPOpenLFile.PUP_DWELLING_SHEET_NAME, headerRowIndex = PUPOpenLFile.DWELLING_HEADER_ROW_NUMBER)
public class OpenLDwelling {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	private Integer number;

	private List<PUPOpenLAddress> address;
	private List<OpenLRecEquipmentInfo> recEquipmentInfo;

	private Integer firearmsCount;
	private Boolean retirementCommunityInd;
	private Integer viciousDogCount;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public List<PUPOpenLAddress> getAddress() {
		return new ArrayList<>(address);
	}

	public void setAddress(List<PUPOpenLAddress> address) {
		this.address = new ArrayList<>(address);
	}

	public List<OpenLRecEquipmentInfo> getRecEquipmentInfo() {
		return new ArrayList<>(recEquipmentInfo);
	}

	public void setRecEquipmentInfo(List<OpenLRecEquipmentInfo> recEquipmentInfo) {
		this.recEquipmentInfo = new ArrayList<>(recEquipmentInfo);
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

	@Override
	public String toString() {
		return "PUPOpenLDwelling{" +
				"number=" + number +
				", address=" + address +
				", recEquipmentInfo=" + recEquipmentInfo +
				", firearmsCount=" + firearmsCount +
				", retirementCommunityInd=" + retirementCommunityInd +
				", viciousDogCount=" + viciousDogCount +
				'}';
	}
}
