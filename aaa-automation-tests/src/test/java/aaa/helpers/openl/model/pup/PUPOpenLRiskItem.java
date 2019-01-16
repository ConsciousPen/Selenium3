package aaa.helpers.openl.model.pup;

import java.util.Comparator;
import aaa.helpers.openl.annotation.MatchingField;
import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
@ExcelTableElement(sheetName = PUPOpenLFile.PUP_RISK_ITEM_SHEET_NAME, headerRowIndex = PUPOpenLFile.PUP_RISK_ITEM_HEADER_ROW_NUMBER)
public class PUPOpenLRiskItem implements Comparable<PUPOpenLRiskItem> {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	@RequiredField
	private Integer number;

	private String riskItemCategoryCd;

	@MatchingField
	@RequiredField
	private String riskItemCd;

	@MatchingField
	private Integer riskItemCount;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getRiskItemCategoryCd() {
		return riskItemCategoryCd;
	}

	public void setRiskItemCategoryCd(String riskItemCategoryCd) {
		this.riskItemCategoryCd = riskItemCategoryCd;
	}

	public String getRiskItemCd() {
		return riskItemCd;
	}

	public void setRiskItemCd(String riskItemCd) {
		this.riskItemCd = riskItemCd;
	}

	public Integer getRiskItemCount() {
		return riskItemCount;
	}

	public void setRiskItemCount(Integer riskItemCount) {
		this.riskItemCount = riskItemCount;
	}

	@Override
	public int compareTo(PUPOpenLRiskItem otherRiskItem) {
		return Comparator
				.comparing(PUPOpenLRiskItem::getRiskItemCd, Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(PUPOpenLRiskItem::getRiskItemCount, Comparator.nullsFirst(Comparator.naturalOrder()))
				.compare(this, otherRiskItem);
	}
}
