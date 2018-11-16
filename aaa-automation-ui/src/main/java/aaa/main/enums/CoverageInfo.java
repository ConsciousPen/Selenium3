package aaa.main.enums;

import com.google.common.collect.ImmutableList;

import java.util.List;
import com.google.common.collect.ImmutableList;

public enum CoverageInfo {

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
	UMBI_UT_00("UMBI", "Uninsured Motorists Bodily Injury", CoverageLimits.COV_00, AvailableCoverageLimits.UMBI_UT, "Per Person/Per Accident"),
	UMBI_UT_100_300("UMBI", "Uninsured Motorists Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI_UT, "Per Person/Per Accident"),
	UIMBI_UT_00("UIMBI", "Underinsured Motorists Bodily Injury", CoverageLimits.COV_00, AvailableCoverageLimits.UIMBI_UT, "Per Person/Per Accident"),
	UIMBI_UT_100_300("UIMBI", "Underinsured Motorists Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UIMBI_UT, "Per Person/Per Accident"),
	UMPD_UT_NO("UMPD", "Uninsured Motorist Property Damage", CoverageLimits.DED_3500, AvailableCoverageLimits.UMPD_UT, "Per Accident"),
	UMPD_UT_0("UMPD", "Uninsured Motorist Property Damage", CoverageLimits.COV_0, AvailableCoverageLimits.UMPD_UT, "Per Accident"),
	UMPD_UT_3500("UMPD", "Uninsured Motorist Property Damage", CoverageLimits.DED_3500, AvailableCoverageLimits.UMPD_UT, "Per Accident"),
	EUIM_MD("EUIM", "Enhanced UIM Selected", CoverageLimits.COV_FALSE, AvailableCoverageLimits.EUIM_MD, null),
	EUIM_MD_TRUE("EUIM", "Enhanced UIM Selected", CoverageLimits.COV_TRUE, AvailableCoverageLimits.EUIM_MD, null),
	BI("BI", "Bodily Injury Liability", CoverageLimits.COV_100300, AvailableCoverageLimits.BI, "Per Person/Per Accident"),
	PD("PD", "Property Damage Liability", CoverageLimits.COV_50000, AvailableCoverageLimits.PD_IN, "Per Accident"),
	UIMPD("UIMPD", "Underinsured Motorist Property Damage", CoverageLimits.COV_50000, AvailableCoverageLimits.UIMPD,"Property Damage"),
	UMPD("UMPD", "Uninsured Motorist Property Damage", CoverageLimits.COV_50000, AvailableCoverageLimits.UMPD, "Per Accident"),
	UIMCONV_CT("UIMCONV", "Underinsured Motorist Conversion Coverage", CoverageLimits.COV_FALSE_NO_COVERAGE, AvailableCoverageLimits.UIMCONV_CT),
	UMBI_CT_NO("UMBI", "Uninsured/Underinsured Motorist Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI_CT), //when UIMCONV  = No
	UMBI("UMBI", "Uninsured Motorist Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI,"Per Person/Per Accident"),
	UMBI_CT_YES("UMBI", "Uninsured/Underinsured Motorist Bodily Injury With UIM Conversion Coverage", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI_CT), //when UIMCONV  = Yes
	UIMBI("UIMBI", "Underinsured Motorist Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UIMBI,"Per Person/Per Accident"),
	BIWV("BI", "Bodily Injury Liability", CoverageLimits.COV_100300, AvailableCoverageLimits.BIWV, "Per Person/Per Accident"),
	PDWV("PD", "Property Damage Liability", CoverageLimits.COV_50000, AvailableCoverageLimits.PD, "Per Accident"),
	UMPD_WV("UMPD", "Uninsured Motorist Property Damage", CoverageLimits.COV_50000, AvailableCoverageLimits.UMPD, "Property Damage");


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
			this.availableLimits = ImmutableList.of();
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
			this.availableLimits = ImmutableList.of();
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
