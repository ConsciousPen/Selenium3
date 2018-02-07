package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.ExcelTableColumnElement;

public class OpenLLossInformation {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	private Integer number;

	private String autoTier;
	private Integer creditBands;
	private Integer expClaimPoint;
	private Integer priorClaimPoint;
	private Integer recentYCF;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getAutoTier() {
		return autoTier;
	}

	public void setAutoTier(String autoTier) {
		this.autoTier = autoTier;
	}

	public Integer getCreditBands() {
		return creditBands;
	}

	public void setCreditBands(Integer creditBands) {
		this.creditBands = creditBands;
	}

	public Integer getExpClaimPoint() {
		return expClaimPoint;
	}

	public void setExpClaimPoint(Integer expClaimPoint) {
		this.expClaimPoint = expClaimPoint;
	}

	public Integer getPriorClaimPoint() {
		return priorClaimPoint;
	}

	public void setPriorClaimPoint(Integer priorClaimPoint) {
		this.priorClaimPoint = priorClaimPoint;
	}

	public Integer getRecentYCF() {
		return recentYCF;
	}

	public void setRecentYCF(Integer recentYCF) {
		this.recentYCF = recentYCF;
	}

	@Override
	public String toString() {
		return "OpenLLossInformation{" +
				"number=" + number +
				", autoTier='" + autoTier + '\'' +
				", creditBands=" + creditBands +
				", expClaimPoint=" + expClaimPoint +
				", priorClaimPoint=" + priorClaimPoint +
				", recentYCF=" + recentYCF +
				'}';
	}
}
