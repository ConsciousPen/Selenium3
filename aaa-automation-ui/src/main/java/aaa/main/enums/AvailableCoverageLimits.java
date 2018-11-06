package aaa.main.enums;

import java.util.List;
import com.google.common.collect.ImmutableList;

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
	));

	private final List<CoverageLimits> availableLimits;

	AvailableCoverageLimits(List<CoverageLimits> availableLimits) {
		this.availableLimits = availableLimits;
	}

	public List<CoverageLimits> getAvailableLimits() {
		return availableLimits;
	}

}
