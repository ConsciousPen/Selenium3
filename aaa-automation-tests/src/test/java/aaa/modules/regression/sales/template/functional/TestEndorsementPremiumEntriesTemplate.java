package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.db.DBService;
import toolkit.webdriver.controls.Link;

public class TestEndorsementPremiumEntriesTemplate extends PolicyBaseTest {

    private ReportsTab reportsTab = new ReportsTab();
    private EndorsementTab endorsementTab = new EndorsementTab();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

    protected void testEndorsementPremiumEntriesCopyFromPolicy() {

        // Create policy and validate DB entries
        createPolicyAndValidate();

        // Copy policy and validate DB entries
        policy.policyCopy().perform(getStateTestData(testDataManager.policy.get(getPolicyType()), "CopyFromPolicy", "TestData"));
        initiateDataGatherAndValidate();

    }

    protected void testEndorsementPremiumEntriesCopyFromQuote() {

        // Create quote and validate DB entries
        createQuoteAndFillUpTo(PremiumsAndCoveragesQuoteTab.class);
        validatePremiumEntriesInDB(premiumsAndCoveragesQuoteTab.getPolicyNumber(), getIncludedEndorsementsWithPremiumAmount());
        premiumsAndCoveragesQuoteTab.saveAndExit();

        // Copy quote and validate DB entries
        policy.copyQuote().perform(getStateTestData(testDataManager.policy.get(getPolicyType()), "CopyFromQuote", "TestData"));
        initiateDataGatherAndValidate();

    }

    protected void testEndorsementPremiumEntriesRewrite() {

        // Create policy and validate DB entries
        createPolicyAndValidate();

        // Cancel/rewrite policy and validate DB entries
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
        policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
        initiateDataGatherAndValidate();

    }

    protected void testEndorsementPremiumEntriesRenewal() {

        // Create policy and validate DB entries
        createPolicyAndValidate();

        // Create renewal image and validate DB entries
        policy.renew().perform();
        premiumsAndCoveragesQuoteTab.calculatePremium();
        validatePremiumEntriesInDB(premiumsAndCoveragesQuoteTab.getPolicyNumber(), getIncludedEndorsementsWithPremiumAmount(), "0");

    }

    private void createPolicyAndValidate() {
        String policyNumber = openAppAndCreatePolicy();
        policy.policyInquiry().start();
        validatePremiumEntriesInDB(policyNumber, getIncludedEndorsementsWithPremiumAmount());
        SearchPage.openPolicy(policyNumber);
    }

    private void initiateDataGatherAndValidate() {
        policy.dataGather().start();

        // Rewrites require a re-order of PPC report
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.REPORTS.get());
        reportsTab.getAssetList().getAsset(HomeCaMetaData.ReportsTab.SALES_AGENT_AGREEMENT).setValue("I Agree");
        Link ppcOrderLink = reportsTab.getAssetList().getAsset(HomeCaMetaData.ReportsTab.PUBLIC_PROTECTION_CLASS).getTable().getRow(1).getCell("Report").controls.links.getFirst();
        if ("Order report".equalsIgnoreCase(ppcOrderLink.getValue())) {
            ppcOrderLink.click();
        }

        premiumsAndCoveragesQuoteTab.calculatePremium();
        validatePremiumEntriesInDB(premiumsAndCoveragesQuoteTab.getPolicyNumber(), getIncludedEndorsementsWithPremiumAmount());
    }

    private void validatePremiumEntriesInDB(String policyNumber, Map<String, Dollar> endorsements) {
        validatePremiumEntriesInDB(policyNumber, endorsements, "1");
    }

    // currentRevisionInd used to distinguish renewals (for renewal image = '0', others = '1')
    private void validatePremiumEntriesInDB(String policyNumber, Map<String, Dollar> endorsements, String currentRevisionInd) {
        String query =  "SELECT pe.premiumamt, c.coveragecd " +
                        "FROM pasadm.policysummary ps " +
                        "JOIN pasadm.coverage c ON c.policydetail_id = ps.policydetail_id " +
                        "JOIN pasadm.premiumentry pe ON pe.coverage_id = c.id " +
                        "WHERE ps.currentrevisionind = '" + currentRevisionInd + "' " +
                        "AND pe.premiumcd = 'NWT' " +
                        "AND ps.policynumber like '" + policyNumber + "'";

        List<Map<String, String>> dbEntries = DBService.get().getRows(query);
        assertThat(dbEntries.size()).isEqualTo(endorsements.size());

        for (Map<String, String> row : dbEntries) {
            String coverageCdValue = row.get("COVERAGECD");
            assertThat(endorsements.get(coverageCdValue.substring(0, coverageCdValue.indexOf("#")))).isEqualTo(new Dollar(row.get("PREMIUMAMT")));
        }

    }

    private Map<String, Dollar> getIncludedEndorsementsWithPremiumAmount() {
        // Get all included endorsement forms from Endorsements Tab
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        List<String>  includedForms = IntStream.rangeClosed(1, endorsementTab.tblIncludedEndorsements.getRowsCount())
                .mapToObj(i -> endorsementTab.tblIncludedEndorsements.getColumn(PolicyConstants.PolicyIncludedAndSelectedEndorsementsTable.FORM_ID).getCell(i).getValue()).collect(Collectors.toList());

        // Get all premiums for included endorsements and merge into hashmap
        Map<String, Dollar> endorsements = new LinkedHashMap<>();
        NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        assertThat(PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowsCount()).isEqualTo(includedForms.size());
        for (String form : includedForms) {
            String sPremium = PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains(PolicyConstants.PolicyEndorsementFormsTable.DESCRIPTION, form)
                    .getCell(PolicyConstants.PolicyEndorsementFormsTable.TERM_PREMIUM).getValue();
            Dollar premium;
            if (sPremium.equals(PolicyConstants.PolicyEndorsementFormsTable.INCLUDED)) {
                premium = new Dollar(0);
            } else {
                premium = new Dollar(sPremium);
            }
            endorsements.put(form, premium);
        }
        return endorsements;
    }

}
