package aaa.modules.regression.sales.home_ca.dp3.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.modules.policy.HomeCaDP3BaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

import java.util.HashMap;
import java.util.Map;

public class TestCAFairPlanSignature extends HomeCaDP3BaseTest {
    /**
     * @author Robert Boles
     * @name Test CA FAIR Plan Signature - PAS-13239 (AC#1_2_DP3)
     * @scenario 1. Create new Customer;
     * 2. Initiate CAH quote creation, set effective date to today, set Policy Form=DP3;
     * 3. Fill all mandatory fields;
     * 4. Calculate premium without FAIR Plan Endorsement;
     * 5. Add FAIR Plan Companion endorsement;
     * 6. Navigate to Documents tab;
     * 7. Validate that for the FPCECA endorsement, 'Not Signed' is defaulted;
     * 8. Validate that when no signature provided, the user is prevented from binding
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.HOME_CA_DP3)
    public void testPolicyRateFairPlanSignatureDP3(@Optional("CA") String state) {
        testSetup();
        validateSignatureAndBind();
    }

    private void testSetup() {
        mainApp().open();
        createCustomerIndividual();
        policy.createQuote(getPolicyTD());
    }

    private void validateSignatureAndBind() {
        Map<String, String> endorsement_FPCECADP = new HashMap<>();
        endorsement_FPCECADP.put("Form ID", "FPCECADP");
        endorsement_FPCECADP.put("Name", "FAIR Plan Companion Endorsement - California");

        new HomeCaPolicyActions.DataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());

        EndorsementTab endorsementTab = new EndorsementTab();
        //Add the ENDO and verify presence
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECADP.getLabel()).click();
        Page.dialogConfirmation.confirm();

        endorsementTab.btnSaveForm.click();
        Assertions.assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(endorsement_FPCECADP).isPresent());

        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.DOCUMENTS.get());
        Assertions.assertThat(new DocumentsTab().getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FAIR_PLAN_COMPANION_ENDORSEMENT_CALIFORNIA).getValue()).isEqualTo("Not Signed");

        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        new BindTab().submitTab();

        ErrorTab errorTab = new ErrorTab();
        errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_CA20180518);
        mainApp().close();
    }

}