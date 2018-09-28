package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class TestViewRatingDetailsLinkTemplate extends PolicyBaseTest {


    protected void pas8871_testViewRatingDetailsLink(TestData td) {

        mainApp().open();
        createCustomerIndividual();

        getPolicyType().get().createPolicy(td);
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        LocalDateTime expDate = PolicySummaryPage.getExpirationDate();

        //Change time to R-35 and run Renewal Jobs
		mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(expDate.minusDays(35));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);

        policy.renew().perform();
        new PremiumsAndCoveragesQuoteTab().calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        new BindTab().submitTab();

        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

        //Pay the minimum due for the proposed renewal
        Dollar minDue = new Dollar(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);

        SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_PENDING);
        policy.policyInquiry().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

        assertThat(PremiumsAndCoveragesQuoteTab.linkViewRatingDetails).isPresent();
        PremiumsAndCoveragesQuoteTab.linkViewRatingDetails.click();
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();

    }
}
