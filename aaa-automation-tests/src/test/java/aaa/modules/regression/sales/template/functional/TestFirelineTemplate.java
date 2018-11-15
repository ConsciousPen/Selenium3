package aaa.modules.regression.sales.template.functional;

import aaa.admin.modules.security.PrivilegesEnum;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.PrivilegeEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.assertj.core.api.Assertions;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.webdriver.controls.Link;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static toolkit.verification.CustomAssertions.assertThat;

public class TestFirelineTemplate extends PolicyBaseTest {

    private ErrorTab errorTab = new ErrorTab();
    private ReportsTab reportsTab = new ReportsTab();

    protected void pas18302_firelineRuleForWoodShingleRoof(String zipCode, String address, int expectedFirelineScore, PrivilegeEnum.Privilege userPrivilege) {
        if (userPrivilege.equals(PrivilegeEnum.Privilege.L41)) {
            mainApp().open();
        } else {
            openAppNonPrivilegedUser(userPrivilege);
        }

        createCustomerIndividual();
        getPolicyType().get().initiate();

        TestData policyTd = getPolicyTD();
        //Override address and ZIP code. This address should return Fireline score 3 or more.
        policyTd.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(),
                HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
                HomeSSMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel()), zipCode);
        policyTd.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(),
                HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
                HomeSSMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel()), address);

        //Then you order PUBLIC_PROTECTION_CLASS new popup shows up if Address returns something, this method clicks popup OK.
        TestData ppcReportDialog = new SimpleDataProvider()
                .adjust(HomeSSMetaData.ReportsTab.PPCReportDialog.BTN_OK.getLabel(), "click");
        TestData ppcReports = new SimpleDataProvider()
                .adjust(HomeSSMetaData.ReportsTab.PublicProtectionClassRow.REPORT.getLabel(), "Report")
                .adjust(HomeSSMetaData.ReportsTab.PublicProtectionClassRow.PPC_REPORT_DIALOG.getLabel(), ppcReportDialog);
        TestData reportTab = policyTd.getTestData(HomeSSMetaData.ReportsTab.class.getSimpleName())
                .adjust(HomeSSMetaData.ReportsTab.PUBLIC_PROTECTION_CLASS.getLabel(), ppcReports);
        policyTd.adjust(HomeSSMetaData.ReportsTab.class.getSimpleName(), reportTab).resolveLinks();

        //Override Roof Type to 'Wood shingle/Wood shake'
        policyTd.adjust(TestData.makeKeyPath(HomeSSMetaData.PropertyInfoTab.class.getSimpleName(),
                HomeSSMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(),
                HomeSSMetaData.PropertyInfoTab.Construction.ROOF_TYPE.getLabel()), "Wood shingle/Wood shake");

        if (!userPrivilege.equals(PrivilegeEnum.Privilege.L41)) {
            policyTd.mask(TestData.makeKeyPath(new GeneralTab().getMetaKey(), HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel()));
        }

        getPolicyType().get().getDefaultView().fillUpTo(policyTd, ReportsTab.class, true);

        assertThat(reportsTab.tblFirelineReport.getRow(1)
                .getCell(HomeSSMetaData.ReportsTab.FirelineReportRow.WILDFIRE_SCORE.getLabel()).getValue()).isEqualTo(String.valueOf(expectedFirelineScore));

        reportsTab.submitTab();
        getPolicyType().get().getDefaultView().fillFromTo(policyTd, PropertyInfoTab.class, PurchaseTab.class, false);

        if (expectedFirelineScore > 2) {
            assertThat(errorTab.isVisible()).isTrue();
            errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS2240042);
            if (userPrivilege.equals(PrivilegeEnum.Privilege.L41)) {
                assertThat(errorTab.buttonOverride.isEnabled()).isTrue();
            } else {
                assertThat(errorTab.buttonOverride.isEnabled()).isFalse();
                assertThat(errorTab.buttonApproval.isEnabled()).isTrue();
            }
        } else {
            assertThat(errorTab.isVisible()).isFalse();
        }
    }
}
