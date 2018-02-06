package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.model.OpenLCoverage;

public class HomeSSOpenLCoverage extends OpenLCoverage {
	private Integer id;
	private String code;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "HomeSSOpenLCoverage{" +
				"id=" + id +
				", code='" + code + '\'' +
				", number=" + number +
				", limit='" + limit + '\'' +
				'}';
	}
}
