package aaa.modules.regression.sales.home_ss.ho6.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestKYCityAndCountyTaxesForEndorsementFormsTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.KY)
public class TestKYCityAndCountyTaxesForEndorsementForms extends TestKYCityAndCountyTaxesForEndorsementFormsTemplate {

     @Override
     protected PolicyType getPolicyType() {

         return PolicyType.HOME_SS_HO6;
     }

    /**
     * @author Sreekanth Kopparapu
     * @name Test - City and County tax calculated = Percentage value * (Dwelling address + Endorsement Forms) from the calculated premium
     * @scenario 1.  Create Customer
     * 2.  Create HO6 - KY Policy
     * 3.  Initiate NB Quote and navigate to P*C Tab > Endorsements (Other Endorsements tab)
     * 4.  Add two endorsements -
     * 4.a - HS 04 53 Credit Card Electronic Fund Transfer etc Increased Limit	and select the Coverage limit as '5000' and Save it
     * 4.b - HS 09 34	Rebuild To Green - Save it
     * 5.  Navigate to Quote tab
     * 6.  Click on Calculate Premium
     * 7.  Under the Endorsement forms section the newly added forms are showed up with the calculated Premium
     * 8.  City Tax is 10% per the mock sheet
     * 9.  County Tax is 20% and the value calculated should be the 20% of [Dwelling Address Premium + Total Premium for endorsement forms]
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.HOME_SS_HO6, testCaseId = "PAS-17089")
    public void pas17089_testKYCityAndCountyTaxesNBTx(@Optional("KY") String state) {

        testKYCityAndCountyTaxesNBTx();

    }

    /**
     * @author Sreekanth Kopparapu
     * @name Test - City and County tax calculated = Percentage value * (Dwelling address + Endorsement Forms) from the calculated premium
     * @scenario 1.  Create Customer
     * 2.  Create HO6 - KY Policy
     * 3.  Initiate endorsement and navigate to P*C Tab > Endorsements (Other Endorsements tab)
     * 4.  Add two endorsements -
     * 4.a - HS 04 53 Credit Card Electronic Fund Transfer etc Increased Limit	and select the Coverage limit as '5000' and Save it
     * 4.b - HS 09 34	Rebuild To Green - Save it
     * 5.  Navigate to Quote tab
     * 6.  Click on Calculate Premium
     * 7.  Under the Endorsement forms section the newly added forms are showed up with the calculated Premium
     * 8.  City Tax is 10% per the mock sheet
     * 9.  County Tax is 20% and the value calculated should be the 20% of [Dwelling Address Premium + Total Premium for endorsement forms]
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.HOME_SS_HO6, testCaseId = "PAS-17089")
    public void pas17089_testKYCityAndCountyTaxesEndorsementsTx(@Optional("KY") String state) {

        testKYCityAndCountyTaxesEndorsementsTx();

    }

    /**
     * @author Sreekanth Kopparapu
     * @name Test - City and County tax calculated = Percentage value * (Dwelling address + Endorsement Forms) from the calculated premium
     * @scenario 1.  Create Customer
     * 2.  Create HO6 - KY Policy
     * 3.  Initiate Renewal and navigate to P*C Tab > Endorsements (Other Endorsements tab)
     * 4.  Add two endorsements -
     * 4.a - HS 04 53 Credit Card Electronic Fund Transfer etc Increased Limit	and select the Coverage limit as '5000' and Save it
     * 4.b - HS 09 34	Rebuild To Green - Save it
     * 5.  Navigate to Quote tab
     * 6.  Click on Calculate Premium
     * 7.  Under the Endorsement forms section the newly added forms are showed up with the calculated Premium
     * 8.  City Tax is 10% per the mock sheet
     * 9.  County Tax is 20% and the value calculated should be the 20% of [Dwelling Address Premium + Total Premium for endorsement forms]
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.HOME_SS_HO6, testCaseId = "PAS-17089")
    public void pas17089_testKYCityAndCountyTaxesRenewalTx(@Optional("KY") String state) {

        testKYCityAndCountyTaxesRenewalTx();

    }

}