package aaa.helpers.product;

import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.utils.datetime.DateTimeUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import com.exigen.ipb.etcsa.utils.Dollar;

public class PolicyHelper {

    //TODO: Refactor verifyPresent methods, use constants instead of string literals
    public static void verifyEndorsementIsCreated() {
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(DateTimeUtils.getCurrentDateTime(), "Bind Endorsement effective");
    }

    public static void verifyAutomatedRenewalGenerated(LocalDateTime date) {
        String message = String.format("Automated Renewal effective %s for Policy %s", date.format(DateTimeUtils.MM_DD_YYYY), PolicySummaryPage.labelPolicyNumber.getValue());
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist(message);
        PolicySummaryPage.buttonRenewals.verify.enabled();
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
        BigDecimal percentValue = (rtp.divide(ctp, 5,5)).multiply((BigDecimal.ONE).add(fcv.divide(new BigDecimal(100))).multiply(new BigDecimal(100))).setScale(0, RoundingMode.HALF_EVEN );
        return String.format("%s.00%%", percentValue.toString());
    }
}
