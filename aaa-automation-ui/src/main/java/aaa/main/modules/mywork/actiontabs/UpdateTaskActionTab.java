package aaa.main.modules.mywork.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.main.metadata.MyWorkMetaData;
import toolkit.webdriver.controls.Button;

public class UpdateTaskActionTab extends ActionTab {

    public static Button buttonUpdate = new Button(By.id("taskUpdateForm:submitUpdate_footer"));
    public static Button buttonCancel = new Button(By.id("taskUpdateForm:navigateBack_footer"));

    public UpdateTaskActionTab() {
        super(MyWorkMetaData.UpdateTaskActionTab.class);
    }
}
