package aaa.modules.regression.sales.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.table.Table;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants.PolicyEndorsementFormsTable;
import aaa.main.enums.PolicyConstants.PolicyIncludedAndSelectedEndorsementsTable;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.HomeCaHO3BaseTest;

public class TestQuoteViewSelectedEndorsements extends HomeCaHO3BaseTest {

    /**
     * @author Jurij Kuznecov
     * @name Test CAH Quote View Selected Endorsements
     * @scenario 
     * 1.  Create new or open existent Customer
     * 2.  Start creation of HO3 policy
     * 3.  Add one structure rented to others and excluded animal in the PropertyInfo tab
     * 4.  Add one Mortgagee, one Additional Insured and one Additional Interest in the Mortgagee tab
     * 5.  Navigate to Endorsements tab, verify forms are selected:
     *     HO-28    - Limited Home Replacement Cost 
     *     HO-90    - Workers' Compensation and Employers' Liability Insurance
     *     HO-40    - Other Structures - Rented To Others 
     *     HO-41    - Additional Insured - Residence Premises 
     *     HO 04 10 - Additional Interests - Residence Premises
     *     F1759C   - Animal Exclusion Endorsement 
     * 6.  Navigate to Quote tab and verify the same 
     */

    @Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void testQuoteViewSelectedEndorsements(@Optional("CA") String state) {

        List<String> selectedEndorsements = initEndorsements();

        mainApp().open();
        createCustomerIndividual();

        policy.createQuote(getTestSpecificTD("TestData"));

        new HomeCaPolicyActions.DataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        EndorsementTab endorsementTab = new EndorsementTab();
        verifySelectedEndorsementsPresent(endorsementTab.tblIncludedEndorsements, PolicyIncludedAndSelectedEndorsementsTable.FORM_ID, selectedEndorsements);
        endorsementTab.submitTab();
        verifySelectedEndorsementsPresent(PremiumsAndCoveragesQuoteTab.tableEndorsementForms, PolicyEndorsementFormsTable.DESCRIPTION, selectedEndorsements);
    }

    private List<String> initEndorsements() {
        List<String> forms = new ArrayList<>();
        forms.add(HomeCaMetaData.EndorsementTab.HO_28.getLabel());
        forms.add(HomeCaMetaData.EndorsementTab.HO_90.getLabel());
        forms.add(HomeCaMetaData.EndorsementTab.HO_40.getLabel());
        forms.add(HomeCaMetaData.EndorsementTab.HO_41.getLabel());
        forms.add(HomeCaMetaData.EndorsementTab.HO_04_10.getLabel());
        forms.add(HomeCaMetaData.EndorsementTab.F1759C.getLabel());
        return forms;
    }

    private void verifySelectedEndorsementsPresent(Table tableForms, String columnName, List<String> selectedEndorsements) {
        for (String form : selectedEndorsements) {
            assertThat(tableForms.getRowContains(columnName, form)).isPresent();
        }
    }
}
