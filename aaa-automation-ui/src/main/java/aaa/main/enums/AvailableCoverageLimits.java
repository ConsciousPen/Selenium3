package aaa.main.enums;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
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
	PIPDED_OR(ImmutableList.of(
			CoverageLimits.DED_0,
			CoverageLimits.DED_250
	)),
	GPIP_CL(ImmutableList.of(
			CoverageLimits.COV_10000,
			CoverageLimits.COV_0
	)),
	PIPMEDICAL_DC(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_100000
	)),
	PIPWORKLOSS_DC(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_12000,
			CoverageLimits.COV_24000
	)),
	PIPFUNERAL_DC(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_4000
	)),
	PIP_DE(ImmutableList.of(
			CoverageLimits.COV_1530,
			CoverageLimits.COV_2550,
			CoverageLimits.COV_50100,
			CoverageLimits.COV_100300
	)),
	PIPDED_DE(ImmutableList.of(
			CoverageLimits.DED_0,
			CoverageLimits.DED_250,
			CoverageLimits.DED_500,
			CoverageLimits.DED_1000,
			CoverageLimits.DED_10000
	)),
	PIPDED_NY(ImmutableList.of(
			CoverageLimits.DED_0,
			CoverageLimits.DED_200
	)),
	OBEL_NY(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_25000
	)),
	APIP_NY(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_50000_FULL,
			CoverageLimits.COV_100000_FULL
	)),
	COV_PIPDEDAPPTO_DE(ImmutableList.of(
			CoverageLimits.COV_PIPDEDAPPTO_NIO,
			CoverageLimits.COV_PIPDEDAPPTO_NIAHF
	)),
	COV_PIP_MD(ImmutableList.of(
			CoverageLimits.COV_PIP_2500_GUEST,
			CoverageLimits.COV_PIP_5000_GUEST,
			CoverageLimits.COV_PIP_2500_FULL,
			CoverageLimits.COV_PIP_5000_FULL
	)),
	COV_PIPRIMINS_NJ(ImmutableList.of(
			CoverageLimits.COV_PIPPRIMINS_AUTO_INSURANCE,
			CoverageLimits.COV_PIPPRIMINS_PERSONAL_HEALTH_INSURANCE
	)),
	COV_PIPNONMEDEXP_NJ(ImmutableList.of(
			CoverageLimits.COV_FALSE,
			CoverageLimits.COV_TRUE
	)),
	COV_PIPMEDEXP_NJ(ImmutableList.of(
			CoverageLimits.COV_15000,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_75000,
			CoverageLimits.COV_150000,
			CoverageLimits.COV_250000
	)),

    COV_PIPMAXINCCONT_NJ(ImmutableList.of(
            CoverageLimits.COV_100,
            CoverageLimits.COV_125,
            CoverageLimits.COV_175,
            CoverageLimits.COV_250,
            CoverageLimits.COV_400,
            CoverageLimits.COV_500,
            CoverageLimits.COV_600,
            CoverageLimits.COV_700
    )),

	COV_PIPMEDEXPDED_NJ(ImmutableList.of(
			CoverageLimits.COV_250,
			CoverageLimits.COV_500,
			CoverageLimits.COV_1000,
			CoverageLimits.COV_2000,
			CoverageLimits.COV_2500
	)),
	COV_PIPEXTMEDPM_NJ(ImmutableList.of(
			CoverageLimits.COV_1000,
			CoverageLimits.COV_10000
	)),
	COV_APIP_NJ(ImmutableList.of(
			CoverageLimits.COV_FALSE,
			CoverageLimits.COV_TRUE
	)),
	PD_IN(ImmutableList.of(
			CoverageLimits.COV_10000,
			CoverageLimits.COV_15000,
			CoverageLimits.COV_25000,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_100000,
			CoverageLimits.COV_300000
	)),
	PD_VA(ImmutableList.of(
			CoverageLimits.COV_20000,
			CoverageLimits.COV_25000,
			CoverageLimits.COV_40000,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_100000,
			CoverageLimits.COV_300000,
			CoverageLimits.COV_500000,
			CoverageLimits.COV_1000000
	)),
	PD_WV(ImmutableList.of(
			CoverageLimits.COV_25000,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_100000,
			CoverageLimits.COV_300000,
			CoverageLimits.COV_500000,
			CoverageLimits.COV_1000000
	)),
	PD_DC(ImmutableList.of(
			CoverageLimits.COV_10000,
			CoverageLimits.COV_15000,
			CoverageLimits.COV_25000,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_100000,
			CoverageLimits.COV_300000,
			CoverageLimits.COV_500000,
			CoverageLimits.COV_1000000
	)),
	PD_NJ(ImmutableList.of(
			CoverageLimits.COV_5000,
			CoverageLimits.COV_10000,
			CoverageLimits.COV_15000,
			CoverageLimits.COV_25000,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_100000,
			CoverageLimits.COV_300000,
			CoverageLimits.COV_500000,
			CoverageLimits.COV_1000000
	)),
	PD_DE(ImmutableList.of(
			CoverageLimits.COV_10000,
			CoverageLimits.COV_15000,
			CoverageLimits.COV_25000,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_100000,
			CoverageLimits.COV_300000,
			CoverageLimits.COV_500000,
			CoverageLimits.COV_1000000
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
	UIMPD_DC(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_5000,
			CoverageLimits.COV_10000,
			CoverageLimits.COV_15000,
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
	MEDEXP_OR(ImmutableList.of(
			CoverageLimits.COV_15000,
			CoverageLimits.COV_25000,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_100000
	)),
	MEDEXP_UT(ImmutableList.of(
			CoverageLimits.COV_3000,
			CoverageLimits.COV_5000,
			CoverageLimits.COV_10000
	)),
	WLB(ImmutableList.of(
			CoverageLimits.COV_FALSE,
			CoverageLimits.COV_TRUE
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
	UMPD_VA(ImmutableList.of(
			CoverageLimits.COV_20000,
			CoverageLimits.COV_25000,
			CoverageLimits.COV_40000,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_100000,
			CoverageLimits.COV_300000,
			CoverageLimits.COV_500000,
			CoverageLimits.COV_1000000
	)),
	UMPD_NJ(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_5000,
			CoverageLimits.COV_10000,
			CoverageLimits.COV_15000,
			CoverageLimits.COV_25000,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_100000,
			CoverageLimits.COV_300000,
			CoverageLimits.COV_500000,
			CoverageLimits.COV_1000000
	)),
	UMPD_DC(ImmutableList.of(
			CoverageLimits.COV_5000,
			CoverageLimits.COV_10000,
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
	GLASS_AZ(ImmutableList.of(
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
	UMBI_DE(ImmutableList.of(
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
	UMBI_NJ(ImmutableList.of(
			CoverageLimits.COV_1530,
			CoverageLimits.COV_2550,
			CoverageLimits.COV_50100,
			CoverageLimits.COV_100300,
			CoverageLimits.COV_250500,
			CoverageLimits.COV_300500,
			CoverageLimits.COV_500500,
			CoverageLimits.COV_5001000,
			CoverageLimits.COV_10001000
	)),
	UMBI_PA(ImmutableList.of(
			CoverageLimits.COV_00,
			CoverageLimits.COV_1530,
			CoverageLimits.COV_2550,
			CoverageLimits.COV_50100,
			CoverageLimits.COV_100300,
			CoverageLimits.COV_250500,
			CoverageLimits.COV_300500,
			CoverageLimits.COV_500500
	)),
	UIMBI_PA(ImmutableList.of(
			CoverageLimits.COV_00,
			CoverageLimits.COV_1530,
			CoverageLimits.COV_2550,
			CoverageLimits.COV_50100,
			CoverageLimits.COV_100300,
			CoverageLimits.COV_250500,
			CoverageLimits.COV_300500,
			CoverageLimits.COV_500500
	)),
	BI_WV_VA_KS_DC_DE(ImmutableList.of(
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
	)),
	BI_AZ_PA_NJ(ImmutableList.of(
			CoverageLimits.COV_1530,
			CoverageLimits.COV_2550,
			CoverageLimits.COV_50100,
			CoverageLimits.COV_100300,
			CoverageLimits.COV_250500,
			CoverageLimits.COV_300500,
			CoverageLimits.COV_500500,
			CoverageLimits.COV_5001000,
			CoverageLimits.COV_10001000
	)),
	BI_PA(ImmutableList.of(
			CoverageLimits.COV_1530,
			CoverageLimits.COV_2550,
			CoverageLimits.COV_50100,
			CoverageLimits.COV_100300,
			CoverageLimits.COV_250500,
			CoverageLimits.COV_300500,
			CoverageLimits.COV_500500
	)),
	COMPDED(ImmutableList.of(
			CoverageLimits.COV_50,
			CoverageLimits.COV_100,
			CoverageLimits.COV_250,
			CoverageLimits.COV_500,
			CoverageLimits.COV_750,
			CoverageLimits.COV_1000
			)),
	COLLDED(ImmutableList.of(
			CoverageLimits.COV_100,
			CoverageLimits.COV_250,
			CoverageLimits.COV_500,
			CoverageLimits.COV_750,
			CoverageLimits.COV_1000
	)),
	LOAN(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_1
	)),
	PREIM(ImmutableList.of(
			CoverageLimits.COV_00,
			CoverageLimits.COV_30_900,
			CoverageLimits.COV_40_1200,
			CoverageLimits.COV_50_1500
	)),
	TOWINGLABOR(ImmutableList.of(
			CoverageLimits.COV_00,
			CoverageLimits.COV_50_300
	)),
	TORT_PA(ImmutableList.of(
			CoverageLimits.COV_FULL_TORT,
			CoverageLimits.COV_LIMITED_TORT
	)),
	FPB_PA(ImmutableList.of(
			CoverageLimits.COV_FPB_ADDED,
			CoverageLimits.COV_FPB_5K_BASIC,
			CoverageLimits.COV_FPB_50K_TOTAL,
			CoverageLimits.COV_FPB_100K_TOTAL,
			CoverageLimits.COV_FPB_177_5K_TOTAL
	)),
	MEDPM_PA(ImmutableList.of(
			CoverageLimits.COV_5000,
			CoverageLimits.COV_10000,
			CoverageLimits.COV_25000,
			CoverageLimits.COV_50000,
			CoverageLimits.COV_100000

	)),
	IL_PA(ImmutableList.of(
			CoverageLimits.COV_00,
			CoverageLimits.COV_IL_5000_MAX_PA,
			CoverageLimits.COV_IL_15000_MAX_PA,
			CoverageLimits.COV_IL_25000_MAX_PA,
			CoverageLimits.COV_IL_50000_MAX_PA
	)),
	FUNERAL_PA(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_1500,
			CoverageLimits.COV_2500
	)),
	ADBC_PA(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_5000,
			CoverageLimits.COV_15000,
			CoverageLimits.COV_25000
	)),
	COLLDEDNONPPA(ImmutableList.of(
			CoverageLimits.COV_NO_COV,
			CoverageLimits.COV_100,
			CoverageLimits.COV_250,
			CoverageLimits.COV_500,
			CoverageLimits.COV_750,
			CoverageLimits.COV_1000
	)),
	COMPDEDNONPPA(ImmutableList.of(
			CoverageLimits.COV_NO_COV,
			CoverageLimits.COV_50,
			CoverageLimits.COV_100,
			CoverageLimits.COV_250,
			CoverageLimits.COV_500,
			CoverageLimits.COV_750,
			CoverageLimits.COV_1000
	)),
	UMPD_DE(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_10000
	)),
	UMPD_OR(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_20000,
			CoverageLimits.COV_25000,
			CoverageLimits.COV_50000
	)),
	UMPD_NV_NOCOLL(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_3500
	)),
	UMPD_NV_WITHCOLL(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_CDW
	)),
	UMPD_NV_WOUM(ImmutableList.of(
			CoverageLimits.COV_0,
			CoverageLimits.COV_3500,
			CoverageLimits.COV_CDW
	)),
	UMSU_PA(ImmutableList.of(
			CoverageLimits.COV_STACKED,
			CoverageLimits.COV_UNSTACKED
	)),
	UIMSU_PA(ImmutableList.of(
			CoverageLimits.COV_STACKED,
			CoverageLimits.COV_UNSTACKED
	)),
	EMB(ImmutableList.of(
			CoverageLimits.COV_EMB_0,
			CoverageLimits.COV_EMB_1000
	)),
	LOL(ImmutableList.of(
			CoverageLimits.COV_LOL,
			CoverageLimits.COV_NO_LOL
	)),
  	BI_NY(ImmutableList.of(
			CoverageLimits.COV_2550,
			CoverageLimits.COV_50100,
			CoverageLimits.COV_100300,
			CoverageLimits.COV_250500,
			CoverageLimits.COV_300500,
			CoverageLimits.COV_500500,
			CoverageLimits.COV_5001000,
			CoverageLimits.COV_10001000
	)),
	UM_SUM(ImmutableList.of(
			CoverageLimits.COV_2550,
			CoverageLimits.COV_50100,
			CoverageLimits.COV_100300,
			CoverageLimits.COV_250500,
			CoverageLimits.COV_300500,
			CoverageLimits.COV_500500,
			CoverageLimits.COV_5001000,
			CoverageLimits.COV_10001000
	)),

	COV_PIPLENINCCONT_NJ(ImmutableList.of(
            CoverageLimits.COV_2YR,
            CoverageLimits.COV_UNL
    )),
    COV_PIPCOVINCLUDES_NJ(ImmutableList.of(
            CoverageLimits.COV_PIPCOVINCLUDES_NI,
            CoverageLimits.COV_PIPCOVINCLUDES_NIFM
    )),

	MEE_NY(ImmutableList.of(
			CoverageLimits.COV_MEE_NONE,
			CoverageLimits.COV_MEE_NAMED_INSURED_ONLY,
			CoverageLimits.COV_MEE_NAMED_INSURED_AND_RELATIVES
	)),

	SSL(ImmutableList.of(
			CoverageLimits.COV_FALSE,
			CoverageLimits.COV_TRUE
	));

	private final List<CoverageLimits> availableLimits;

	AvailableCoverageLimits(List<CoverageLimits> availableLimits) {
		this.availableLimits = availableLimits;
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
