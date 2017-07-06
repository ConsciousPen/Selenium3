package aaa.admin.modules.reports.operationalreports.defaulttabs;

import org.openqa.selenium.By;

import aaa.admin.metadata.reports.OperationalReportsMetaData;
import aaa.common.DefaultTab;
import aaa.common.Tab;
import aaa.common.pages.Page;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class OperationalReportsTab extends DefaultTab {
    public static Button buttonGenerateReport = new Button(By.id("jobsForm:generateReport"));
    public static Button buttonSchedule = new Button(By.id("jobsForm:schedule"));

    public OperationalReportsTab() {
        super(OperationalReportsMetaData.OperationalReportsTab.class);
        assetList = new AssetList(By.xpath(Page.OPERATIONAL_REPORTS_ASSETLIST_CONTAINER), metaDataClass);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
