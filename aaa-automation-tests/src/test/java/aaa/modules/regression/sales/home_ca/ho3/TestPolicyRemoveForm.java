package aaa.modules.regression.sales.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants.PolicyEndorsementFormsTable;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;

public class TestPolicyRemoveForm extends HomeCaHO3BaseTest {

    /**
      * @author Jurij Kuznecov
      * @name Test CAH Policy Remove Form
      * @scenario 
      * 1.  Create new or open existent Customer
      * 2.  Initiate HO3 policy creation
      * 3.  Fill tabs till Premiums And Coverages Quote Tab
      * 4.  Add Endorsement Form
      * 5.  Verify that added Endorsement presents on Premiums And Coverages Quote Tab
      * 6.  Navigate back to Endorsement Tab and remove added Endorsement form
      * 7.  Verify that removed Endorsement is not displayed on Premiums And Coverages Quote Tab
      * 8.  Fill all remaining tabs and purchase policy
      * 9.  Verify policy status is 'Active'
      */

	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)  
    public void testPolicyRemoveForm(@Optional("CA") String state) {

        mainApp().open();
        createCustomerIndividual();
        policy.initiate();

        policy.getDefaultView().fillUpTo(getPolicyTD().adjust(getTestSpecificTD("TestData_AddForm_HARI")).resolveLinks(), PremiumsAndCoveragesQuoteTab.class, false);
        assertThat(PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains(PolicyEndorsementFormsTable.DESCRIPTION, HomeCaMetaData.EndorsementTab.HARI.getLabel())).isPresent();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
        policy.getDefaultView().fill(getPolicyTD().ksam(EndorsementTab.class.getSimpleName()).adjust(getTestSpecificTD("TestData_RemoveForm_HARI")).resolveLinks());
        assertThat(PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains(PolicyEndorsementFormsTable.DESCRIPTION, HomeCaMetaData.EndorsementTab.HARI.getLabel())).isPresent(false);
        policy.getDefaultView().fillFromTo(getPolicyTD(), PremiumsAndCoveragesQuoteTab.class, PurchaseTab.class);
        policy.getDefaultView().fill(getPolicyTD().ksam(PurchaseTab.class.getSimpleName()));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
