/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.admin.reports.operationalreports;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.admin.metadata.reports.OperationalReportsMetaData;
import aaa.admin.modules.reports.operationalreports.IOperationalReport;
import aaa.admin.modules.reports.operationalreports.OperationalReportType;
import aaa.admin.pages.reports.OperationalReportSummaryPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Andrey Shashenka
 * @name Test for Operational Report creation
 * @scenario
 * 1. Create Individual Customer
 * 2. Create Auto Policy
 * 3. Open Operational Reports
 * 4. Create Policy Report with specified Policy #
 * 5. Check Report headers and content (specified policy should be present)
 * @details
 */

public class TestOperationalReportCreation extends BaseTest {

    private PolicyType policyType = PolicyType.AUTO_SS;
    private IPolicy policy = policyType.get();
    private TestData tdPolicy = testDataManager.policy.get(policyType);

    private OperationalReportType reportType = OperationalReportType.OPERATIONAL_REPORT;
    private IOperationalReport report = reportType.get();
    private TestData tdReport = testDataManager.operationalReports.get(reportType);

    private String reportCaption, reportPeriod, runPeriodStart, runPeriodFinish, reportRunDate;

    @Test
    @TestInfo(component = "Platform.Admin")
    public void testOperationalReportCreation() {

        mainApp().open();

        createCustomerIndividual();

        policy.createPolicy(tdPolicy.getTestData("DataGather", "TestData")
                .adjust(tdPolicy.getTestData("Issue", "TestData").resolveLinks()));

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("TEST: Create Operational Report for Policy #" + policyNumber);

        report.create(tdReport.getTestData("DataGather", "TestData").adjust(
                TestData.makeKeyPath(OperationalReportsMetaData.OperationalReportsTab.class.getSimpleName(),
                        OperationalReportsMetaData.OperationalReportsTab.POLICY_NUMBER.getLabel()), policyNumber));

        log.info("TEST: Check Report headers and content");

        storeValues();

        CustomAssert.enableSoftMode();

        OperationalReportSummaryPage.labelCaptionMain.verify.value(reportCaption);
        OperationalReportSummaryPage.labelCaptionPeriod.verify.value(reportPeriod);
        OperationalReportSummaryPage.labelCaptionRun.verify.value(reportRunDate);

        OperationalReportSummaryPage.tableReportData.getRow(2).getCell(1).verify.value(policyNumber);

        CustomAssert.assertAll();
        CustomAssert.disableSoftMode();
    }

    private void storeValues() {
        TestData tdOperationReportTab = tdReport.getTestData("DataGather", "TestData").getTestData(
                OperationalReportsMetaData.OperationalReportsTab.class.getSimpleName());

        reportCaption = tdOperationReportTab.getValue(
                OperationalReportsMetaData.OperationalReportsTab.NAME.getLabel());

        runPeriodStart = tdOperationReportTab.getValue(
                OperationalReportsMetaData.OperationalReportsTab.ACTIVITY_DATE_FROM.getLabel());
        runPeriodFinish = tdOperationReportTab.getValue(
                OperationalReportsMetaData.OperationalReportsTab.ACTIVITY_DATE_TO.getLabel());
        reportPeriod = String.format("Reporting Period: %s - %s",
                runPeriodStart, runPeriodFinish);

        reportRunDate = String.format("Run Date: %s",
                TimeSetterUtil.getInstance().getCurrentTime().toString("MM/dd/yyyy"));
    }

}
