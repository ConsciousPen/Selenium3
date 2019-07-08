package aaa.modules.regression.sales.template.functional;

import static aaa.common.pages.SearchPage.tableSearchResults;
import static aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab.*;
import static aaa.main.pages.summary.PolicySummaryPage.buttonRenewals;
import static aaa.main.pages.summary.PolicySummaryPage.labelPolicyNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Files.contentOf;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.BooleanUtils;
import org.testng.annotations.BeforeTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableMap;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.PrivilegeEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.claim.BatchClaimHelper;
import aaa.helpers.claim.ClaimCASResponseTags;
import aaa.helpers.claim.datamodel.claim.CASClaimResponse;
import aaa.helpers.claim.datamodel.claim.Claim;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.toolkit.webdriver.customcontrols.ActivityInformationMultiAssetList;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;

/**
 * This template is used to test Batch Claim Logic.
 *
 * @author Andrii Syniagin
 */
public class TestCADriverAssignmentWithoutGenderTemplate extends CommonTemplateMethods {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_hhmmss");
    protected TestData adjusted;
    protected LocalDateTime policyExpirationDate;
    protected LocalDateTime policyEffectiveDate;
    protected String policyNumber;

    protected static DriverTab driverTab = new DriverTab();
    protected static GeneralTab generalTab = new GeneralTab();
    protected static PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    protected static DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    protected static AssignmentTab assignmentTab = new AssignmentTab();
    protected static PurchaseTab purchaseTab = new PurchaseTab();
    protected static ErrorTab errorTab = new ErrorTab();
    protected static DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
    protected static ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();

    protected void pas29418_DriverAssignmentRanking() {
        //Setup test data
        TestData testDataForFNI = getTestSpecificTD("TestData_DriverTab_Assignment_Data").resolveLinks();
        adjusted = getPolicyTD().adjust(testDataForFNI);

        //Create quote with X drivers
        createQuoteAndFillUpTo(adjusted, DriverTab.class);
    }

}

