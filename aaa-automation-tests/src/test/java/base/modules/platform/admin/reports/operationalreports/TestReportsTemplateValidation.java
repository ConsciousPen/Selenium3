package base.modules.platform.admin.reports.operationalreports;

import org.testng.annotations.Test;

import aaa.admin.modules.reports.templates.ITemplate;
import aaa.admin.modules.reports.templates.TemplateType;
import aaa.admin.pages.reports.TemplatesSummaryPage;
import aaa.modules.BaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Andrey Shashenka
 * @name Test for Report Templates validation
 * @scenario
 * 1. Login to Operational Reports
 * 2. Open Templates tab
 * 3. Click Validate button
 * 4. Verify that "No Errors" message is present
 * @details
 */

public class TestReportsTemplateValidation extends BaseTest {

    private TemplateType templateType = TemplateType.TEMPLATE;
    private ITemplate template = templateType.get();

    @Test
    @TestInfo(component = "Platform.Admin")
    public void testReportsTemplateValidation() {

        opReportApp().open();

        template.validate();

        TemplatesSummaryPage.labelNoErrors.verify.present();
    }
}
