package aaa.helpers.product;

import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;

public class PolicyHelper {

    //TODO: Refactor verifyPresent methods, use constants instead of string literals
    public static void verifyEndorsementIsCreated() {
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Bind Endorsement effective");
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
}
