package aaa.main.modules.policy.abstract_tabs;

import aaa.common.Tab;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Created by lkazarnovskiy on 8/8/2017.
 */
public abstract class Error extends Tab {

    protected Error(Class<? extends MetaData> mdClass) {
        super(mdClass);
    }

    public Table tblErrorsList = new Table(By.id("errorsForm:msgList"));
    public Button btnOverride = new Button(By.id("errorsForm:overrideRules"));
    public Button btnCancel = new Button(By.id("errorsForm:back"));

    public Tab cancel(){
        btnCancel.click();
        return this;
    }

}
