package aaa.helpers.openl.model.pup;

import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = PUPOpenLFile.PUP_REC_EQUIPMENT_INFO_SHEET_NAME, headerRowIndex = PUPOpenLFile.PUP_REC_EQUIPMENT_INFO_HEADER_ROW_NUMBER)
public class OpenLRecEquipmentInfo {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	private Integer number;

	private Boolean divingBoardInd;
	private Boolean poolInd;
	private Boolean slideInd;
	private Boolean spaInd;
	private Boolean trampolineInd;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Boolean getDivingBoardInd() {
		return divingBoardInd;
	}

	public void setDivingBoardInd(Boolean divingBoardInd) {
		this.divingBoardInd = divingBoardInd;
	}

	public Boolean getPoolInd() {
		return poolInd;
	}

	public void setPoolInd(Boolean poolInd) {
		this.poolInd = poolInd;
	}

	public Boolean getSlideInd() {
		return slideInd;
	}

	public void setSlideInd(Boolean slideInd) {
		this.slideInd = slideInd;
	}

	public Boolean getSpaInd() {
		return spaInd;
	}

	public void setSpaInd(Boolean spaInd) {
		this.spaInd = spaInd;
	}

	public Boolean getTrampolineInd() {
		return trampolineInd;
	}

	public void setTrampolineInd(Boolean trampolineInd) {
		this.trampolineInd = trampolineInd;
	}
}
