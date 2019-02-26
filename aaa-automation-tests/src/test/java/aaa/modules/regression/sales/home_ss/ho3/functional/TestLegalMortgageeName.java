package aaa.modules.regression.sales.home_ss.ho3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestLegalMortgageeName extends HomeSSHO3BaseTest {
    MortgageesTab mortgageeTab = new MortgageesTab();

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO3;
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Additional text boxes for Legal Mortgagee Name")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-24699")
    public void pas24669_testLegalMortgageeNameNB(@Optional("") String state) {

        TestData td = getPolicyTD();

        createQuoteAndFillUpTo(td, MortgageesTab.class, false);
        mortgageeTab.getAssetList().getAsset(HomeSSMetaData.MortgageesTab.MORTGAGEE).setValue("Yes");
        mortgageeTab.getMortgageeInfoAssetList().getAsset(HomeSSMetaData.MortgageesTab.MortgageeInformation
                .USE_LEGAL_MORTGAGEE_FOR_EVIDENCE_OF_INSURANCE).setValue("Yes");

        mortgageeTab.getMortgageeInfoAssetList().getAsset(HomeSSMetaData.MortgageesTab.MortgageeInformation.MORTGAGEE_CLAUSE).setValue("Sreekanth");
        String mortgageeHelpText = "Use the Mortgagee Clause field to override the “Lender Name” entered above. Information entered here will appear on the Evidence of Insurance and the Property Insurance Invoice. Please fill in with all required text by the lender in the format of how the clause should appear.";
        assertThat(MortgageesTab.mortgageeClauseHelpText.getAttribute("title")).contains(mortgageeHelpText);

    }

}
