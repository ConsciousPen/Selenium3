package aaa.main.enums;

public enum CoverageLimits {

	COV_25("25", "$25"),
	COV_900("900", "$900"),
	COV_1500("1500", "$1,500"),
	COV_2000("2000", "$2,000"),
	COV_4500("4500", "$4,500"),
	COV_10000("10000", "$10,000"),
	COV_20000("20000", "$20,000"),
	COV_25000("25000", "$25,000"),
	COV_30000("30000", "$30,000"),
	COV_40000("40000", "$40,000"),
	COV_0("0", "No Coverage"),

	DED_0("0", "$0"),
	DED_250("250", "$250"),
	DED_500("500", "$500"),
	DED_1000("1000", "$1,000");

	private final String limit;
	private final String display;

	CoverageLimits(String limit, String display) {
		this.limit = limit;
		this.display = display;
	}

	public String getLimit() {
		return limit;
	}

	public String getDisplay() {
		return display;
	}
}
