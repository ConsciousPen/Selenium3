package aaa.helpers.openl.model.home_ss;

import java.time.LocalDate;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.CLAIM_SHEET_NAME, headerRowIndex = OpenLFile.CLAIM_HEADER_ROW_NUMBER)
public class OpenLClaim {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	private Integer number;

	private String causeLoss;
	private Double claimAmount;
	private Boolean isAAAClaim;
	private LocalDate lossDate;
	private Boolean isClaimRatable;
	private Boolean isClaimWithinThreeYrs;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getCauseLoss() {
		return causeLoss;
	}

	public void setCauseLoss(String causeLoss) {
		this.causeLoss = causeLoss;
	}

	public Double getClaimAmount() {
		return claimAmount;
	}

	public void setClaimAmount(Double claimAmount) {
		this.claimAmount = claimAmount;
	}

	public Boolean isAAAClaim() {
		return isAAAClaim;
	}

	public void setAAAClaim(Boolean isAAAClaim) {
		this.isAAAClaim = isAAAClaim;
	}

	public LocalDate getLossDate() {
		return lossDate;
	}

	public void setLossDate(LocalDate lossDate) {
		this.lossDate = lossDate;
	}

	public Boolean isClaimRatable() {
		return isClaimRatable;
	}

	public void setClaimRatable(Boolean isClaimRatable) {
		this.isClaimRatable = isClaimRatable;
	}

	public Boolean isClaimWithinThreeYrs() {
		return isClaimWithinThreeYrs;
	}

	public void setClaimWithinThreeYrs(Boolean isClaimWithinThreeYrs) {
		this.isClaimWithinThreeYrs = isClaimWithinThreeYrs;
	}
}
