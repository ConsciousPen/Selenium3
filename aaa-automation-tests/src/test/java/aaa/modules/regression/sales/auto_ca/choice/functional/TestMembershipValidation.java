package aaa.modules.regression.sales.auto_ca.choice.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;

public class TestMembershipValidation extends AutoCaChoiceBaseTest {
    /**
     * @author Andrejs Mitjukovs
     * @name Test Membership validation and override.
     * @scenario
     * 1. Create Customer.
     * 2. Create Auto Select.
     * 3. Add member number on General tab with mismatching First Name, Last Name and DOB.
     * 4. Fill All other required data up to Documents and Bind Tab.
     * 5. Verify that Error "Membership Validation Failed. Please review the Membership Report and confirm..."
     * 6. Override the error and bind.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-3786")
    public void pas3786_ScenarioAC1_Validate_Override(@Optional("CA") String state) {

        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_MembershipValidation").resolveLinks());

        mainApp().open();
        createCustomerIndividual();
        log.info("Policy Creation Started...");

        policy.initiate();

        //class_td contains erroneous data, will fire Override rule, last name != membership last name = override.
        policy.getDefaultView().fillUpTo(testData, DocumentsAndBindTab.class, true);
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME,true);

        DocumentsAndBindTab docTab = new DocumentsAndBindTab();
        docTab.submitTab();
        new ErrorTab().overrideAllErrors();
        docTab.submitTab();

        new PurchaseTab().fillTab(testData).submitTab();

        mainApp().close();
    }


    /**
     * @author Andrejs Mitjukovs
     * @name Test Membership validation for non-Primary membership members returned in membership Report and override.
     * @scenario
     * 1. Create Customer.
     * 2. Create Auto Select.
     * 3. Add member number on General tab with matching First Name and Last Name with membership response 1st member.
     * 4. Fill All other required data up to Documents and Bind Tab.
     * 5. Verify that Error "Membership Validation Failed. Please review the Membership Report and confirm..."
     * 6  Change First Name and Last Name (dataset "TestData_Membership_Valid_First_Name") to match only First name of the Associated Member (2nd member) in membership response.
     * 7. Fill All other required data up to Documents and Bind Tab.
     * 8. Verify that Error "Membership Validation Failed. Please review the Membership Report and confirm..."
     * 9. Repeat steps 3-5 with matching Last Name only and DOB only
     * 10. Override the error and bind.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-3786")
    public void pas6800_pas3786_ScenarioAC1_Validate_NoOverride(@Optional("CA") String state) {

        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_MembershipValid").resolveLinks());

        mainApp().open();
        createCustomerIndividual();
        log.info("Policy Creation Started...");

        policy.initiate();

        //class_td contains erroneous data, will fire Override rule, last name != membership last name = override.
        policy.getDefaultView().fillUpTo(testData, DocumentsAndBindTab.class, true);
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME, false);

        //Change First Name to make it match with First Name only from Membership report, rule should not fail
        policy.dataGather().start();
        policy.getDefaultView().fillUpTo(getPolicyTD().adjust(getTestSpecificTD("TestData_Membership_Valid_First_Name")), DocumentsAndBindTab.class, true);
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME, false);

        //Change Last Name to make it match with Last Name only from Membership report, rule should not fail
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREFILL.get());
        policy.getDefaultView().fillUpTo(testData.adjust(getTestSpecificTD("TestData_Membership_Valid_Last_Name")), DocumentsAndBindTab.class, true);
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME, false);

        //Change DOB to make it match with DOB only from Membership report, rule should not fail
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREFILL.get());
        policy.getDefaultView().fillUpTo(testData.adjust(getTestSpecificTD("TestData_Membership_Valid_DOB")), DocumentsAndBindTab.class, true);
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME, false);


        //      Finally issuing policy
        DocumentsAndBindTab docTab = new DocumentsAndBindTab();
        docTab.submitTab();
        new ErrorTab().overrideAllErrors();
        docTab.submitTab();
        new PurchaseTab().fillTab(testData).submitTab();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        mainApp().close();
    }


    /**
     * @author Andrejs Mitjukovs
     * @name Test Membership validation and override.
     * @scenario
     * 1. Create Customer.
     * 2. Create Auto Select Policy with membership NO.
     * 3. Initiate ENDORSEMENT
     * 3. Add member number on General tab with mismatching First Name, Last Name and DOB.
     * 4. Fill All other required data up to Documents and Bind Tab.
     * 5. Verify that Error "Membership Validation Failed. Please review the Membership Report and confirm..."
     * 6. Override the error and bind.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-3786")
    public void pas3786_ScenarioAC1_Validate_Override_Endorsement(@Optional("CA") String state) {

        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_MembershipValid").resolveLinks());
        TestData tdInvalidMembership = getTestSpecificTD("TestData_Endorsement_Renewal").resolveLinks()
                .adjust(getPolicyTD("Endorsement", "TestData"));

        mainApp().open();
        createCustomerIndividual();
        log.info("Policy Creation Started...");

        policy.createPolicy(testData);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        //Endorsing policy and veryfing if rule fails, last name != membership last name
        policy.endorse().performAndFill(tdInvalidMembership);
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME,true);

        new DocumentsAndBindTab().submitTab();
        new ErrorTab().overrideAllErrors();
        new DocumentsAndBindTab().submitTab();
        PolicyHelper.verifyEndorsementIsCreated();
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        mainApp().close();
    }


    /**
     * @author Andrejs Mitjukovs
     * @name Test Membership validation and override.
     * @scenario
     * 1. Create Customer.
     * 2. Create Auto Select Policy with membership NO.
     * 3. Initiate RENEWAL
     * 3. Add member number on General tab with mismatching First Name, Last Name and DOB.
     * 4. Fill All other required data up to Documents and Bind Tab.
     * 5. Verify that Error "Membership Validation Failed. Please review the Membership Report and confirm..."
     * 6. Override the error and bind.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-3786")
    public void pas3786_ScenarioAC1_Validate_Override_Renewal(@Optional("CA") String state) {

        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_MembershipValid").resolveLinks());
        TestData tdInvalidMembership = getTestSpecificTD("TestData_Endorsement_Renewal").resolveLinks();

        mainApp().open();
        createCustomerIndividual();
        log.info("Policy Creation Started...");
        policy.createPolicy(testData);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        //Renew policy and verify if rule fails, last name != membership last name
        policy.renew().start();
        policy.getDefaultView().fill(tdInvalidMembership);
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME,true);


        DocumentsAndBindTab docTab = new DocumentsAndBindTab();
        docTab.submitTab();

        //Overriding failed rules in Renew flow and binding endorsement
        ErrorTab errorTab = new ErrorTab();
        errorTab.overrideAllErrors();
        docTab.submitTab();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        mainApp().close();
    }

    /**
     * @author Andrejs Mitjukovs
     * @name Test Membership validation and override.
     * @scenario
     * 1. Create Customer.
     * 2. Create Auto Select Policy with membership NO.
     * 3. Initiate Manual RENEWAL
     * 3. Add DUMMY member number on General tab
     * 4. Fill All other required data up to Documents and Bind Tab.
     * 5. Verify that Error "Membership Validation Failed. Please review the Membership Report and confirm..." is not displayed (repeat for all DUMMY numbers)
     * 6. Override the error and bind.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-3786")
    public void pas6668_ScenarioAC2_Dummy_Numbers_Manual_Renewal(@Optional("CA") String state) {

        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_MembershipValid").resolveLinks());

        mainApp().open();
        createCustomerIndividual();
        log.info("Policy Creation Started...");
        policy.createPolicy(testData);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        //Renew policy and verify if rule fails, last name != membership last name
        policy.renew().start();
        validateDummyNumbersOnRenewalIssue();
    }

    /**
     * @author Andrejs Mitjukovs
     * @name Test Membership validation and override.
     * @scenario
     * 1. Create Customer.
     * 2. Create Auto Select Policy with membership NO.
     * 3. Initiate Manual RENEWAL
     * 3. Add DUMMY member number on General tab
     * 4. Fill All other required data up to Documents and Bind Tab.
     * 5. Verify that Error "Membership Validation Failed. Please review the Membership Report and confirm..." is not displayed (repeat for all DUMMY numbers)
     * 6. Override the error and bind.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-3786")
    public void pas6668_ScenarioAC2_Dummy_Numbers_Automatic_Renewal(@Optional("CA") String state) {

        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_MembershipValid").resolveLinks());

        mainApp().open();
        createCustomerIndividual();
        log.info("Policy Creation Started...");
        policy.createPolicy(testData);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("TEST: Renewing Policy #" + policyNumber + " to test DUMMY Membership number");
        LocalDateTime renewDate=getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
        TimeSetterUtil.getInstance().nextPhase(renewDate);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);

        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        PolicySummaryPage.buttonRenewals.verify.enabled(true);
        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();

        validateDummyNumbersOnRenewalIssue();


    }

    private void validateDummyNumbersOnRenewalIssue() {

        TestData testData = getTestSpecificTD("TestData_MembershipInvalid");
        policy.getDefaultView().fillUpTo(testData.adjust(getTestSpecificTD("TestData_Dummy_Number1")), DocumentsAndBindTab.class, true);
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME, false);

        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        policy.getDefaultView().fillUpTo(testData.adjust(getTestSpecificTD("TestData_Dummy_Number2")), DocumentsAndBindTab.class, true);
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME, false);

        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        policy.getDefaultView().fillUpTo(testData.adjust(getTestSpecificTD("TestData_Dummy_Number3")), DocumentsAndBindTab.class, true);
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME, false);

        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        policy.getDefaultView().fillUpTo(testData.adjust(getTestSpecificTD("TestData_Dummy_Number4")), DocumentsAndBindTab.class, true);
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME, false);

        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        policy.getDefaultView().fillUpTo(testData.adjust(getTestSpecificTD("TestData_Dummy_Number5")), DocumentsAndBindTab.class, true);
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME, false);

        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        policy.getDefaultView().fillUpTo(testData.adjust(getTestSpecificTD("TestData_Dummy_Number6")), DocumentsAndBindTab.class, true);
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME, false);

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        mainApp().close();

    }

    private void goToBindAndVerifyError(ErrorEnum.Errors errorCode, boolean present) {
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());

        ErrorTab errorTab = new ErrorTab();
        PurchaseTab purchaseTab = new PurchaseTab();
        DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
        documentsAndBindTab.submitTab();

        if (errorTab.isVisible()) {
            errorTab.verify.errorsPresent(present, errorCode);
            errorTab.cancel();
        }
        else {
            if(purchaseTab.buttonCancel.isPresent()) {
                PurchaseTab.buttonCancel.click();
            }
        }

    }

}
