package aaa.modules.financials;

import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.PolicyBaseTest;

public class FinancialsPreconditions extends PolicyBaseTest {

    /**
     * Any Financials test that runs ledgerStatusUpdateJob needs to depend on successful completion of this.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FINANCE_PRECONDITION})
    public void createLedgerStatusUpdateJob(@Optional("") String state) {
        adminApp().open();
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());
        boolean created = GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.LEDGER_STATUS_UPDATE_JOB);
        assertThat(created).isTrue();
    }

}
