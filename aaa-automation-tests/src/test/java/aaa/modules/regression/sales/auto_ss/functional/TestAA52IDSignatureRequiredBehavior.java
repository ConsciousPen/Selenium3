package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;

@StateList(states = Constants.States.ID)
public class TestAA52IDSignatureRequiredBehavior extends AutoSSBaseTest {

	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private ComboBox umCoverage = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY);
	private ComboBox uimCoverage = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORISTS_BODILY_INJURY);
	private RadioGroup disclosureStmtRadioBtn = documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_UNDERINSURED_DISCLOSURE_STATEMENT_AND_REJECTION_OF_COVERAGE);
	public static final int ENDORSEMENT = 1;
	public static final int RENEWAL = 2;

	/**
	 * @author Josh Carpenter
	 * @name Test that the signature option in the 'Required to Bind' section is reset after adding/removing UM/UIM coverage during Endorsement
	 * @scenario
	 * 1.  Create Customer
	 * 2.  Create Auto SS ID Policy
	 * 3.  Initiate endorsement and navigate to Documents & Bind Tab
	 * 4.  Validate the signature option is set to anything except 'Not Signed'
	 * 5.  Reject UM Coverage and validate signature option
	 * 6.  Reject UIM Coverage and validate signature option
	 * 7.  Add UM Coverage and validate signature option
	 * 8.  Add UIM Coverage and validate signature option
	 * 9.  Reject both UM & UIM and validate signature option
	 * 10. Add both UM & UIM and validate signature option
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = "PAS-18892")
	public void pas18892_testResetSignatureOptionEndorsement(@Optional("ID") String state) {

		openAppAndCreatePolicy();
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

        testResetSignatureRejectUM(ENDORSEMENT, PolicyConstants.SignatureStatus.NOT_SIGNED);
		testResetSignatureAddUM(ENDORSEMENT, PolicyConstants.SignatureStatus.NOT_SIGNED);
        testResetSignatureRejectUIM(ENDORSEMENT, PolicyConstants.SignatureStatus.NOT_SIGNED);
		testResetSignatureAddUIM(ENDORSEMENT, PolicyConstants.SignatureStatus.NOT_SIGNED);
        testResetSignatureRejectBothCoverages(ENDORSEMENT, PolicyConstants.SignatureStatus.NOT_SIGNED);
        testResetSignatureAddBothCoverages(ENDORSEMENT, PolicyConstants.SignatureStatus.NOT_SIGNED);
        documentsAndBindTab.submitTab();

	}

	/**
	 * @author Josh Carpenter
	 * @name Test that the signature option in the 'Required to Bind' section is reset after adding/removing UM/UIM coverage during Renewal
	 * @scenario
	 * 1.  Create Customer
	 * 2.  Create Auto SS ID Policy
	 * 3.  Create renewal image and navigate to Documents & Bind Tab
	 * 4.  Validate the signature option is set to anything except 'Not Signed'
	 * 5.  Reject UM Coverage and validate signature option
	 * 6.  Reject UIM Coverage and validate signature option
	 * 7.  Add UM Coverage and validate signature option
	 * 8.  Add UIM Coverage and validate signature option
	 * 9.  Reject both UM & UIM and validate signature option
	 * 10. Add both UM & UIM and validate signature option
	 * 11. Save renewal
	 * 12. Create second renewal version
	 * 13. Change
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Renewal.AUTO_SS, testCaseId = "PAS-18892")
	public void pas18892_testResetSignatureOptionRenewal(@Optional("ID") String state) {

		openAppAndCreatePolicy();
		policy.renew().perform();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

        testResetSignatureRejectUM(RENEWAL, PolicyConstants.SignatureStatus.NOT_SIGNED);
		testResetSignatureAddUM(RENEWAL, PolicyConstants.SignatureStatus.PHYSICALLY_SIGNED);
        testResetSignatureRejectUIM(RENEWAL, PolicyConstants.SignatureStatus.NOT_SIGNED);
		testResetSignatureAddUIM(RENEWAL, PolicyConstants.SignatureStatus.PHYSICALLY_SIGNED);
		testResetSignatureRejectBothCoverages(RENEWAL, PolicyConstants.SignatureStatus.NOT_SIGNED);
        testResetSignatureAddBothCoverages(RENEWAL, PolicyConstants.SignatureStatus.PHYSICALLY_SIGNED);
        documentsAndBindTab.submitTab();

	}

	private void testResetSignatureRejectUM(int txType, String sigStatus) {

		// Reject UM Coverage and validate signature option
		setCoverage(umCoverage, Coverage.NONE);
		validateSignatureStatusOnBindTab(sigStatus);
        initiateNavigateToBindTab(txType);

	}

	private void testResetSignatureRejectUIM(int txType, String sigStatus) {

		// Reject UIM Coverage and validate signature option
		setCoverage(uimCoverage, Coverage.NONE);
		validateSignatureStatusOnBindTab(sigStatus);
        initiateNavigateToBindTab(txType);

	}

	private void testResetSignatureAddUM(int txType, String sigStatus) {

		// Add UM Coverage and validate signature option
		setCoverage(umCoverage, Coverage.COV_100_300);
		validateSignatureStatusOnBindTab(sigStatus);
        initiateNavigateToBindTab(txType);
	}

	private void testResetSignatureAddUIM(int txType, String sigStatus) {

		// Add UIM Coverage and validate signature option
		setCoverage(uimCoverage, Coverage.COV_100_300);
		validateSignatureStatusOnBindTab(sigStatus);
        initiateNavigateToBindTab(txType);

	}


	private void testResetSignatureAddBothCoverages(int txType, String sigStatus) {

        // Add both UM & UIM and validate signature option
        setCoverage(umCoverage, Coverage.COV_100_300);
        setCoverage(uimCoverage, Coverage.COV_100_300);
        validateSignatureStatusOnBindTab(sigStatus);
        initiateNavigateToBindTab(txType);

    }

    private void testResetSignatureRejectBothCoverages(int txType, String sigStatus) {

        // Reject both UM & UIM and validate signature option
        setCoverage(umCoverage, Coverage.NONE);
        setCoverage(uimCoverage, Coverage.NONE);
        validateSignatureStatusOnBindTab(sigStatus);
        initiateNavigateToBindTab(txType);

   }

	private void setCoverage(ComboBox coverage, String value) {
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		coverage.setValueContains(value);
		premiumAndCoveragesTab.calculatePremium();
	}

	private void validateSignatureStatusOnBindTab(String sigType) {
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        assertThat(disclosureStmtRadioBtn.getValue()).isEqualTo(sigType);
        disclosureStmtRadioBtn.setValue(PolicyConstants.SignatureStatus.PHYSICALLY_SIGNED);
        documentsAndBindTab.submitTab();
    }

	private void initiateNavigateToBindTab(int txType) {

		if (txType == 1) {
			policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		} else if (txType == 2) {
			PolicySummaryPage.buttonRenewals.click();
			policy.dataGather().start();
		}

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
	}


	private final class Coverage {
		static final String NONE = "No Coverage";
		static final String COV_100_300 = "$100,000/$300,000";
	}
}
