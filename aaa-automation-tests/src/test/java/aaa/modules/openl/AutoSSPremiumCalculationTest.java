package aaa.modules.openl;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.helpers.openl.model.auto_ss.AutoSSOpenLVehicle;
import aaa.main.modules.policy.auto_ss.actiontabs.CancellationActionTab;
import aaa.main.modules.policy.auto_ss.actiontabs.ReinstatementActionTab;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLPolicy;
import aaa.helpers.openl.testdata_generator.AutoSSTestDataGenerator;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import org.apache.commons.lang3.StringUtils;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

import java.util.stream.IntStream;

public class AutoSSPremiumCalculationTest extends OpenLRatingBaseTest<AutoSSOpenLPolicy> {

    @Override
    protected TestData getRatingDataPattern() {
        return super.getRatingDataPattern().mask(new VehicleTab().getMetaKey(), new PremiumAndCoveragesTab().getMetaKey());
    }

    @Override
    protected String createQuote(AutoSSOpenLPolicy openLPolicy) {
        PremiumAndCoveragesTab pacTab = new PremiumAndCoveragesTab();
        AutoSSTestDataGenerator tdGenerator = openLPolicy.getTestDataGenerator(getRatingDataPattern());
        TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy);
        TestData cappingData = tdGenerator.getCappingData(openLPolicy);

        if (openLPolicy.isLegacyConvPolicy()) {
            TestData renewalEntryData = tdGenerator.getRenewalEntryData(openLPolicy);
            if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())) {
                NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
            }
            customer.initiateRenewalEntry().perform(renewalEntryData);
        } else {
            policy.get().initiate();
        }

        policy.get().getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesTab.class, false);
        pacTab.getAssetList().fill(quoteRatingData);

        if (openLPolicy.isCappedPolicy()) {
            if (!openLPolicy.isLegacyConvPolicy()) {
                policyPurchaseAndRenew(tdGenerator.getPolicyPurchaseData(), tdGenerator.getPolicyRenewData(), openLPolicy, quoteRatingData);
            }
            pacTab.calculatePremium();
            assertThat(PremiumAndCoveragesTab.buttonViewCappingDetails).as("View Capping Details button did not appear after premium calculation").isPresent();
            pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.VIEW_CAPPING_DETAILS_DIALOG).fill(cappingData);
        }

        return Tab.labelPolicyNumber.getValue();
    }

    @Override
    protected Dollar calculatePremium(AutoSSOpenLPolicy openLPolicy) {
        new PremiumAndCoveragesTab().calculatePremium();
        Dollar totalPremium = PremiumAndCoveragesTab.getTotalTermPremium();
        if (PremiumAndCoveragesTab.tableStateAndLocalTaxesSummaryDetailed.isPresent() || PremiumAndCoveragesTab.tableStateAndLocalTaxesSummary.isPresent()) { // WV and KY states have AP/RP taxes
            totalPremium = totalPremium.subtract(PremiumAndCoveragesTab.getStateAndLocalTaxesAndPremiumSurchargesPremium());
        }
        return totalPremium;
    }

    @Override
    protected String createCustomerIndividual(AutoSSOpenLPolicy openLPolicy) {
        int driverAge = (openLPolicy.isNewRenPasCappedPolicy() && openLPolicy.getTerm() == 12) ? openLPolicy.getDrivers().get(0).getDriverAge() - 1 : openLPolicy.getDrivers().get(0).getDriverAge();
        TestData td = getCustomerIndividualTD("DataGather", "TestData")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.DATE_OF_BIRTH.getLabel()),
                        AutoSSTestDataGenerator.getDriverTabDateOfBirth(driverAge, openLPolicy.getEffectiveDate()));
        return createCustomerIndividual(td);
    }

    /**
     * This method issues quote and creates renewal for tests which have capping but are not LegacyConv.
     **/
    private void policyPurchaseAndRenew(TestData tdPurchase, TestData tdRenew, AutoSSOpenLPolicy openLPolicy, TestData quoteRatingData) {
        VehicleTab vehicleTab = new VehicleTab();
        PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

        //set BI coverage = priorBILimit to get correct priorBILimit during renewal
        String priorBILimit = quoteRatingData.getValue(GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
                AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS.getLabel());
        if (!priorBILimit.equals("None")) {
            premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).setValue("starts=" + priorBILimit);
        }

        premiumAndCoveragesTab.calculatePremium();
        PremiumAndCoveragesTab.buttonContinue.click();
        ErrorTab errorTab = new ErrorTab();
        if (errorTab.isVisible()) {
            errorTab.overrideAllErrors();
            errorTab.override();
            PremiumAndCoveragesTab.buttonContinue.click();
        }
        policy.get().getDefaultView().fillUpTo(tdPurchase, PurchaseTab.class, false);
        if (errorTab.isVisible()) {
            errorTab.overrideAllErrors();
            errorTab.submitTab();
        }
        policy.get().getDefaultView().fill(DataProviderFactory.dataOf(PurchaseTab.class.getSimpleName(), tdPurchase.getTestData(PurchaseTab.class.getSimpleName())));

        //perform reinstatement if required
        cancelReinstatePolicy(openLPolicy);

        //perform renewal
        policy.get().renew().perform();

        policy.get().getDefaultView().fillUpTo(tdRenew, VehicleTab.class, false);

        //update VIN codes or you will get error 500 on Premium tab
        for (AutoSSOpenLVehicle vehicle : openLPolicy.getVehicles()) {
            if (StringUtils.isNotBlank(vehicle.getVinCode())) {
                vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LIST_OF_VEHICLE).getTable().getRow(openLPolicy.getVehicles().indexOf(vehicle) + 1).
                        getCell(5).controls.links.get("View/Edit").click();
                vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).clear();
                vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).waitForPageUpdate();
                vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).setValue(vehicle.getVinCode());
            }
        }

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

        //return original coverage limit from openL during Renewal
        new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).
                setValue(quoteRatingData.getValue(PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel()));
    }

    private void cancelReinstatePolicy(AutoSSOpenLPolicy openLPolicy) {
        int daysShift = 25;
        if (openLPolicy.getReinstatements() != null && openLPolicy.getReinstatements() > 0) {
            IntStream.range(0, openLPolicy.getReinstatements()).forEach(reinsNumber -> {
                policy.get().cancel().perform(getPolicyTD("Cancellation", "TestData").
                        adjust(TestData.makeKeyPath(CancellationActionTab.class.getSimpleName(), AutoSSMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()),
                                String.format("$<today-%dd>", daysShift - reinsNumber)).resolveLinks());
                policy.get().reinstate().perform(getPolicyTD("Reinstatement", "TestData").
                        adjust(TestData.makeKeyPath(ReinstatementActionTab.class.getSimpleName(), AutoSSMetaData.ReinstatementActionTab.REINSTATE_DATE.getLabel()),
                                String.format("$<today-%dd>", daysShift - reinsNumber - 1)).resolveLinks());
            });
        }
    }

}
