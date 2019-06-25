package aaa.modules.regression.service.auto_ca.select.functional;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.CoverageLimits;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.regression.service.helper.TestRFIHelper;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.AbstractEditableStringElement;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

import static aaa.main.enums.ErrorEnum.Errors.ERROR_AAA_CSA6100815;

public class TestServiceRFI extends TestRFIHelper {
    private final DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    private final PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    /**
     * @name RFI 550007 CA Form
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
     * 3. Trigger the document by updating UMBI
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
        AssetDescriptor<RadioGroup> documentAsset = AutoCaMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_MOTORIST_COVERAGE_DELETION_OR_SELECTION_OF_LIMITS_AGREEMENT;
        ErrorEnum.Errors error = ERROR_AAA_CSA6100815;

        // scenario 1
        TestData td = getPolicyDefaultTD();
        verifyRFIScenarios("UMBI", AutoCaMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY, CoverageLimits.COV_2550.getLimit(), CoverageLimits.COV_00.getDisplay(), document, documentAsset, error, td, false, false);

        // sceanrio 2
        // Create policy and override rule
        td.adjust(TestData.makeKeyPath(AutoCaMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY.getLabel()), "contains=$25,000");
        td.adjust(TestData.makeKeyPath(AutoCaMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoCaMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(), AutoCaMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_MOTORIST_COVERAGE_DELETION_OR_SELECTION_OF_LIMITS_AGREEMENT.getLabel()), "Not Signed");
        TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
        td = td.adjust(AutoCaMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();
        verifyRFIScenarios("UMBI", AutoCaMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY, CoverageLimits.COV_1530.getLimit(), CoverageLimits.COV_2550.getDisplay(), document, documentAsset, error, td, true, true);
    }

    @Override
    protected AssetList getDocumentAssetList() {
        return documentsAndBindTab.getRequiredToBindAssetList();
    }

    @Override
    protected Tab getDocumentsAndBindTab() {
        return documentsAndBindTab;
    }

    @Override
    protected void updatePremiumAndCoveragesTab(AssetDescriptor<? extends AbstractEditableStringElement> coverageAsset, String coverageLimit) {
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.getAssetList().getAsset(coverageAsset).setValue("contains=" + coverageLimit);
        premiumAndCoveragesTab.calculatePremium();
    }
}
