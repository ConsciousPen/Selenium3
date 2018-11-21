package aaa.admin.pages.agencyvendor;

import aaa.admin.pages.AdminPage;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;

public class AgencyTransferPage extends AdminPage {
    public static Button buttonAddNewTransfer = new Button(By.id("borManagementForm:addButton"));
    public static Button buttonSearchTransfer = new Button(By.id("borManagementForm:searchButton"));
    public static Table tableTransfers = new Table(By.id("borManagementForm:body_borInfoTable"));
}
