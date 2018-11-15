package aaa.main.enums;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum AvailableCoverageLimits {

	BPIP_CL(ImmutableList.of(
			CoverageLimits.COV_10000,
			CoverageLimits.COV_0
	)),
	ADDPIP_CL(ImmutableList.of(
			CoverageLimits.COV_10000,
			CoverageLimits.COV_20000,
			CoverageLimits.COV_30000,
			CoverageLimits.COV_40000,
			CoverageLimits.COV_0
	)),
	PIPDED_CL(ImmutableList.of(
			CoverageLimits.DED_0,
			CoverageLimits.DED_250,
			CoverageLimits.DED_500,
			CoverageLimits.DED_1000
	)),
	GPIP_CL(ImmutableList.of(
			CoverageLimits.COV_10000,
			CoverageLimits.COV_0
	)),
	PD_IN(ImmutableList.of(
			CoverageLimits.COV_10000,
			CoverageLimits.COV_15000,
			CoverageLimits.COV_25000,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_100000,
			CoverageLimits.COV_300000
	)),
	UIMPD(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_25000,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_100000,
			CoverageLimits.COV_300000,
			CoverageLimits.COV_500000,
			CoverageLimits.COV_1000000
	)),
	UMPD(ImmutableList.of(
			CoverageLimits.COV_25000,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_100000,
			CoverageLimits.COV_300000,
			CoverageLimits.COV_500000,
			CoverageLimits.COV_1000000
	)),
	MEDEXP_KS(ImmutableList.of(
			CoverageLimits.COV_4500,
			CoverageLimits.COV_10000,
			CoverageLimits.COV_25000
	)),
	UMBI_MD(ImmutableList.of(
			CoverageLimits.COV_3060,
			CoverageLimits.COV_50100,
			CoverageLimits.COV_100300,
			CoverageLimits.COV_250500,
			CoverageLimits.COV_300500,
			CoverageLimits.COV_500500,
			CoverageLimits.COV_5001000,
			CoverageLimits.COV_10001000
	)),
	UMPD_MD(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_15000,
			CoverageLimits.COV_25000,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_100000,
			CoverageLimits.COV_300000,
			CoverageLimits.COV_500000,
			CoverageLimits.COV_1000000
	)),
	UMBI_UT(ImmutableList.of(
			CoverageLimits.COV_00,
			CoverageLimits.COV_2565,
			CoverageLimits.COV_50100,
			CoverageLimits.COV_100300,
			CoverageLimits.COV_250500,
			CoverageLimits.COV_300500,
			CoverageLimits.COV_500500,
			CoverageLimits.COV_5001000,
			CoverageLimits.COV_10001000
	)),
	UIMBI_UT(ImmutableList.of(
			CoverageLimits.COV_00,
			CoverageLimits.COV_1020,
			CoverageLimits.COV_2565,
			CoverageLimits.COV_50100,
			CoverageLimits.COV_100300,
			CoverageLimits.COV_250500,
			CoverageLimits.COV_300500,
			CoverageLimits.COV_500500,
			CoverageLimits.COV_5001000,
			CoverageLimits.COV_10001000
	)),
	UMPD_UT(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.DED_3500
	)),
	EUIM_MD(ImmutableList.of(
			CoverageLimits.COV_TRUE,
			CoverageLimits.COV_FALSE
	)),
	BI(ImmutableList.of(
			CoverageLimits.COV_3060,
			CoverageLimits.COV_50100,
			CoverageLimits.COV_100300,
			CoverageLimits.COV_250500,
			CoverageLimits.COV_300500,
			CoverageLimits.COV_500500,
			CoverageLimits.COV_5001000,
			CoverageLimits.COV_10001000
	)),
	UIMCONV_CT(ImmutableList.of(
			CoverageLimits.COV_FALSE_NO_COVERAGE,
			CoverageLimits.COV_TRUE
	)),

	UMBI_CT(ImmutableList.of(
			CoverageLimits.COV_2550,
			CoverageLimits.COV_4080,
			CoverageLimits.COV_50100,
			CoverageLimits.COV_100200,
			CoverageLimits.COV_100300,
			CoverageLimits.COV_200600,
			CoverageLimits.COV_250500,
			CoverageLimits.COV_300500,
			CoverageLimits.COV_500500,
			CoverageLimits.COV_5001000,
			CoverageLimits.COV_6001000,
			CoverageLimits.COV_10001000,
			CoverageLimits.COV_10002000,
			CoverageLimits.COV_20002000
	)),
	UMBI(ImmutableList.of(
			CoverageLimits.COV_2550,
			CoverageLimits.COV_50100,
			CoverageLimits.COV_100300,
			CoverageLimits.COV_250500,
			CoverageLimits.COV_300500,
			CoverageLimits.COV_500500,
			CoverageLimits.COV_5001000,
			CoverageLimits.COV_10001000

	)),
	UIMBI(ImmutableList.of(
			CoverageLimits.COV_00,
			CoverageLimits.COV_2550,
			CoverageLimits.COV_50100,
			CoverageLimits.COV_100300,
			CoverageLimits.COV_250500,
			CoverageLimits.COV_300500,
			CoverageLimits.COV_500500,
			CoverageLimits.COV_5001000,
			CoverageLimits.COV_10001000
	)),

	BIWV(ImmutableList.of(
			CoverageLimits.COV_2550,
			CoverageLimits.COV_50100,
			CoverageLimits.COV_100300,
			CoverageLimits.COV_250500,
			CoverageLimits.COV_300500,
			CoverageLimits.COV_500500,
			CoverageLimits.COV_5001000,
			CoverageLimits.COV_10001000
	)),
	PD(ImmutableList.of(
			CoverageLimits.COV_25000,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_100000,
			CoverageLimits.COV_300000,
			CoverageLimits.COV_500000
	));

	private final List<CoverageLimits> availableLimits;

	AvailableCoverageLimits(List<CoverageLimits> availableLimits) {
		this.availableLimits = availableLimits;
	}

	public List<CoverageLimits> getAvailableLimits() {
		return availableLimits;
	}

}
