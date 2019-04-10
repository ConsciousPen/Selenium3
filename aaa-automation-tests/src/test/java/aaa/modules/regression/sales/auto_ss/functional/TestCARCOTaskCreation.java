package aaa.modules.regression.sales.auto_ss.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.mywork.MyWork;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import static toolkit.verification.CustomAssertions.assertThat;

@StateList(states = {Constants.States.NJ, Constants.States.NY})
public class TestCARCOTaskCreation extends AutoSSBaseTest {

    /**
     * @author Josh Carpenter
     * @name Test no task is created for the rule 'Vehicles with Physical Damage Coverage require a CARCO Inspection'
     * @scenario
     * 1. Initiate NJ/NY Auto SS quote with the following characteristics:
     *      a.  Base date and prior carrier time elapsed less than 4 years for NJ (2 years for NY)
     *      b.  Vehicle on policy 7 years old or less with greater than 1000 miles for NJ (3000 miles for NY)
     * 2. On Documents & Bind tab mark the sales agreement as NOT received
     * 3. Attempt to bind policy
     * 4. Override CARCO error message (Duration = 'Term', Reason = 'Temp Issue')
     * 5. Bind policy
     * 6. Navigate to tasks and validate no task is created for this rule
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22614")
    public void pas22614_testCARCOInspectionTaskNotCreated(@Optional("") String state) {

        DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();

        TestData td  = getPolicyTD()
                .adjust(TestData.makeKeyPath(AutoSSMetaData.VehicleTab.class.getSimpleName(), AutoSSMetaData.VehicleTab.VIN.getLabel()), "JTNKARJE1JJ566521");

        if (getState().equals(Constants.States.NJ)) {
            td.adjust(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(),
                    AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.ACNOWLEDGEMENT_OF_REQUIREMENT_FOR_INSURANCE_INSPECTION.getLabel()), PolicyConstants.SignatureStatus.PHYSICALLY_SIGNED);
        } else if (getState().equals(Constants.States.NY)) {
            td.adjust(TestData.makeKeyPath(AutoSSMetaData.VehicleTab.class.getSimpleName(), AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES.getLabel()), "No")
                    .adjust(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(),
                            AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.ACKNOWLEDGEMENT_OF_REQUIREMENT_FOR_PHOTO_INSPECTION.getLabel()), PolicyConstants.SignatureStatus.PHYSICALLY_SIGNED);
        }

        // Create policy and fill up to Documents & Bind, select 'No Document Received' for vehicle
        createQuoteAndFillUpTo(td, DocumentsAndBindTab.class);

        // Bind policy
        documentsAndBindTab.submitTab();
        new PurchaseTab().fillTab(td).submitTab();
        String policyNumber = PolicySummaryPage.getPolicyNumber();

        // Navigate to 'My Work' tab and validate no task was created
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.MY_WORK.get());
        new MyWork().filterTask().performByReferenceId(policyNumber);
        assertThat(MyWorkSummaryPage.tableTasks.getRowsCount()).isEqualTo(0);

    }

}
