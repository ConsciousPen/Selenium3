package aaa.modules.regression.conversions.pup.functional;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.customer.CustomerActions;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.modules.regression.conversions.ConvPUPBaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import static toolkit.verification.CustomAssertions.assertThat;

/**
 * @author Sreenivas Mamidi
 * @name Test Policy RME Renewal Image TimeLine for both SIS and MAIG
 * @scenario 1. Create Individual Customer / Account
 * 2. Select RME Action with HSS product
 * 3. Verify Renewal Effective Date with in timeline
 */

public class TestPolicyRmeEffDateTimeLine extends ConvPUPBaseTest {

    @Parameters({"state"})
    @StateList(states = {Constants.States.AZ, Constants.States.KY, Constants.States.WV, Constants.States.NJ, Constants.States.MD, Constants.States.PA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Conversions.PUP, testCaseId = "PAS-14206,PAS-14207,PAS-14208")
    public void testPolicyRmeTimeLine(@Optional("PA") String state) {

        InitiateRenewalEntryActionTab initiateRenewalEntryActionTab = new InitiateRenewalEntryActionTab();
        mainApp().open();

        // Create customer
        createCustomerIndividual();
        customer.initiateRenewalEntry().start();

        // Fill the data with future date time line and verify the error message
        initiateRenewalEntryActionTab.fillTab(getTestSpecificTD("TestData_Future_Date"));
        initiateRenewalEntryActionTab.submitTab();
        ErrorTab errorTab = new ErrorTab();
        assertThat(errorTab.tableTabFormErrors.getRow(1).getCell("Description").getValue()).isEqualTo(ErrorEnum.Errors.ERROR_AAA_MES_IRE_06.getMessage());
        Tab.buttonBack.click();

        // Fill the data with past date time line and verify the error message
        initiateRenewalEntryActionTab.fillTab(getTestSpecificTD("TestData_Past_Date"));
        initiateRenewalEntryActionTab.submitTab();
        assertThat(errorTab.tableTabFormErrors.getRow(1).getCell("Description").getValue()).isEqualTo(ErrorEnum.Errors.ERROR_AAA_MES_IRE_07.getMessage());
        Tab.buttonBack.click();

        // Fill the data with correct time line and verify the data gather mode
        initiateRenewalEntryActionTab.fillTab(getTestSpecificTD("TestData_Within_Timeline"));
        initiateRenewalEntryActionTab.submitTab();

        new CustomerActions.InitiateRenewalEntry().submit();
        assertThat(PersonalUmbrellaMetaData.PrefillTab.NamedInsured.OCCUPATION.getLabel()).isEqualTo("Occupation");

    }
}
