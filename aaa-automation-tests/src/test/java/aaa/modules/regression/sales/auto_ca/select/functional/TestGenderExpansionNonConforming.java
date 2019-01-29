package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestGenderExpansionNonConformingCATemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestGenderExpansionNonConforming extends TestGenderExpansionNonConformingCATemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    /**
     * @author Sreekanth Kopparapu
     * @name Test Gender Expansion for NonConforming value of X on Applicable Pages - Create Customer, Driver page,
     *        Quote/Policy Summary page, VRD page
     * @scenario
     * 1. Open App with Create Customer page
     * 2. Create customer with Option 'X' in Gender combo box
     * 3. Create Auto Quote, fill mandatory tabs - Prefill and General
     * 4. Navigate to Driver Tab and validate for Gender - X value is prefilled
     * 5. Fill mandatory fields and navigate to P*C
     * 6. Validate VRD Page - Gender X Is displayed
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Gender Expansion for NonConforming value of X")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-23040")
    public void pas23040_ValidateGenderExpansionNonConformingNB(@Optional("CA") String state) {
        pas23040_ValidateGenderExpansionNonConformingNB();

    }

    /**
     * @author Sreekanth Kopparapu
     * @name Test Gender Expansion for NonConforming value of X on Applicable Pages - Create Customer, Driver page,
     * Quote/Policy Summary page, VRD page
     * @scenario 1. Create a policy for Auto SS State and Bind the policy - one NI with gender as Male
     * 2. Initiate an endorsement and add a driver with Gender 'X' in Gender combo box
     * 3. Fill mandatory fields and navigate to P*C, calculate premium
     * 6. Validate VRD Page - Gender X Is displayed in the proposed new driver column
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Gender Expansion for NonConforming value of X")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-23040")
    public void pas23040_ValidateGenderExpansionNonConformingEndTx(@Optional("CA") String state) {

        pas23040_ValidateGenderExpansionNonConformingEndTx();
    }

    /**
     * @author Sreekanth Kopparapu
     * @name Test Gender Expansion for NonConforming value of X on Applicable Pages - Create Customer, Driver page,
     * Quote/Policy Summary page, VRD page
     * @scenario 1. Create a policy for Auto SS State and Bind the policy - one NI with gender as Male
     * 2. Initiate an endorsement and change the gender 'Male' to Gender 'X' in Gender combo box
     * 3. Fill mandatory fields and navigate to P*C, calculate premium
     * 6. Validate VRD Page - Gender X Is displayed in the proposed new driver column
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Gender Expansion for NonConforming value of X")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-23040")
    public void pas23040_ValidateGenderExpansionNonConformingEnd1Tx(@Optional("CA") String state) {

        pas23040_ValidateGenderExpansionNonConformingEnd1Tx();
    }

    /**
     * @author Sreekanth Kopparapu
     * @name Test Gender Expansion for NonConforming value of X on Applicable Pages - Create Customer, Driver page,
     * Quote/Policy Summary page, VRD page
     * @scenario 1. Create a policy for Auto CA Select State and Bind the policy - one NI with gender as Male
     * 2. Initiate an Renewal and change the gender 'Male' to Gender 'X' in Gender combo box
     * 3. Fill mandatory fields and navigate to P*C, calculate premium
     * 6. Validate VRD Page - Gender X Is displayed in the proposed new driver column
     * 7. Propose the policy, pay the min due
     * 8 Renew the policy and assert the updated gender on Policy summary page for the Renewed policy
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT}, description = "Test Gender Expansion for NonConforming value of X")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-23040")
    public void pas23040_ValidateGenderExpansionNonConformingRenewal(@Optional("CA") String state) {

        pas23040_ValidateGenderExpansionNonConformingRenewal();
    }

    /**
     * @author Sreekanth Kopparapu
     * @name Test Gender Expansion for NonConforming value of X on Applicable Pages - Create Customer, Driver page,
     * Quote/Policy Summary page, VRD page
     * @scenario 1. Create a policy for Auto CA Select State and Bind the policy - one NI with gender as Male
     * 2. Initiate an Renewal and add a new driver on the Driver tab with Gender 'X'
     * 3. Fill mandatory fields and navigate to P*C, calculate premium
     * 6. Validate VRD Page - Gender X Is displayed in the proposed new driver column
     * 7. Propose the policy, pay the min due
     * 8 Renew the policy and assert the newly added driver gender on Policy summary page for the Renewed policy
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT}, description = "Test Gender Expansion for NonConforming value of X")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-23040")
    public void pas23040_ValidateGenderExpansionNonConformingRenewal1(@Optional("CA") String state) {

        pas23040_ValidateGenderExpansionNonConformingRenewal1();
    }

    /**
     * @author Sreekanth Kopparapu
     * @name Test Additional options available for 'Rel to First Named Insured' field on Driver Tab
     * @scenario 1. Create a Quote for Auto CA Select
     * 2. Fill in mandatory fields and navigate to Driver Tab
     * 3. For Rel to First Named Insured Field Validate the newly added options in the dropdown list First Named Insured
     *     Spouse, Son, Daughter, Other, Father, Mother, Domestic Partner, Other Resident Relative, Child, Parent, Sibling
     *     Employee
     * 4. Add a new driver with parent as value for Rel to Named Insured field
     * 5. Bind the policy
     * 6. Initiate an endorsement and
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-23279")
    public void pas23279_ValidateRelToFirstNamedInsuredListNB(@Optional("CA") String state) {

        pas23279_ValidateRelToFirstNamedInsuredListNBEndTx();

    }


}
