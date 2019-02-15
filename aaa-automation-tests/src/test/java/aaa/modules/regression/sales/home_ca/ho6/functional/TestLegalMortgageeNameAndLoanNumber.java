package aaa.modules.regression.sales.home_ca.ho6.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestMortgageeNameAndLoanNumberCATemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestLegalMortgageeNameAndLoanNumber extends TestMortgageeNameAndLoanNumberCATemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_HO6;
    }

    /**
     * @author Sreekanth Kopparapu
     * @name Test Loan Number is mandatory on Mortgagee & Additional Interests Tab
     * 1. Create a Quote for HO6 and navigate till Mortgagee & Additional Interests Tab
     * 2. Select Option "Yes' for Mortgagee and Mortgagee Information section is enabled
     * 3. Make sure Loan Number is a mandatory field
     * 4. Validate an error is displayed when trying to navigate to next tab without providing Loan Number
     * 5. Tab out to Bind Tab and purchase the policy
     * 6. Hard stop rule error -'AAA_HO_CA7230928' is displayed
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "PAS-6214-Validate Loan Number being Mandatory")
    @TestInfo(component = ComponentConstant.Service.HOME_CA_HO6,  testCaseId = "PAS-6214")
    public void pas6214_testMortgageeLoanNumberNB(@Optional("CA") String state) {

        pas6214_testMortgageeLoanNumberNB();

    }

    /**
     * @author Sreekanth Kopparapu
     * @name Test Loan Number is mandatory on Mortgagee & Additional Interests @Endorsement
     * 1. Create a Policy for HO6
     * 2. Initiate an endorsement and navigate till Mortgagee & Additional Interests Tab
     * 3. Select Option "Yes' for Mortgagee and Mortgagee Information section is enabled
     * 4 .Make sure Loan Number is a mandatory field
     * 5. Validate an error is displayed when trying to navigate to next tab without providing Loan Number
     * 6. Tab out to Bind Tab and purchase the policy
     * 7. Hard stop rule error -'AAA_HO_CA7230928' is displayed
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "PAS-6214-Validate Loan Number being Mandatory")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-6214")
    public void pas6214_testMortgageeLoanNumberEndTx(@Optional("CA") String state) {

        pas6214_testMortgageeLoanNumberEndTx();

    }

    /**
     * @author Sreekanth Kopparapu
     * @name Test Loan Number is mandatory on Mortgagee & Additional Interests @Renewal
     * 1. Create a Policy for HO6
     * 2. Initiate an Renewal and navigate till Mortgagee & Additional Interests Tab
     * 3. Select Option "Yes' for Mortgagee and Mortgagee Information section is enabled
     * 4 .Make sure Loan Number is a mandatory field
     * 5. Validate an error is displayed when trying to navigate to next tab without providing Loan Number
     * 6. Tab out to Bind Tab and purchase the policy
     * 7. Hard stop rule error -'AAA_HO_CA7230928' is displayed
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "PAS-6214-Validate Loan Number being Mandatory")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-6214")
    public void pas6214_testMortgageeLoanNumberRenewal(@Optional("CA") String state) {

        pas6214_testMortgageeLoanNumberRenewal();
    }

}