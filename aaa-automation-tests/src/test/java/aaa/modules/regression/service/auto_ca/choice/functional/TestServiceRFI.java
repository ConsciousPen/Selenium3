package aaa.modules.regression.service.auto_ca.choice.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.CoverageLimits;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.modules.regression.service.helper.HelperMiniServices;
import aaa.modules.regression.service.helper.TestRFIHelper;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

import static aaa.main.enums.ErrorEnum.Errors.ERROR_AAA_CSA6100815;

public class TestServiceRFI extends TestRFIHelper {

    /**
     * @name RFI AA52UPAA Form
     * @scenario 1
     * 1. Create policy.
     * 2. Create endorsement outside of PAS.
     * 3. Rate. Hit RFI service.
     * 4. Check the response.
     * 5. Update UMBI coverage. Rate.
     * 6. Hit RFI service, check if document is displaying.
     * 7. Run bind service without signing document and verify error. and policy is not bound.
     * 8. Run bind service with document id verify no error and we can bind the policy.
     * 9. go to pas UI and verify if policy is bound
     * 10. Go to document and bind page and verify if document is electronically signed.
     *
     * 11. create an endorsement on policy from pas change coverage and rate the policy
     * 12. go to document and bind page verify if its reset to document not signed
     * 13. Try to bind policy from pas and verify error.
     * 14. Select document physically signed
     * 15.  Bind the policy verify there is no error message.
     *
     * @scenario 2
     * 1. Create policy and override the rule
     * 2. Create endorsement outside of PAS.
     * 3. Trigger the document by updating one of the coverages (UMBI or UMSU)
     * 4. Hit RFI service and check that docuemnt is returned
     * 5. Bind Endorsement ---> No rule is fired (as it was overriden at NB)
     *
     */
    @Parameters({"state"})
    @StateList(states = {Constants.States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-27225"})
    public void pas27225_550007(@Optional("CA") String state) {
        DocGenEnum.Documents document = DocGenEnum.Documents._550007;
        AssetDescriptor<RadioGroup> documentAsset = AutoCaMetaData.DocumentsAndBindTab.DocumentsForPrinting.OPERATOR_EXCLUSION_ENDORSEMENT_AND_UNINSURED_MOTORIST_COVERAGE;
        ErrorEnum.Errors error = ERROR_AAA_CSA6100815;

        // scenario 1
        TestData td = getPolicyDefaultTD();
        verifyRFIScenarios("UMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY, CoverageLimits.COV_2550.getLimit(), CoverageLimits.COV_00.getDisplay(), document, documentAsset, error, td, true, false);

        // sceanrio 2
        // Create policy and override rule
        td.adjust(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(), AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNUNSURED_MOTORISTS_COVERAGE_SELECTION_REJECTION.getLabel()), "Not Signed");
        TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
        td = td.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();
        verifyRFIScenarios("UMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY, CoverageLimits.COV_1530.getLimit(), CoverageLimits.COV_2550.getDisplay(), document, documentAsset, error, td, true, true);
    }
}
