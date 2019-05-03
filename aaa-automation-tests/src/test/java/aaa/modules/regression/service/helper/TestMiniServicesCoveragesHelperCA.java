package aaa.modules.regression.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
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
		if (TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().isAfter(LocalDate.parse("2019-06-06"))) { //Starting from 2019-06-07, PD available limit 5000 is removed
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
}
