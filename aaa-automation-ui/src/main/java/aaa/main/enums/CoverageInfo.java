package aaa.main.enums;

import java.util.List;

public enum CoverageInfo {

	BPIP("BPIP", "Basic Personal Injury Protection Coverage", CoverageLimits.COV_10000, AvailableCoverageLimits.BPIP_CL.getAvailableLimits()),
	ADDPIP("ADDPIP", "Additional Personal Injury Protection Coverage", CoverageLimits.COV_0, AvailableCoverageLimits.ADDPIP_CL.getAvailableLimits()),
	PIPDED("PIPDED", "Personal Injury Protection Deductible", CoverageLimits.COV_00, AvailableCoverageLimits.PIPDED_CL.getAvailableLimits()),
	GPIP("GPIP", "Guest Personal Injury Protection", CoverageLimits.COV_0, AvailableCoverageLimits.GPIP_CL.getAvailableLimits());

	private final String code;
	private final String description;
	private final String coverageType;
	private final CoverageLimits defaultLimit;
	private final List<CoverageLimits> availableLimits;

	CoverageInfo(String code, String description, CoverageLimits defaultLimit, List<CoverageLimits> availableLimits) {
		this.code = code;
		this.description = description;
		this.coverageType = null;
		this.defaultLimit = defaultLimit;
		this.availableLimits = availableLimits;
	}

	CoverageInfo(String code, String description, CoverageLimits defaultLimit, List<CoverageLimits> availableLimits, String coverageType) {
		this.code = code;
		this.description = description;
		this.coverageType = coverageType;
		this.defaultLimit = defaultLimit;
		this.availableLimits = availableLimits;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getCoverageType() {
		return coverageType;
	}

	public CoverageLimits getDefaultLimit() {
		return defaultLimit;
	}

	public List<CoverageLimits> getAvailableLimits() {
		return availableLimits;
	}
}
