package aaa.main.enums;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum CoverageInfo {

	BPIP("BPIP", "Basic Personal Injury Protection Coverage", CoverageLimits.COV_10000, AvailableCoverageLimits.BPIP_CL),
	ADDPIP("ADDPIP", "Additional Personal Injury Protection Coverage", CoverageLimits.COV_0, AvailableCoverageLimits.ADDPIP_CL),
	PIPDED("PIPDED", "Personal Injury Protection Deductible", CoverageLimits.DED_0, AvailableCoverageLimits.PIPDED_CL),
	PIPDED_OR("PIPDED", "Personal Injury Protection Deductible", CoverageLimits.DED_250, AvailableCoverageLimits.PIPDED_OR, "Deductible"),
	GPIP("GPIP", "Guest Personal Injury Protection", CoverageLimits.COV_0, AvailableCoverageLimits.GPIP_CL),
	PIP_KS_4500("PIP", "Basic Personal Injury Protection", null, null),
	PIP_KS_10000_25000("PIP", "Added Personal Injury Protection",  null, null),
	PIP_OR("PIP", "Personal Injury Protection",  null, null),
	PIP_NO_UT("PIP", "Basic Personal Injury Protection", null, null), //Reject Work Loss = NO
	PIP_YES_UT("PIP", "Limited Personal Injury Protection", null, null), //Reject Work Loss = YES
	PIPMEDICAL_DC("PIPMEDICAL", "PIP Medical and Rehabilitation Expense", CoverageLimits.COV_0, AvailableCoverageLimits.PIPMEDICAL_DC),
	PIPWORKLOSS_DC("PIPWORKLOSS", "PIP Work Loss Benefits", CoverageLimits.COV_0, AvailableCoverageLimits.PIPWORKLOSS_DC),
	PIPFUNERAL_DC("FUNERAL", "PIP Funeral Expense Benefits", CoverageLimits.COV_0, AvailableCoverageLimits.PIPFUNERAL_DC),
	PIP_1530_DE("PIP", "Personal Injury Protection", CoverageLimits.COV_1530, AvailableCoverageLimits.PIP_DE, "Per Person/Per Accident"),
	PIP_OTHER_THAN_1530_DE("PIP", "Added Personal Injury Protection", CoverageLimits.COV_2550, AvailableCoverageLimits.PIP_DE, "Per Person/Per Accident"),
	PIPDED_DE("PIPDED", "Personal Injury Protection Deductible", CoverageLimits.DED_0, AvailableCoverageLimits.PIPDED_DE, "Deductible"),
	PIPDEDAPPTO_DE("PIPDEDAPPTO", "Personal Injury Protection Deductible Applies To", CoverageLimits.COV_PIPDEDAPPTO_NIO, AvailableCoverageLimits.COV_PIPDEDAPPTO_DE),
	PIP_MD("PIP", "Personal Injury Protection", CoverageLimits.COV_PIP_2500_GUEST, AvailableCoverageLimits.COV_PIP_MD, "Per Person"),
	PIP_NJ("PIP", "Personal Injury Protection", null, null, null),
	PIPPRIMINS_NJ("PIPPRIMINS", "Primary Insurer", CoverageLimits.COV_PIPPRIMINS_AUTO_INSURANCE, AvailableCoverageLimits.COV_PIPRIMINS_NJ, null),
	PIPNONMEDEXP_NJ("PIPNONMEDEXP", "Non-Medical Expense", CoverageLimits.COV_FALSE, AvailableCoverageLimits.COV_PIPNONMEDEXP_NJ, null),
	PIPNONMEDEXP_NME_NO_NJ("PIPNONMEDEXP", "Non-Medical Expense", CoverageLimits.COV_TRUE, AvailableCoverageLimits.COV_PIPNONMEDEXP_NJ, null),
	PIPMEDEXP_NJ("PIPMEDEXP", "Medical Expense", CoverageLimits.COV_150000, AvailableCoverageLimits.COV_PIPMEDEXP_NJ, null),
	PIPMAXINCCONT_NJ("PIPMAXINCCONT", "Weekly Income Continuation Benefits", CoverageLimits.COV_100, AvailableCoverageLimits.COV_PIPMAXINCCONT_NJ, "Per Week"),
    PIPMAXINCCONT_NME_NO_NJ("PIPMAXINCCONT", "Income Continuation", CoverageLimits.COV_5200, AvailableCoverageLimits.COV_PIPMAXINCCONT_NJ, "Weekly/Maximum"),
	PIPMEDEXPDED_NJ("PIPMEDEXPDED", "Medical Expense Deductible", CoverageLimits.COV_1000, AvailableCoverageLimits.COV_PIPMEDEXPDED_NJ, "Deductible"),
	PIPEXTMEDPM_NJ("PIPEXTMEDPM", "Extended Medical Payments", CoverageLimits.COV_1000, AvailableCoverageLimits.COV_PIPEXTMEDPM_NJ, null),
	PIPLENINCCONT_NJ("PIPLENINCCONT", "Length of Income Continuation", CoverageLimits.COV_2YR, AvailableCoverageLimits.COV_PIPLENINCCONT_NJ, null),
	PIPCOVINCLUDES_NJ("PIPCOVINCLUDES", "Coverage Includes", CoverageLimits.COV_PIPCOVINCLUDES_NI, AvailableCoverageLimits.COV_PIPCOVINCLUDES_NJ, null),
	DEATHBENEFIT_NJ("DEATHBENEFIT", "Death Benefits", CoverageLimits.COV_10000, null, null),
	APIP_NJ("APIP", "Added Personal Injury Protection", CoverageLimits.COV_FALSE, AvailableCoverageLimits.COV_APIP_NJ, null),
	APIP_NME_YES_PIP_YES_NJ("APIP", "Added Personal Injury Protection", CoverageLimits.COV_TRUE, AvailableCoverageLimits.COV_APIP_NJ, null),
	INCCONT_NJ("PIPMAXINCCONT", "Income Continuation", null, null, null),
	ESSENSERV_NJ("ESSENSERV", "Essential Services", null, null, null),
	ESSENSERV_NME_YES_NJ("ESSENSERV", "Essential Services", CoverageLimits.COV_4380, null, "Day/Maximum"),
	ESSENSERV_NME_YES_PIP_YES_NJ("ESSENSERV", "Essential Services", CoverageLimits.COV_8760, null, "Day/Maximum"),
	FUNEXP_NJ("FUNEXP", "Funeral Expense/Death Benefits", null, null, null),
	FUNEXP_NME_NO_NJ("FUNEXP", "Funeral Expense/Death Benefits", CoverageLimits.COV_1000, null, null),
	FUNEXP_NME_YES_PIP_YES__NJ("FUNEXP", "Funeral Expenses", CoverageLimits.COV_2000, null, null),
	WLB_UT("WLB", "Rejection of Work Loss Benefit", CoverageLimits.COV_FALSE, AvailableCoverageLimits.WLB_UT),
	INCOMELOSS_OR("WORKLOSS", "Income Loss", CoverageLimits.COV_3000, null, "Per Month/Max 52 Weeks"),
	CHILDEXP_OR("CHILDEXP", "Childcare Expenses", CoverageLimits.COV_25, null, "Per Day/$750 Max"),
	WORKLOSS_KS_4500("WORKLOSS", "Work Loss", CoverageLimits.COV_900, null, "Per Month/Max 1 Year"),
	WORKLOSS_KS_10000_25000("WORKLOSS", "Work Loss", CoverageLimits.COV_1500, null, "Per Month/Max 2 Year"),
	WORKLOSS_UT("WORKLOSS", "Work Loss", CoverageLimits.COV_250, null, "Per Week"),
	HOUSEEXP_UT("HOUSEEXP", "Household Services", CoverageLimits.COV_20, null, "Per Day"),
	MEDEXP_KS("MEDEXP", "Medical Expense", CoverageLimits.COV_4500, AvailableCoverageLimits.MEDEXP_KS, "Per Person"),
	MEDEXP_OR("MEDEXP", "Medical Expense", CoverageLimits.COV_15000, AvailableCoverageLimits.MEDEXP_OR, "Per Accident"),
	MEDEXP_UT("MEDEXP", "Medical Expense", CoverageLimits.COV_3000, AvailableCoverageLimits.MEDEXP_UT, "Per Person"),
	REHABEXP_KS("REHABEXP", "Rehabilitation Expense", CoverageLimits.COV_4500, null, "Per Person"),
	ESSENSERV_KS("ESSENSERV", "Essential Services", CoverageLimits.COV_25, null, "Per Day/Max 365 Days"),
	ESSENSERV_OR("ESSENSERV", "Essential Services", CoverageLimits.COV_30, null, "Per Day/Max 52 Weeks"),
	FUNEXP_KS("FUNEXP", "Funeral Expenses", CoverageLimits.COV_2000, null, "Per Person"),
	FUNEXP_OR("FUNEXP", "Funeral Expenses", CoverageLimits.COV_UP_TO_5000, null, null),
	FUNEXP_UT("FUNEXP", "Funeral Expenses", CoverageLimits.COV_1500, null, "Per Person"),
	FUNEXP_DE("FUNEXP", "Funeral Expenses", CoverageLimits.COV_5000, null, null),
	PROPERTY_DE("PROPERTY", "Property Protection Coverage", CoverageLimits.COV_10000, null, "Per Accident"),
	SURVLOSS_KS("SURVLOSS", "Survivor's Loss", CoverageLimits.COV_900, null, "Per Month"),
	SURVLOSS_UT("SURVLOSS", "Survivor's Loss", CoverageLimits.COV_3000, null, null),
	UMBI_MD("UMBI", "Standard Uninsured/Underinsured Motorist Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI_MD, "Per Person/Per Accident"),
	UMBI_MD_ENHANCED_UIM_TRUE("UMBI", "Enhanced Uninsured/Underinsured Motorist Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI_MD, "Per Person/Per Accident"),
	UMPD_MD("UMPD", "Standard Uninsured Motorist Property Damage", CoverageLimits.COV_50000, AvailableCoverageLimits.UMPD_MD, "Per Accident"),
	UMPD_MD_ENHANCED_UIM_TRUE("UMPD", "Enhanced Uninsured Motorist Property Damage", CoverageLimits.COV_50000, AvailableCoverageLimits.UMPD_MD, "Per Accident"),
	UMBI_UT_00("UMBI", "Uninsured Motorists Bodily Injury", CoverageLimits.COV_00, AvailableCoverageLimits.UMBI_UT, "Per Person/Per Accident"),
	UMBI_UT_100_300("UMBI", "Uninsured Motorists Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI_UT, "Per Person/Per Accident"),
	UIMBI_UT_00("UIMBI", "Underinsured Motorists Bodily Injury", CoverageLimits.COV_00, AvailableCoverageLimits.UIMBI_UT, "Per Person/Per Accident"),
	UIMBI_UT_100_300("UIMBI", "Underinsured Motorists Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UIMBI_UT, "Per Person/Per Accident"),
	UMPD_UT_0("UMPD", "Uninsured Motorist Property Damage", CoverageLimits.COV_0, AvailableCoverageLimits.UMPD_UT, "Per Accident"),
	UMPD_UT_3500("UMPD", "Uninsured Motorist Property Damage", CoverageLimits.DED_3500, AvailableCoverageLimits.UMPD_UT, "Per Accident"),
	EUIM_MD("EUIM", "Enhanced UIM", CoverageLimits.COV_FALSE, AvailableCoverageLimits.EUIM_MD, null),
	EUIM_MD_TRUE("EUIM", "Enhanced UIM", CoverageLimits.COV_TRUE, AvailableCoverageLimits.EUIM_MD, null),
	BI("BI", "Bodily Injury Liability", CoverageLimits.COV_100300, AvailableCoverageLimits.BI, "Per Person/Per Accident"),
	PD("PD", "Property Damage Liability", CoverageLimits.COV_50000, AvailableCoverageLimits.PD_IN, "Per Accident"),
	PD_VA("PD", "Property Damage Liability", CoverageLimits.COV_50000, AvailableCoverageLimits.PD_VA, "Per Accident"),
	PD_WV("PD", "Property Damage Liability", CoverageLimits.COV_50000, AvailableCoverageLimits.PD_WV, "Per Accident"),
	PD_DC("PD", "Property Damage Liability", CoverageLimits.COV_50000, AvailableCoverageLimits.PD_DC, "Per Accident"),
	PD_NJ("PD", "Property Damage Liability", CoverageLimits.COV_50000, AvailableCoverageLimits.PD_NJ, "Per Accident"),
	UIMPD("UIMPD", "Underinsured Motorist Property Damage", CoverageLimits.COV_50000, AvailableCoverageLimits.UIMPD,"Property Damage"),
	UIMPD_DC("UIMPD", "Underinsured Motorist Property Damage", CoverageLimits.COV_50000, AvailableCoverageLimits.UIMPD_DC, "Per Accident"),
	UMPD("UMPD", "Uninsured Motorist Property Damage", CoverageLimits.COV_50000, AvailableCoverageLimits.UMPD, "Per Accident"),
	UMPD_VA("UMPD", "Uninsured Motorist Property Damage", CoverageLimits.COV_50000, AvailableCoverageLimits.UMPD_VA, "Per Accident"),
	UMPD_DC("UMPD", "Uninsured Motorist Property Damage", CoverageLimits.COV_50000, AvailableCoverageLimits.UMPD_DC, "Per Accident"),
	UMPD_DE("UMPD", "Uninsured Motorist Property Damage", CoverageLimits.COV_10000, AvailableCoverageLimits.UMPD_DE, "Per Accident"),
	UMPD_NJ("UMPD", "Uninsured Motorist Property Damage", CoverageLimits.COV_50000, AvailableCoverageLimits.UMPD_NJ, "Per Accident"),
	UIMCONV_CT("UIMCONV", "Underinsured Motorist Conversion Coverage", CoverageLimits.COV_FALSE_NO_COVERAGE, AvailableCoverageLimits.UIMCONV_CT),
	UMBI_CT_NO("UMBI", "Uninsured/Underinsured Motorist Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI_CT), //when UIMCONV  = No
	UMBI_CT_YES("UMBI", "Uninsured/Underinsured Motorist Bodily Injury With UIM Conversion Coverage", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI_CT), //when UIMCONV  = Yes
	ADB("ADB", "Automobile Death Benefit", null, null, null),
	TD("TD", "Total Disability", null, null, null),
	TORT_PA("TORT", "Tort Threshold", CoverageLimits.COV_LIMITED_TORT, AvailableCoverageLimits.TORT_PA, null),
	UMBI("UMBI", "Uninsured Motorist Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI,"Per Person/Per Accident"),
	UMBI_DC("UMBI", "Uninsured Motorists Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI,"Per Person/Per Accident"),
	UMBI_PA("UMBI", "Uninsured Motorists Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI_PA,"Per Person/Per Accident"),
	UIMBI_PA("UIMBI", "Underinsured Motorists Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UIMBI_PA,"Per Person/Per Accident"),
	UMSU_PA("UMSU", "Uninsured Motorist Stacked/Unstacked", CoverageLimits.COV_STACKED, AvailableCoverageLimits.UMSU_PA, null),
	UIMSU_PA("UIMSU", "Underinsured Motorist Stacked/Unstacked", CoverageLimits.COV_STACKED, AvailableCoverageLimits.UIMSU_PA, null),
	UMBI_VA_KS("UMBI", "Uninsured/Underinsured Motorist Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI,"Per Person/Per Accident"),
	UIMBI("UIMBI", "Underinsured Motorist Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UIMBI,"Per Person/Per Accident"),
	UIMBI_DC("UIMBI", "Underinsured Motorists Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UIMBI,"Per Person/Per Accident"),
	BI_WV_VA_KS_DC_DE("BI", "Bodily Injury Liability", CoverageLimits.COV_100300, AvailableCoverageLimits.BI_WV_VA_KS_DC_DE, "Per Person/Per Accident"),
	UMBI_DE("UMBI", "Uninsured/Underinsured Motorist Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI_DE,"Per Person/Per Accident"),
	UMBI_NJ("UMBI", "Uninsured/Underinsured Motorist Bodily Injury", CoverageLimits.COV_100300, AvailableCoverageLimits.UMBI_NJ,"Per Person/Per Accident"),
	PDWV("PD", "Property Damage Liability", CoverageLimits.COV_50000, AvailableCoverageLimits.PD, "Per Accident"),
	UMPD_WV("UMPD", "Uninsured Motorist Property Damage", CoverageLimits.COV_50000, AvailableCoverageLimits.UMPD, "Property Damage"),
	UMPD_OR("UMPD", "Uninsured Motorist Property Damage", CoverageLimits.COV_0, AvailableCoverageLimits.UMPD_OR, "Per Accident"),
	LOAN("LOAN", "Vehicle Loan/Lease Protection", CoverageLimits.COV_0, AvailableCoverageLimits.LOAN,"None"),
	RREIM("RREIM", "Rental Reimbursement", CoverageLimits.COV_00, AvailableCoverageLimits.PREIM,"Per Day/Maximum"),
	TOWINGLABOR("TOWINGLABOR", "Towing and Labor Coverage", CoverageLimits.COV_00, AvailableCoverageLimits.TOWINGLABOR,"Per Disablement/Maximum"),
	SPECEQUIP("SPECEQUIP", "Special Equipment Coverage", CoverageLimits.COV_SP_1000, null,null),
	NEWCAR("NEWCAR", "New Car Added Protection", CoverageLimits.COV_FALSE, null),
	COMPDED_AZ_PPA("COMPDED", "Comprehensive Deductible", CoverageLimits.COV_250, AvailableCoverageLimits.COMPDEDNONPPA, "Deductible"),
	COLLDED_AZ_PPA("COLLDED", "Collision Deductible", CoverageLimits.COV_500, AvailableCoverageLimits.COLLDEDNONPPA, "Deductible"),
	SPECEQUIP_NONPPA("SPECEQUIP", "Special Equipment Coverage", CoverageLimits.SP_EQ_0, null,null),
	BI_AZ_PA_NJ("BI", "Bodily Injury Liability", CoverageLimits.COV_100300, AvailableCoverageLimits.BI_AZ_PA_NJ, "Per Person/Per Accident"),
	COMPDED_AZ("COMPDED", "Comprehensive Deductible", CoverageLimits.COV_750, AvailableCoverageLimits.COMPDED, "Deductible"),
	COLLDED_AZ("COLLDED", "Collision Deductible", CoverageLimits.COV_500, AvailableCoverageLimits.COLLDED, "Deductible"),
	FPB_BASIC_PA("FPB", "Basic First Party Benefits", CoverageLimits.COV_FPB_5K_BASIC,  AvailableCoverageLimits.FPB_PA, null),
	FPB_PACKAGED_PA("FPB", "Packaged First Party Benefits", CoverageLimits.COV_FPB_50K_TOTAL,  AvailableCoverageLimits.FPB_PA, null),
	FPB_ADDED_PA("FPB", "Added First Party Benefits", CoverageLimits.COV_FPB_ADDED,  AvailableCoverageLimits.FPB_PA, null),
	MEDPM_PA("MEDPM", "Medical Benefit", CoverageLimits.COV_5000,  AvailableCoverageLimits.MEDPM_PA, null),
	IL_PA("IL", "Work Loss Benefit", CoverageLimits.COV_00,  AvailableCoverageLimits.IL_PA, null),
	FUNERAL_PA("FUNERAL", "Funeral Expense Benefit", CoverageLimits.COV_0,  AvailableCoverageLimits.FUNERAL_PA, null),
	ADBC_PA("ADBC", "Accidental Death Benefit", CoverageLimits.COV_0,  AvailableCoverageLimits.ADBC_PA, null),
	GLASS_AZ("GLASS", "Full Safety Glass", CoverageLimits.COV_FALSE_NO_COVERAGE, AvailableCoverageLimits.GLASS_AZ,"None"),
	UMPD_NV_NOCOLL("UMPD", "Uninsured Motorist Property Damage or Collision Deductible Waived", null, AvailableCoverageLimits.UMPD_NV_NOCOLL, "Per Accident"),
	UMPD_NV_WITHCOLL("UMPD", "Uninsured Motorist Property Damage or Collision Deductible Waived", null, AvailableCoverageLimits.UMPD_NV_WITHCOLL, "Per Accident"),
	UMPD_NV_WOUM("UMPD", "Uninsured Motorist Property Damage or Collision Deductible Waived", null, AvailableCoverageLimits.UMPD_NV_WOUM, "Per Accident"),
	EMB("EMB", "Extraordinary Medical Expense Benefits", CoverageLimits.COV_EMB_0, AvailableCoverageLimits.EMB, null),
	LOL("LOL", "Limitation on Lawsuit", CoverageLimits.COV_LOL, AvailableCoverageLimits.LOL, null);

	private final String code;
	private final String description;
	private final String coverageType;
	private final CoverageLimits defaultLimit;
	private final List<CoverageLimits> availableLimits;

	CoverageInfo(String code, String description, CoverageLimits defaultLimit, AvailableCoverageLimits availableLimits) {
		this(code, description, defaultLimit, availableLimits, null);
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

	public List<CoverageLimits> getReversedAvailableLimits() {
		List<CoverageLimits> reversedAvailableCoverageLimitList = new ArrayList<>(availableLimits);
		Collections.reverse(reversedAvailableCoverageLimitList);
		return reversedAvailableCoverageLimitList;
	}
}
