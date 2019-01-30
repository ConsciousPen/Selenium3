package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.LookupQueries;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestStatCodeRefresh extends AutoSSBaseTest {

    /**
     * * @author Sarunas Jaraminas
     *
     * @name Test Ensure STAT Code is "refreshed" as part of Vehicle/MSRP Refresh
     * @scenario
     * 1. Add Vehicle STAT Code values
     * 2. TimeShift:07/03/18
     * 3. Create customer
     * 4. Create Auto SS Quote
     * where VIN code is not matching
     * 3. Calculate Premium
     * 4. View Rating Details Table
     * 5. Verify that STAT Code is "refreshed"
     *
     * @details
     *
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12879")
    public void pas12879_EnsureStatCodeIsRrefreshed(@Optional("ID") String state) {

        LookupQueries.insertStatCodeValues();

        mainApp().open();
        createCustomerIndividual();

        TestData testData = getPolicyTD()
                .adjust(TestData.makeKeyPath("VehicleTab", "VIN"), "ZZZKN3DD3E0344466")
                .adjust(TestData.makeKeyPath("VehicleTab", "Make"), "OTHER")
                .adjust(TestData.makeKeyPath("VehicleTab", "Other Make"), "OTHER")
                .adjust(TestData.makeKeyPath("VehicleTab", "Other Model"), "OTHER")
                .adjust(TestData.makeKeyPath("VehicleTab", "Other Body Style"), "Sedan")
                .adjust(TestData.makeKeyPath("VehicleTab", "Stated Amount"), "11000")
                .adjust(TestData.makeKeyPath("VehicleTab", "Stat Code"), "Small car");

        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, PremiumAndCoveragesTab.class, true);

        PremiumAndCoveragesTab.RatingDetailsView.open();

        List<String> ratingDetailsTable = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "MP Symbol");
        ratingDetailsTable.forEach(f -> assertThat(PremiumAndCoveragesTab
                .tableRatingDetailsVehicles.getRow(1, f).getCell(2).getValue()).isEqualToIgnoringCase("AN"));
    }
}