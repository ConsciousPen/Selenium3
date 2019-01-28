package aaa.helpers.product;

import static toolkit.verification.CustomAssertions.assertThat;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import javax.annotation.Nonnull;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;

public class PolicyHelper {

    private static final String GET_CEILLING_BY_POLICY_NUMBER = "SELECT pcg.ceiling\n"
            + "FROM POLICYSUMMARY PS JOIN POLICYDETAIL PD ON PD.ID = PS.POLICYDETAIL_ID\n"
            + "JOIN PREMIUMCAPPING PC ON PD.PREMIUMCAPPING_ID = PC.ID\n"
            + "JOIN PREMIUMCAPPINGCONFIGURATION PCG ON pcg.id = pc.cappingconfig_id\n"
            + "WHERE PD.ID = PS.POLICYDETAIL_ID\n"
            + "AND PS.POLICYNUMBER='%s' AND ROWNUM = 1";

    private static final String GET_FLOOR_BY_POLICY_NUMBER = "SELECT pcg.floor\n"
            + "FROM POLICYSUMMARY PS JOIN POLICYDETAIL PD ON PD.ID = PS.POLICYDETAIL_ID\n"
            + "JOIN PREMIUMCAPPING PC ON PD.PREMIUMCAPPING_ID = PC.ID\n"
            + "JOIN PREMIUMCAPPINGCONFIGURATION PCG ON pcg.id = pc.cappingconfig_id\n"
            + "WHERE PD.ID = PS.POLICYDETAIL_ID\n"
            + "AND PS.POLICYNUMBER='%s' AND ROWNUM = 1";

    private static final String GET_CEILLING_BY_REGULAR_POLICY_NUMBER = "SELECT pcg.ceiling\n"
            + "FROM POLICYSUMMARY PS JOIN POLICYDETAIL PD ON PD.ID = PS.POLICYDETAIL_ID\n"
            + "JOIN PREMIUMCAPPING PC ON PD.PREMIUMCAPPING_ID = PC.ID\n"
            + "JOIN PREMIUMCAPPINGCONFIGURATION PCG ON pcg.id = pc.cappingconfig_id\n"
            + "WHERE PD.ID = PS.POLICYDETAIL_ID\n"
            + "AND PS.POLICYNUMBER='%s' AND PS.POLICYSTATUSCD = 'rated'";

    private static final String GET_FLOOR_BY__REGULAR_POLICY_NUMBER = "SELECT pcg.floor\n"
            + "FROM POLICYSUMMARY PS JOIN POLICYDETAIL PD ON PD.ID = PS.POLICYDETAIL_ID\n"
            + "JOIN PREMIUMCAPPING PC ON PD.PREMIUMCAPPING_ID = PC.ID\n"
            + "JOIN PREMIUMCAPPINGCONFIGURATION PCG ON pcg.id = pc.cappingconfig_id\n"
            + "WHERE PD.ID = PS.POLICYDETAIL_ID\n"
            + "AND PS.POLICYNUMBER='%s' AND PS.POLICYSTATUSCD = 'rated'";

    //TODO: Refactor verifyPresent methods, use constants instead of string literals
    public static void verifyEndorsementIsCreated() {
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(DateTimeUtils.getCurrentDateTime(), "Bind Endorsement effective");
    }

    public static void verifyAutomatedRenewalGenerated(LocalDateTime date) {
        String message = String.format("Automated Renewal effective %s for Policy %s", date.format(DateTimeUtils.MM_DD_YYYY), PolicySummaryPage.labelPolicyNumber.getValue());
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist(message);
        assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
    }

    public static void verifyAutomatedRenewalNotGenerated(LocalDateTime date) {
        String message = String.format("Automated Renewal effective %s for Policy %s", date.format(DateTimeUtils.MM_DD_YYYY), PolicySummaryPage.labelPolicyNumber.getValue());
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionNotExist(message);
    }

    public static String calculateChangeInPolicyPremium(String calculatedTermPremium, String renewalTermPremiumOld) {
        BigDecimal ctp = new BigDecimal(new Dollar(calculatedTermPremium).toPlaingString());
        BigDecimal rtp = new BigDecimal(new Dollar(renewalTermPremiumOld).toPlaingString());
        BigDecimal percentValue = ctp.divide(rtp, 5,5).subtract(BigDecimal.ONE).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_EVEN);
        return String.format("%s%%", percentValue.toString());
    }

    public static String calculateCeilingOrFloorCap(String renewalTermPremiumOld, String calculatedTermPremium, String floorOrCeilCap) {
        BigDecimal rtp = new BigDecimal(new Dollar(renewalTermPremiumOld).toPlaingString());
        BigDecimal ctp = new BigDecimal(new Dollar(calculatedTermPremium).toPlaingString());
        BigDecimal fcv = new BigDecimal(new Dollar(floorOrCeilCap).toPlaingString());
		BigDecimal percentValue = rtp.divide(ctp, 5, 5).multiply(BigDecimal.ONE.add(fcv.divide(new BigDecimal(100))).multiply(new BigDecimal(100))).setScale(0, RoundingMode.HALF_EVEN);
        return String.format("%s.00%%", percentValue.toString());
    }

    public static String getCeilingByPolicyNumber(@Nonnull String policyNumber) {
        String query = String.format(GET_CEILLING_BY_POLICY_NUMBER, policyNumber);
        return DBService.get().getValue(query).get();
    }

    public static String getFloorByPolicyNumber(@Nonnull String policyNumber) {
        String query = String.format(GET_FLOOR_BY_POLICY_NUMBER, policyNumber);
        return DBService.get().getValue(query).get();
    }

    public static String getCeilingByRegularPolicyNumber(@Nonnull String policyNumber) {
        String query = String.format(GET_CEILLING_BY_REGULAR_POLICY_NUMBER, policyNumber);
        return DBService.get().getValue(query).get();
    }

    public static String getFloorByRegularPolicyNumber(@Nonnull String policyNumber) {
        String query = String.format(GET_FLOOR_BY__REGULAR_POLICY_NUMBER, policyNumber);
        return DBService.get().getValue(query).get();
    }
}
