package aaa.helpers.openl.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public class OpenLTest {
	@ExcelTransient
	public static final String TOTAL_PREMIUM_COLUMN_NAME = "Total Premium";

	@ExcelTableColumnElement(isPrimaryKey = true)
	private Integer policy;

	//@ExcelTableColumnElement(listByContains = "_res_.$Value") //TODO-dchubkov: to be done...
	@ExcelTransient
	private List<Integer> premiums;

	@ExcelTableColumnElement(name = TOTAL_PREMIUM_COLUMN_NAME)
	private Integer totalPremium;

	public Integer getPolicy() {
		return this.policy;
	}

	public void setPolicy(int policy) {
		this.policy = policy;
	}

	public List<Integer> getPremiums() {
		return new ArrayList<>(this.premiums);
	}

	public void setPremiums(List<Integer> premiums) {
		this.premiums = new ArrayList<>(premiums);
	}

	public Integer getTotalPremium() {
		return this.totalPremium != null ? this.totalPremium : getPremiums().stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
	}

	public void setTotalPremium(int totalPremium) {
		this.totalPremium = totalPremium;
	}

	@Override
	public String toString() {
		return "OpenLTest{" +
				"policy=" + this.policy +
				", premiums=" + this.premiums +
				", totalPremium=" + this.totalPremium +
				'}';
	}
}
