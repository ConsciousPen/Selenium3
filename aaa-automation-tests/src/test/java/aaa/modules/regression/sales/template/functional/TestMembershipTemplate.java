package aaa.modules.regression.sales.template.functional;

import aaa.admin.modules.administration.generateproductschema.defaulttabs.CacheManager;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.policy.*;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class TestMembershipTemplate extends PolicyBaseTest {

    RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
    private GeneralTab generalTab = new GeneralTab();
    private ApplicantTab applicantTab = new ApplicantTab();
    private ErrorTab errorTab = new ErrorTab();
    private PurchaseTab purchaseTab = new PurchaseTab();
    private aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab purchaseTabCA = new aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab();
    private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();


    protected void pas16457_validateMembershipNB15() {

        //Create and bind policy
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();

        if (getPolicyType() == PolicyType.AUTO_SS) {
            TestData tdAuto = getAdjustedTestData_Auto();
            AutoSSPolicy(tdAuto);
        } else if (getPolicyType() == PolicyType.HOME_SS_HO3) {
            TestData tdHome = getAdjustedTestData_Home();
            homeSSPolicy(tdHome);
        } else if (getPolicyType() == PolicyType.HOME_CA_HO3) {
            TestData tdHome = getAdjustedTestData_Home();
            homeCAPolicy(tdHome);
        } else {
            TestData tdAuto = getAdjustedTestData_Auto();
            AutoCASpecificPolicy(tdAuto);
        }

        String policyNumber = PolicySummaryPage.getPolicyNumber();
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

        //Update membership number in DB
        String db = DBService.get().getValue(String.format(GET_STATUS + policyNumber + GET_STATUS_2)).get();
        assertThat(db.contains("Error")).isTrue();
        DBService.get().executeUpdate(UPDATE_MEMBERSHIP_NUMBER + policyNumber + "')");
        DBService.get().executeUpdate(UPDATE_PRIOR_MEMBERSHIP_NUMBER + policyNumber + "')");

        adminApp().open();
        new CacheManager().goClearCacheManagerTable();
        mainApp().reopen();

        //Run NB15 and NB30 jobs and verify that the Membership Status is 'Active' in DB
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(15));
        JobUtils.executeJob(Jobs.membershipValidationJob);

        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(30));
        JobUtils.executeJob(Jobs.membershipValidationJob);

        db = DBService.get().getValue(String.format(GET_STATUS + policyNumber + GET_STATUS_2)).get();
        assertThat(db.contains("Active")).isTrue();
    }

    protected void AutoCASpecificPolicy(TestData td) {
        //TODO Finish Auto CA Policy in which There is a No Hit on Membership
        //Member Since Dialog is currently broken 8/1/2018
        policy.getDefaultView().fillUpTo(td, DriverActivityReportsTab.class, true);
        errorTab.overrideAllErrors();
        errorTab.override();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
        policy.getDefaultView().fillFromTo(td, DriverActivityReportsTab.class, DocumentsAndBindTab.class, false);
        new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.AGREEMENT).setValue("I agree");
        documentsAndBindTab.submitTab();
        errorTab.overrideAllErrors();
        errorTab.override();
        documentsAndBindTab.submitTab();
        purchaseTab.fillTab(td);
        purchaseTab.submitTab();

    }

    protected void AutoSSPolicy(TestData td) {

        //Fill up to the rating details report then fill out the Member Since Date dialog
        policy.getDefaultView().fillUpTo(td, RatingDetailReportsTab.class, true);
        ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
                .getCell(7).click();
        new RatingDetailReportsTab().getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT)
                .getAsset(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.ADD_MEMBER_SINCE_DIALOG.getLabel(), AssetList.class)
                .getAsset(AutoSSMetaData.RatingDetailReportsTab.AddMemberSinceDialog.MEMBER_SINCE.getLabel(), TextBox.class).setValue("11/14/2016");
        Page.dialogConfirmation.confirm();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get()); //Continue button disappears for a moment
        policy.getDefaultView().fillFromTo(td, VehicleTab.class, DriverActivityReportsTab.class);
        errorTab.overrideAllErrors();
        errorTab.override();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
        policy.getDefaultView().fillFromTo(td, DriverActivityReportsTab.class, DocumentsAndBindTab.class, false);

        new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.AGREEMENT).setValue("I agree");
        documentsAndBindTab.submitTab();
        errorTab.overrideAllErrors();
        errorTab.override();
        documentsAndBindTab.submitTab();
        purchaseTab.fillTab(td).submitTab();
    }

    protected void homeSSPolicy(TestData td) {

        try {
            policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        } catch (Exception e) {
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get()); //continue button disappears
            policy.getDefaultView().fillFromTo(td, PropertyInfoTab.class, BindTab.class);
        }

        documentsAndBindTab.submitTab();
        errorTab.overrideAllErrors();
        errorTab.override();
        documentsAndBindTab.submitTab();
        purchaseTab.fillTab(td).submitTab();

    }

    protected void homeCAPolicy(TestData td) {

        policy.getDefaultView().fillUpTo(td, aaa.main.modules.policy.home_ca.defaulttabs.BindTab.class, true);

        documentsAndBindTab.submitTab();
        errorTab.overrideAllErrors();
        errorTab.override();
        documentsAndBindTab.submitTab();
        purchaseTabCA.fillTab(td).submitTab();
    }

    private TestData getAdjustedTestData_Home() {
        TestData testData = getPolicyTD();

        TestData testDataApplicantTab = testData.getTestData(applicantTab.getMetaKey());

        TestData testDataAAAProductsOwned = testDataApplicantTab.getTestData(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel())
                .adjust(HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), "2111111111111110");

        TestData applicantTabAdjusted = testDataApplicantTab
                .adjust(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), testDataAAAProductsOwned);

        testData.adjust(applicantTab.getMetaKey(), applicantTabAdjusted).resolveLinks();
        return testData;
    }

    private TestData getAdjustedTestData_Auto() {
        TestData testData = getPolicyTD();

        TestData testDataGeneralTab = testData.getTestData(generalTab.getMetaKey());

        TestData testDataAAAProductsOwned = testDataGeneralTab.getTestData(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel())
                .adjust(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel(), "2111111111111110");

        TestData generalTabAdjusted = testDataGeneralTab
                .adjust(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), testDataAAAProductsOwned);

        testData.adjust(generalTab.getMetaKey(), generalTabAdjusted).resolveLinks();
        return testData;
    }

    public static final String UPDATE_MEMBERSHIP_NUMBER = "UPDATE membershipsummaryentity mse SET  mse.ordermembershipnumber = '4290023796712001' WHERE mse.id IN (" +
            "SELECT ms.id FROM policysummary ps JOIN membershipsummaryentity ms ON ms.id = ps.membershipsummary_id and PS.policynumber='";

    public static final String UPDATE_PRIOR_MEMBERSHIP_NUMBER = "UPDATE otherorpriorpolicy op SET op.policynumber = '4290023796712001'" +
            "WHERE op.id IN (SELECT op.id FROM policysummary ps JOIN otherorpriorpolicy op ON op.policydetail_id = ps.policydetail_id AND op.productcd = 'membership' AND ps.policynumber = '";

    public static final String GET_STATUS = "SELECT ms.membershipstatus AS msstatus, ms.id AS msid FROM policysummary ps JOIN membershipsummaryentity ms ON ms.id = ps.membershipsummary_id where ps.policynumber = '";

    public static final String GET_STATUS_2 ="' ORDER BY msid DESC";

}
