package aaa.admin.pages.reports;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;

public class OperationalReportSummaryPage {

    public static StaticElement labelCaptionMain = new StaticElement(By.xpath("//table[@id='workbook']//table[@id='Policy Status']/tbody/tr[1]/td[1]"));
    public static StaticElement labelCaptionPeriod = new StaticElement(By.xpath("//table[@id='workbook']//table[@id='Policy Status']/tbody/tr[2]/td[1]"));
    public static StaticElement labelCaptionRun = new StaticElement(By.xpath("//table[@id='workbook']//table[@id='Policy Status']/tbody/tr[3]/td[1]"));

    public static Table tableWorkBook = new Table(By.xpath("//table[@id='workbook']"));
    public static Table tableReportData = new Table(By.xpath("//table[@id='workbook']//table//table"));
    public static Table tableTriggers = new Table(By.id("triggersForm:triggersTable"));
}
