package aaa.modules.regression.sales.home_ca.ho3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestCAFairPlanSignature extends HomeCaHO3BaseTest {
    /**
     * @author Robert Boles
	 * <b> Test CA FAIR Plan Signature - PAS-13239 (AC#1_2_HO3) </b>
	 * <p> Steps: 1. Create new Customer;
	 * <p> 2. Initiate CAH quote creation, set effective date to today, set Policy Form=HO3;
	 * <p> 3. Fill all mandatory fields;
	 * <p> 4. Calculate premium without FAIR Plan Endorsement;
	 * <p> 5. Add FAIR Plan Companion endorsement;
	 * <p> 6. Navigate to Documents tab;
	 * <p> 7. Validate that for the FPCECA endorsement, 'Not Signed' is defaulted;
	 * <p> 8. Validate that when no signature provided, the user is prevented from binding
	 *
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "PAS-13239 18.5: CA FAIR Plan: Signature required to bind HO3")
    @TestInfo(component = ComponentConstant.Service.HOME_CA_HO3,  testCaseId = "PAS-13239")
    public void testPolicyRateFairPlanSignatureHO3(@Optional("CA") String state) {
        testSetup();
        validateSignatureAndBind();
    }

    private void testSetup() {
        mainApp().open();
        createCustomerIndividual();
        policy.createQuote(getPolicyTD());
    }

    private void validateSignatureAndBind() {
        Map<String, String> endorsement_FPCECA = new HashMap<>();
        endorsement_FPCECA.put("Form ID", "FPCECA");
        endorsement_FPCECA.put("Name", "FAIR Plan Companion Endorsement - California");

        new HomeCaPolicyActions.DataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());

        EndorsementTab endorsementTab = new EndorsementTab();
        //Add the ENDO and verify presence
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECA.getLabel()).click();

        endorsementTab.btnSaveEndo.click();
	    assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(endorsement_FPCECA)).exists();

        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.DOCUMENTS.get());
        assertThat(new DocumentsTab().getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FAIR_PLAN_COMPANION_ENDORSEMENT_CALIFORNIA)).hasValue("Not Signed");

        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        new BindTab().btnPurchase.click();
        ErrorTab errorTab = new ErrorTab();
        assertThat(errorTab.getErrorCodesList().contains(ErrorEnum.Errors.ERROR_AAA_HO_CA20180517.getCode())).isTrue();
    }



}