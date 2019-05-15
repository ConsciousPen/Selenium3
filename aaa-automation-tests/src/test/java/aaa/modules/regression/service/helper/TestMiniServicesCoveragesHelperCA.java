package aaa.modules.regression.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.rest.dtoDxp.Coverage;
import aaa.helpers.rest.dtoDxp.PolicyCoverageInfo;
import aaa.main.enums.CoverageInfo;
import aaa.main.enums.CoverageLimits;

public class TestMiniServicesCoveragesHelperCA extends TestMiniServicesCoveragesHelper {

	protected void pas15412_viewCAPolicyLevelCoveragesBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		validateViewPolicyCoveragesIsTheSameAsViewEndorsementCoverage(policyNumber, viewEndorsementCoverages);

		Coverage covBIExpected = Coverage.create(CoverageInfo.BI_CA);
		Coverage covPDExpected = Coverage.create(CoverageInfo.PD_CA);
		if (TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().isAfter(LocalDate.parse("2019-06-07"))) { //Starting from 2019-06-08, PD should not have availableLimit 5000
			covPDExpected.removeAvailableLimit(CoverageLimits.COV_5000);
		}
		Coverage covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).removeAvailableLimitsAbove(CoverageLimits.COV_500500);
		Coverage covUIMBIExpected = Coverage.create(CoverageInfo.UIMBI_CA).disableCanChange();
		Coverage covMEDPMExpected = Coverage.create(CoverageInfo.MEDPM_CA);

		Coverage covBIActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.BI_CA.getCode());
		Coverage covPDActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.PD_CA.getCode());
		Coverage covUMBIActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.UMBI_CA.getCode());
		Coverage covUIMBIActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.UIMBI_CA.getCode());
		Coverage covMEDPMActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.MEDPM_CA.getCode());

		assertThat(covBIActual).isEqualTo(covBIExpected);
		assertThat(covPDActual).isEqualTo(covPDExpected);
		assertThat(covUMBIActual).isEqualTo(covUMBIExpected);
		assertThat(covUIMBIActual).isEqualToIgnoringGivenFields(covUIMBIExpected, "availableLimits");//don't care about available limits as UIMBI is not changable
		assertThat(covMEDPMActual).isEqualTo(covMEDPMExpected);

	}

	protected void pas28579_updatePolicyLevelCoveragesCABody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//Update BI to lower limit
		CoverageLimits biNewLimit = CoverageLimits.COV_1530;
		Coverage covBIExpected = Coverage.create(CoverageInfo.BI_CA).changeLimit(biNewLimit);
		Coverage covPDExpected = Coverage.create(CoverageInfo.PD_CA);//the same as at NB
		Coverage covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).changeLimit(biNewLimit).removeAvailableLimitsAbove(biNewLimit);
		Coverage covUIMBIExpected = Coverage.create(CoverageInfo.UIMBI_CA).changeLimit(biNewLimit).removeAvailableLimitsAbove(biNewLimit).disableCanChange();
		Coverage covMEDPMExpected = Coverage.create(CoverageInfo.MEDPM_CA);//the same as at NB
		updateCoverageAndCheck(policyNumber, covBIExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update BI to higher limit
		biNewLimit = CoverageLimits.COV_5001000;
		covBIExpected = Coverage.create(CoverageInfo.BI_CA).changeLimit(biNewLimit);
		covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).changeLimit(biNewLimit).removeAvailableLimitsAbove(biNewLimit);
		covUIMBIExpected = Coverage.create(CoverageInfo.UIMBI_CA).changeLimit(biNewLimit).removeAvailableLimitsAbove(biNewLimit).disableCanChange();
		updateCoverageAndCheck(policyNumber, covBIExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update PD to higher limit
		covPDExpected = Coverage.create(CoverageInfo.PD_CA).changeLimit(CoverageLimits.COV_1000000);
		updateCoverageAndCheck(policyNumber, covPDExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update PD to lower limit
		covPDExpected = Coverage.create(CoverageInfo.PD_CA).changeLimit(CoverageLimits.COV_10000);
		updateCoverageAndCheck(policyNumber, covPDExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update MEDPM to higher limit
		covMEDPMExpected = Coverage.create(CoverageInfo.MEDPM_CA).changeLimit(CoverageLimits.COV_25000);
		updateCoverageAndCheck(policyNumber, covMEDPMExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update MEDPM to lower limit
		covMEDPMExpected = Coverage.create(CoverageInfo.MEDPM_CA).changeLimit(CoverageLimits.COV_0);
		updateCoverageAndCheck(policyNumber, covMEDPMExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update MEDPM to other limit than at NB for bind
		covMEDPMExpected = Coverage.create(CoverageInfo.MEDPM_CA).changeLimit(CoverageLimits.COV_1000);
		updateCoverageAndCheck(policyNumber, covMEDPMExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update UM to lower limit
		CoverageLimits newUMBILimit = CoverageLimits.COV_1530;
		covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).changeLimit(newUMBILimit).removeAvailableLimitsAbove(biNewLimit);
		covUIMBIExpected = Coverage.create(CoverageInfo.UIMBI_CA).changeLimit(newUMBILimit).removeAvailableLimitsAbove(biNewLimit).disableCanChange();
		updateCoverageAndCheck(policyNumber, covUMBIExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update UM to higher limit
		newUMBILimit = CoverageLimits.COV_3060;
		covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).changeLimit(newUMBILimit).removeAvailableLimitsAbove(biNewLimit);
		covUIMBIExpected = Coverage.create(CoverageInfo.UIMBI_CA).changeLimit(newUMBILimit).removeAvailableLimitsAbove(biNewLimit).disableCanChange();
		updateCoverageAndCheck(policyNumber, covUMBIExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update BI to lower limit and see what happens
		biNewLimit = CoverageLimits.COV_50100;
		covBIExpected = Coverage.create(CoverageInfo.BI_CA).changeLimit(biNewLimit);
		covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).changeLimit(biNewLimit).removeAvailableLimitsAbove(biNewLimit);
		covUIMBIExpected = Coverage.create(CoverageInfo.UIMBI_CA).changeLimit(biNewLimit).removeAvailableLimitsAbove(biNewLimit).disableCanChange();
		updateCoverageAndCheck(policyNumber, covBIExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}
}
