package aaa.main.enums;

public enum CoverageLimits {
	COV_20("20", "$20"),
	COV_25("25", "$25"),
	COV_30("30", "$30"),
	COV_250("250", "$250"),
	COV_900("900", "$900"),
	COV_1500("1500", "$1,500"),
	COV_2000("2000", "$2,000"),
	COV_2500("2500", "$2,500"),
	COV_3000("3000", "$3,000"),
	COV_3500("3500", "$3,500"),
	COV_4000("4000", "$4,000"),
	COV_4500("4500", "$4,500"),
	COV_5000("5000", "$5,000"),
	COV_UP_TO_5000("5000", "Up to $5,000"),
	COV_10000("10000", "$10,000"),
	COV_12000("12000", "$12,000"),
	COV_15000("15000", "$15,000"),
	COV_20000("20000", "$20,000"),
	COV_24000("24000", "$24,000"),
	COV_25000("25000", "$25,000"),
	COV_30000("30000", "$30,000"),
	COV_40000("40000", "$40,000"),
	COV_50000("50000", "$50,000"),
	COV_100000("100000", "$100,000"),
	COV_177500("177500", "$177,500"),
	COV_300000("300000", "$300,000"),
	COV_500000("500000", "$500,000"),
	COV_1000000("1000000", "$1,000,000"),
	COV_1530("15000/30000", "$15,000/$30,000"),
	COV_1020("10000/20000", "$10,000/$20,000"),
	COV_2550("25000/50000", "$25,000/$50,000"),
	COV_2565("25000/65000", "$25,000/$65,000"),
	COV_3060("30000/60000", "$30,000/$60,000"),
	COV_4080("40000/80000", "$40,000/$80,000"),
	COV_50100("50000/100000", "$50,000/$100,000"),
	COV_100200("100000/200000", "$100,000/$200,000"),
	COV_100300("100000/300000", "$100,000/$300,000"),
	COV_200600("200000/600000", "$200,000/$600,000"),
	COV_250500("250000/500000", "$250,000/$500,000"),
	COV_300500("300000/500000", "$300,000/$500,000"),
	COV_500500("500000/500000", "$500,000/$500,000"),
	COV_5001000("500000/1000000", "$500,000/$1,000,000"),
	COV_6001000("600000/1000000", "$600,000/$1,000,000"),
	COV_10001000("1000000/1000000", "$1,000,000/$1,000,000"),
	COV_10002000("1000000/2000000", "$1,000,000/$2,000,000"),
	COV_20002000("2000000/2000000", "$2,000,000/$2,000,000"),
	COV_INCLUDED("Included"),
	COV_0("0", "No Coverage"),
	COV_00("0/0", "No Coverage"),
	COV_1("1", "Yes"),
	COV_NO_COV("-1", "No Coverage"),
	COV_TRUE("true", "Yes"),
	COV_FALSE("false", "No"),
	COV_FALSE_NO_COVERAGE("false", "No Coverage"),
	COV_CDW("1", "Collision Deductible Waiver"),
	DED_0("0", "$0"),
	DED_250("250", "$250"),
	DED_500("500", "$500"),
	DED_1000("1000", "$1,000"),
	DED_3500("3500", "$3500/$250 Ded"),
	DED_10000("10000", "$10,000"),
	COV_750("750", "$750"),
	COV_500("500", "$500"),
	COV_50("50", "$50"),
	COV_100("100", "$100"),
	COV_1000("1000", "$1,000"),
	COV_SP_1000("1000", "$1,000.00"),
	COV_30_900("30/900", "$30/$900"),
	COV_40_1200("40/1200", "$40/$1,200"),
	COV_50_1500("50/1500", "$50/$1,500"),
	COV_50_300("50/300", "$50/$300"),
	COV_PIPDEDAPPTO_NIAHF("F", "NI and Household Family"),
	COV_PIPDEDAPPTO_NIO("P", "Named Insured Only"),
	COV_FULL_TORT("TRUE", "Full Tort"),
	COV_LIMITED_TORT("FALSE", "Limited Tort"),
	COV_FPB_5K_BASIC("Basic", "$5K Basic Coverage Package"),
	COV_FPB_50K_TOTAL("Combo1", "$50K Total Coverage Package"),
	COV_FPB_100K_TOTAL("Combo2", "$100K Total Coverage Package"),
	COV_FPB_177_5K_TOTAL("Combo3", "$177.5K Total Coverage Package"),
	COV_FPB_ADDED("Added", "Added"),
	COV_IL_5000_MAX_PA("1000/5000", "$1,000/month ($5,000 max)"),
	COV_IL_15000_MAX_PA("1000/15000", "$1,000/month ($15,000 max)"),
	COV_IL_25000_MAX_PA("1500/25000", "$1,500/month ($25,000 max)"),
	COV_IL_50000_MAX_PA("2500/50000", "$2,500/month ($50,000 max)"),
	SP_EQ_0("0", "$0.00"),
	COV_EMB_0("0", "No"),
	COV_EMB_1000("1000", "Yes");

	private final String limit;
	private final String display;

	CoverageLimits(String limit, String display) {
		this.limit = limit;
		this.display = display;
	}

	CoverageLimits(String display) {
		this.limit = null;
		this.display = display;
	}

	public String getLimit() {
		return limit;
	}

	public String getDisplay() {
		return display;
	}
}
