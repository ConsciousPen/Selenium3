package aaa.modules.regression.sales.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
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
import aaa.utils.StateList;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.table.Table;

public class TestQuoteViewSelectedEndorsements extends HomeCaHO3BaseTest {

    /**
     * @author Jurij Kuznecov
	 * <b> Test CAH Quote View Selected Endorsements </b>
	 * <p> Steps:
	 * <p> 1.  Create new or open existent Customer
	 * <p> 2.  Start creation of HO3 policy
	 * <p> 3.  Add one structure rented to others and excluded animal in the PropertyInfo tab
	 * <p> 4.  Add one Mortgagee, one Additional Insured and one Additional Interest in the Mortgagee tab
	 * <p> 5.  Navigate to Endorsements tab, verify forms are selected:
	 * <p>     HO-28    - Limited Home Replacement Cost
	 * <p>     HO-90    - Workers' Compensation and Employers' Liability Insurance
	 * <p>     HO-40    - Other Structures - Rented To Others
	 * <p>     HO-41    - Additional Insured - Residence Premises
	 * <p>     HO 04 10 - Additional Interests - Residence Premises
	 * <p>     F1759C   - Animal Exclusion Endorsement
	 * <p> 6.  Navigate to Quote tab and verify the same
     */

    @Parameters({"state"})
    @StateList(states =  States.CA)
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
