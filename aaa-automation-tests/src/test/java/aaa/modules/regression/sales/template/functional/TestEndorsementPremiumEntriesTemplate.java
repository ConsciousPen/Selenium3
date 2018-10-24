package aaa.modules.regression.sales.template.functional;

import java.util.*;
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

    private void validatePremiumEntriesInDB(String policyNumber, Map<String, String> endorsements) {
        String query =  "SELECT pe.premiumamt, c.coveragecd " +
                        "FROM pasadm.policysummary ps " +
                        "JOIN pasadm.coverage c ON c.policydetail_id = ps.policydetail_id " +
                        "JOIN pasadm.premiumentry pe ON pe.coverage_id = c.id " +
                        "WHERE ps.currentrevisionind = '1' " +
                        "AND pe.premiumcd = 'NWT' " +
                        "AND ps.policynumber like '" + policyNumber + "'";

        List<Map<String, String>> dbEntries = DBService.get().getRows(query);

        for (Map<String, String> row : dbEntries) {
            for (String form : endorsements.keySet()) {
                if (form.equals(row.get("COVERAGECD").substring(0, 5))) {
                    assertThat(endorsements.get(form)).isEqualTo(row.get("PREMIUMAMT"));
                }
            }
            endorsements.get(row.get("COVERAGECD").substring(0, 5));
        }

    }

    private Map<String, String> getIncludedEndorsementsWithPremiumAmount() {
        Map<String, String> endorsements = new LinkedHashMap<>();
        policy.policyInquiry().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        for (int i = 1; i <= endorsementTab.tblIncludedEndorsements.getRowsCount(); i++) {
            String formId = new EndorsementTab().tblIncludedEndorsements.getColumn("Form ID").getCell(i).getValue();
            NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
            Dollar premAmt = new Dollar(PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains(PolicyConstants.PolicyEndorsementFormsTable.DESCRIPTION, formId)
                    .getCell(PolicyConstants.PolicyEndorsementFormsTable.TERM_PREMIUM).getValue());
            endorsements.put(formId, premAmt.toPlaingString());
            NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.ENDORSEMENT.get());
        }
        return endorsements;
    }

}
