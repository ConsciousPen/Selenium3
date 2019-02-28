package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.modules.policy.PolicyBaseTest;
import static org.assertj.core.api.Assertions.assertThat;

public class TestLegalMortgageeNameSSTemplate extends PolicyBaseTest {

    MortgageesTab mortgageesTab = new MortgageesTab();

    private String pas24699MortgageeName =  "CSAA Insurance LLC\n"+
                                            "5353 W Bell Rd\n"+
                                            "Glendale AZ 85038";

    protected void pas24669_testLegalMortgageeNameNB(){

       createQuoteAndFillUpTo(getPolicyTD(), MortgageesTab.class, false);
       validateMortgageeClause();

    }

    protected void pas24699_testMortgageeClauseSSEndTx(){

        openAppAndCreatePolicy(getPolicyTD());
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.MORTGAGEE_AND_ADDITIONAL_INTERESTS.get());
        validateMortgageeClause();
    }

    protected void pas24699_testMortgageeClauseSSRenewal(){
        openAppAndCreatePolicy(getPolicyTD());
        policy.renew().perform();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.MORTGAGEE_AND_ADDITIONAL_INTERESTS.get());
        validateMortgageeClause();

    }

    private void validateMortgageeClause(){

        mortgageesTab.getAssetList().getAsset(HomeSSMetaData.MortgageesTab.MORTGAGEE).setValue("Yes");
        mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeSSMetaData.MortgageesTab.MortgageeInformation
                .USE_LEGAL_MORTGAGEE_FOR_EVIDENCE_OF_INSURANCE).setValue("Yes");

        mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeSSMetaData.MortgageesTab.MortgageeInformation.MORTGAGEE_CLAUSE).setValue("Sreekanth");
        String mortgageeHelpText = "Use the Mortgagee Clause field to override the “Lender Name” entered above. Information entered here will appear on the Evidence of Insurance and the Property Insurance Invoice. Please fill in with all required text by the lender in the format of how the clause should appear.";
        assertThat(MortgageesTab.mortgageeClauseHelpText.getAttribute("title")).contains(mortgageeHelpText);
        mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeSSMetaData.MortgageesTab.MortgageeInformation.MORTGAGEE_CLAUSE).setValue(pas24699MortgageeName);
        String mortgageeClauseValue = mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeSSMetaData.MortgageesTab.MortgageeInformation.MORTGAGEE_CLAUSE).getValue();
        assertThat(mortgageeClauseValue).isEqualTo(pas24699MortgageeName);
    }

}
