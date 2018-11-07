package aaa.main.enums;

public enum CoverageLimits {

	COV_25("25", "$25"),
	COV_900("900", "$900"),
	COV_1500("1500", "$1,500"),
	COV_2000("2000", "$2,000"),
	COV_4500("4500", "$4,500"),
	COV_10000("10000", "$10,000"),
	COV_15000("15000", "$15,000"),
	COV_20000("20000", "$20,000"),
	COV_25000("25000", "$25,000"),
	COV_30000("30000", "$30,000"),
	COV_40000("40000", "$40,000"),
	COV_50000("50000", "$50,000"),
	COV_100000("100000", "$100,000"),
	COV_300000("300000", "$300,000"),
	COV_0("0", "No Coverage"),
	COV_FALSE_NO("false", "No"),
	COV_TRUE_YES("true", "Yes"),
	COV_FALSE_NO_COVERAGE("false", "No Coverage"),
	COV_500000("500000","$500,000"),
	COV_1000000("1000000","$1,000,000"),
	COV_3060("30000/60000", "$30,000/$60,000"),
	COV_50100("50000/100000", "$50,000/$100,000"),
	COV_100300("100000/300000", "$100,000/$300,000"),
	COV_250500("250000/500000", "$250,000/$500,000"),
	COV_300500("300000/500000", "$300,000/$500,000"),
	COV_500500("500000/500000", "$500,000/$500,000"),
	COV_5001000("500000/1000000", "$500,000/$1,000,000"),
	COV_10001000("1000000/1000000", "$1,000,000/$1,000,000"),
		COV_TRUE("true", "Yes"),
	COV_FALSE("false", "No"),

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
