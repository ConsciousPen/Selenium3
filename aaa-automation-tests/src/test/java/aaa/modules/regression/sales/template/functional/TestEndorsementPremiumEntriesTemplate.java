package aaa.modules.regression.sales.template.functional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.PolicyConstants;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.db.DBService;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestEndorsementPremiumEntriesTemplate extends PolicyBaseTest {

    EndorsementTab endorsementTab = new EndorsementTab();

    protected void testEndorsementPremiumEntriesCopyFromPolicy() {
        String policyNumber = openAppAndCreatePolicy();
        validatePremiumEntriesInDB(policyNumber, getIncludedEndorsementsWithPremiumAmount());

        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());
        policy.policyCopy().perform(getStateTestData(testDataManager.policy.get(getPolicyType()), "CopyFromPolicy", "TestData"));


    }

    protected void testEndorsementPremiumEntriesRewrite() {

    }

    protected void testEndorsementPremiumEntriesRenewal() {


    }

    private void validatePremiumEntriesInDB(String policyNumber, Map<String, Dollar> endorsements) {
        String query =  "SELECT pe.premiumamt, c.coveragecd " +
                        "FROM pasadm.policysummary ps " +
                        "JOIN pasadm.coverage c ON c.policydetail_id = ps.policydetail_id " +
                        "JOIN pasadm.premiumentry pe ON pe.coverage_id = c.id " +
                        "WHERE ps.currentrevisionind = '1' " +
                        "AND pe.premiumcd = 'NWT' " +
                        "AND ps.policynumber like '" + policyNumber + "'";

        List<Map<String, String>> dbEntries = DBService.get().getRows(query);
        assertThat(dbEntries.size()).isEqualTo(endorsements.size());

        for (Map<String, String> row : dbEntries) {
            assertThat(endorsements.get(row.get("COVERAGECD").substring(0, 5))).isEqualTo(new Dollar(row.get("PREMIUMAMT")));
        }

    }

    private Map<String, Dollar> getIncludedEndorsementsWithPremiumAmount() {
        // Get all included endorsement forms from Endorsements Tab
        policy.policyInquiry().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        List<String>  includedForms = IntStream.rangeClosed(1, endorsementTab.tblIncludedEndorsements.getRowsCount())
                .mapToObj(i -> endorsementTab.tblIncludedEndorsements.getColumn(PolicyConstants.PolicyIncludedAndSelectedEndorsementsTable.FORM_ID).getCell(i).getValue()).collect(Collectors.toList());

        // Get all premiums for included endorsements and merge into hashmap
        Map<String, Dollar> endorsements = new LinkedHashMap<>();
        NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        assertThat(PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowsCount()).isEqualTo(endorsements.size());
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
