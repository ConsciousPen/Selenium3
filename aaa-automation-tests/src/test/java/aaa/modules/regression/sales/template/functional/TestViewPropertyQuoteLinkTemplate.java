package aaa.modules.regression.sales.template.functional;


import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.webdriver.BrowserController;
import static toolkit.verification.CustomAssertions.assertThat;


public class TestViewPropertyQuoteLinkTemplate extends PolicyBaseTest {

    protected void pas14842_testViewPropertyQuoteLinkSS(TestData td) {

        initiatePolicy();
        getPolicyType().get().getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class);

        assertThat(PremiumsAndCoveragesQuoteTab.linkViewPropertyQuote.isPresent()).isFalse();

        new PremiumsAndCoveragesQuoteTab().calculatePremium();
        assertThat(PremiumsAndCoveragesQuoteTab.linkViewPropertyQuote.isEnabled()).isTrue();
        assertThat(PremiumsAndCoveragesQuoteTab.linkViewPropertyQuote.getValue()).isEqualTo("View Property Quote");
        //PremiumsAndCoveragesQuoteTab.linkViewPropertyQuote.click();
        //assertThat(BrowserController.get().driver().getWindowHandles().size()).isEqualTo(2);

    }

    protected void pas14842_testViewPropertyQuoteLinkCA(TestData td) {

        initiatePolicy();
        getPolicyType().get().getDefaultView().fillUpTo(td, aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab.class);

        assertThat(aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab.linkViewPropertyQuote.isPresent()).isFalse();

        new aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab().calculatePremium();
        assertThat(aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab.linkViewPropertyQuote.isEnabled()).isTrue();
        assertThat(aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab.linkViewPropertyQuote.getValue()).isEqualTo("View Property Quote");
        //aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab.linkViewPropertyQuote.click();
        //assertThat(BrowserController.get().driver().getWindowHandles().size()).isEqualTo(2);

    }

    private void initiatePolicy(){

        // Create customer and policy
        mainApp().open();
        createCustomerIndividual();
        getPolicyType().get().initiate();
    }
}
