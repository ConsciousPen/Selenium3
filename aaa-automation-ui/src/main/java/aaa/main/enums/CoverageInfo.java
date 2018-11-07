package aaa.main.enums;

import java.util.List;

public enum CoverageInfo {

	BPIP("BPIP", "Basic Personal Injury Protection Coverage", CoverageLimits.COV_10000, AvailableCoverageLimits.BPIP_CL.getAvailableLimits()),
	ADDPIP("ADDPIP", "Additional Personal Injury Protection Coverage", CoverageLimits.COV_0, AvailableCoverageLimits.ADDPIP_CL.getAvailableLimits()),
	PIPDED("PIPDED", "Personal Injury Protection Deductible", CoverageLimits.DED_0, AvailableCoverageLimits.PIPDED_CL.getAvailableLimits()),
	GPIP("GPIP", "Guest Personal Injury Protection", CoverageLimits.COV_0, AvailableCoverageLimits.GPIP_CL.getAvailableLimits()),
	PD("PD", "Property Damage Liability", CoverageLimits.COV_50000, AvailableCoverageLimits.PD_IN.getAvailableLimits(), "Per Accident"),
	UIMPD("UIMPD", "Underinsured Motorist Property Damage", CoverageLimits.COV_50000, AvailableCoverageLimits.UIMPD.getAvailableLimits()),
	UMPD("UMPD", "Uninsured Motorist Property Damage", CoverageLimits.COV_50000, AvailableCoverageLimits.UMPD.getAvailableLimits(), "Per Accident"),

	PIP_KS_4500("PIP", "Basic Personal Injury Protection", null, ImmutableList.of()),
	PIP_KS_10000_25000("PIP", "Added Personal Injury Protection",  null, ImmutableList.of()),
	WORKLOSS_KS_4500("WORKLOSS", "Work Loss", CoverageLimits.COV_900, ImmutableList.of(), "Per Month/Max 1 Year"),
	WORKLOSS_KS_10000_25000("WORKLOSS", "Work Loss", CoverageLimits.COV_1500, ImmutableList.of(), "Per Month/Max 2 Year"),
	MEDEXP_KS("MEDEXP", "Medical Expense", CoverageLimits.COV_4500, AvailableCoverageLimits.MEDEXP_KS.getAvailableLimits(), "Per Person"),
	REHABEXP_KS("REHABEXP", "Rehabilitation Expense", CoverageLimits.COV_4500, ImmutableList.of(), "Per Person"),
	ESSENSERV_KS("ESSENSERV", "Essential Services", CoverageLimits.COV_25, ImmutableList.of(), "Per Day/Max 365 Days"),
	FUNEXP_KS("FUNEXP", "Funeral Expenses", CoverageLimits.COV_2000, ImmutableList.of(), "Per Person"),
	SURVLOSS_KS("SURVLOSS", "Survivor's Loss", CoverageLimits.COV_900, ImmutableList.of(), "Per Month");
	BPIP("BPIP", "Basic Personal Injury Protection Coverage", CoverageLimits.COV_10000, AvailableCoverageLimits.BPIP_CL),
	ADDPIP("ADDPIP", "Additional Personal Injury Protection Coverage", CoverageLimits.COV_0, AvailableCoverageLimits.ADDPIP_CL),
	PIPDED("PIPDED", "Personal Injury Protection Deductible", CoverageLimits.DED_0, AvailableCoverageLimits.PIPDED_CL),
	GPIP("GPIP", "Guest Personal Injury Protection", CoverageLimits.COV_0, AvailableCoverageLimits.GPIP_CL),
	PIP_KS_4500("PIP", "Basic Personal Injury Protection", null, null),
	PIP_KS_10000_25000("PIP", "Added Personal Injury Protection",  null, null),
	WORKLOSS_KS_4500("WORKLOSS", "Work Loss", CoverageLimits.COV_900, null, "Per Month/Max 1 Year"),
	WORKLOSS_KS_10000_25000("WORKLOSS", "Work Loss", CoverageLimits.COV_1500, null, "Per Month/Max 2 Year"),
	MEDEXP_KS("MEDEXP", "Medical Expense", CoverageLimits.COV_4500, AvailableCoverageLimits.MEDEXP_KS, "Per Person"),
	REHABEXP_KS("REHABEXP", "Rehabilitation Expense", CoverageLimits.COV_4500, null, "Per Person"),
	ESSENSERV_KS("ESSENSERV", "Essential Services", CoverageLimits.COV_25, null, "Per Day/Max 365 Days"),
	FUNEXP_KS("FUNEXP", "Funeral Expenses", CoverageLimits.COV_2000, null, "Per Person"),
	SURVLOSS_KS("SURVLOSS", "Survivor's Loss", CoverageLimits.COV_900, null, "Per Month"),
	UMBI_MD("UMBI", "Standard Uninsured/Underinsured Motorist Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI_MD, "Per Person/Per Accident"),
	UMBI_MD_ENHANCED_UIM_TRUE("UMBI", "Enhanced Uninsured/Underinsured Motorist Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI_MD, "Per Person/Per Accident"),
	UMPD_MD("UMPD", "Standard Uninsured Motorist Property Damage", CoverageLimits.COV_50000, AvailableCoverageLimits.UMPD_MD, "Per Accident"),
	UMPD_MD_ENHANCED_UIM_TRUE("UMPD", "Enhanced Uninsured Motorist Property Damage", CoverageLimits.COV_50000, AvailableCoverageLimits.UMPD_MD, "Per Accident"),
	EUIM_MD("EUIM", "Enhanced UIM Selected", CoverageLimits.COV_FALSE, AvailableCoverageLimits.EUIM_MD, null),
	EUIM_MD_TRUE("EUIM", "Enhanced UIM Selected", CoverageLimits.COV_TRUE, AvailableCoverageLimits.EUIM_MD, null),
	BI("BI", "Bodily Injury Liability", CoverageLimits.COV_100300, AvailableCoverageLimits.BI, "Per Person/Per Accident"),
	PD("PD", "Property Damage Liability", CoverageLimits.COV_50000, AvailableCoverageLimits.PD_IN, "Per Accident");

	private final String code;
	private final String description;
	private final String coverageType;
	private final CoverageLimits defaultLimit;
	private final List<CoverageLimits> availableLimits;

	CoverageInfo(String code, String description, CoverageLimits defaultLimit, AvailableCoverageLimits availableLimits) {
		this.code = code;
		this.description = description;
		this.coverageType = null;
		this.defaultLimit = defaultLimit;
		if(availableLimits != null) {
			this.availableLimits = availableLimits.getAvailableLimits();
		} else {
			this.availableLimits = null;
		}
	}

	CoverageInfo(String code, String description, CoverageLimits defaultLimit, AvailableCoverageLimits availableLimits, String coverageType) {
		this.code = code;
		this.description = description;
		this.coverageType = coverageType;
		this.defaultLimit = defaultLimit;
		if(availableLimits != null) {
			this.availableLimits = availableLimits.getAvailableLimits();
		} else {
			this.availableLimits = null;
		}
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
