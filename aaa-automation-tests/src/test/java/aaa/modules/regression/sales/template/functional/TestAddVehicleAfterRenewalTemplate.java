package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class TestAddVehicleAfterRenewalTemplate extends PolicyBaseTest {

    private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    private VehicleTab vehicleTab = new VehicleTab();

    protected void testAddVehicleAfterRenewal(List<TestData> tdVehicleTab) {

        // Prepare test data
        tdVehicleTab.add(getPolicyTD().getTestDataList(VehicleTab.class.getSimpleName()).get(0));
        tdVehicleTab.get(2).adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), "JH4DA9350PS016433");
        TestData td = getPolicyTD()
                .adjust(VehicleTab.class.getSimpleName(), tdVehicleTab)
                .adjust(AssignmentTab.class.getSimpleName(), testDataManager.getDefault(TestAddVehicleAfterRenewalTemplate.class).getTestData("TestData_AssignmentTab"));

        // Create policy with 3 vehicles
        String policyNumber = openAppAndCreatePolicy(td);
        LocalDateTime renewalEffDate = PolicySummaryPage.getExpirationDate();

        // Advance time to R-20
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(renewalEffDate.minusDays(20));
        mainApp().open();

        // Create renewal image and pay total amount due
        SearchPage.openPolicy(policyNumber);
        policy.renew().perform();
        calculatePremiumAndBind();
        payTotalAmtDue(policyNumber);
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());

        // Initiate endorsement with trans. eff. date equal to renewal eff. date and remove 2nd vehicle
        TestData tdEndorsement = getPolicyTD("Endorsement", "TestData")
                .adjust(AutoCaMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel(), renewalEffDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        policy.endorse().perform(tdEndorsement);
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
        vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.LIST_OF_VEHICLE).getTable().getRow(2).getCell(5).controls.links.getFirst().click();
        calculatePremiumAndBind();

        // Advance time to R and run policyStatusUpdateJob
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(renewalEffDate);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob);
        mainApp().open();

        // Validate the renewal is now active
        SearchPage.openPolicy(policyNumber);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        // Initiate endorsement dated 5 days into the renewal and add a vehicle back
        tdEndorsement.adjust(AutoCaMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel(), renewalEffDate.plusDays(5).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        policy.endorse().perform(tdEndorsement);
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
        vehicleTab.fillTab(tdVehicleTab.get(1)).submitTab();

        // Assign vehicle to a driver, calculate premium, and validate no error is displayed
        new AssignmentTab().getAssetList().getAsset(AutoCaMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getTable()
                .getRow(3).getCell(PolicyConstants.AssignmentTabTable.PRIMARY_DRIVER).controls.comboBoxes.getFirst().setValueByIndex(1);
        premiumAndCoveragesTab.calculatePremium();
        assertThat(PremiumAndCoveragesTab.buttonViewRatingDetails).isPresent();

    }

    private void calculatePremiumAndBind() {
        premiumAndCoveragesTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();
    }

}
