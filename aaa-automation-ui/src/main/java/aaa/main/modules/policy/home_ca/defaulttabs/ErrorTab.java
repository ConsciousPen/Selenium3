package aaa.main.modules.policy.home_ca.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.abstract_tabs.CommonErrorTab;
import aaa.toolkit.webdriver.customcontrols.FillableErrorTable;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Created by lkazarnovskiy on 8/8/2017.
 */
public class ErrorTab extends CommonErrorTab {

    public Table tableErrors = new Table(By.xpath(".//form[@id='errorsForm']//table"));
    public Table tableBaseErrors = new Table(By.xpath(".//form[@id='errorsForm']//table[2]"));

    public ErrorTab() {
        super(HomeCaMetaData.ErrorTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonOverride.click();
        new BindTab().submitTab();
        return this;
    }

    @Override
    public FillableErrorTable getErrorsControl() {
        return getAssetList().getAsset(HomeCaMetaData.ErrorTab.ERROR_OVERRIDE);
    }

}
