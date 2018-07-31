package aaa.toolkit.webdriver.customcontrols;

import com.exigen.ipb.etcsa.controls.AdvancedSelector;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.waiters.Waiter;

public class AdvancedSelectorOR extends AdvancedSelector {

    public AdvancedSelectorOR(By locator) {
        super(locator);
        this.buttonAdd = new Button(this.actionControlsParent, By.xpath(".//*[@id='searchForm_shuttle_uwcode:add_shuttle_uwcode']"));
        this.buttonRemove = new Button(this.actionControlsParent, By.xpath(".//*[@id='searchForm_shuttle_uwcode:remove_shuttle_uwcode']"));
    }

    public AdvancedSelectorOR(By locator, Waiter waitBy) {
        super(locator, waitBy);
        this.buttonAdd = new Button(this.actionControlsParent, By.xpath(".//*[@id='searchForm_shuttle_uwcode:add_shuttle_uwcode']"));
        this.buttonRemove = new Button(this.actionControlsParent, By.xpath(".//*[@id='searchForm_shuttle_uwcode:remove_shuttle_uwcode']"));
    }

    public AdvancedSelectorOR(BaseElement<?, ?> parent, By locator) {
        super(parent, locator);
        this.buttonAdd = new Button(this.actionControlsParent, By.xpath(".//*[@id='searchForm_shuttle_uwcode:add_shuttle_uwcode']"));
        this.buttonRemove = new Button(this.actionControlsParent, By.xpath(".//*[@id='searchForm_shuttle_uwcode:remove_shuttle_uwcode']"));
    }

    public AdvancedSelectorOR(BaseElement<?, ?> parent, By locator, Waiter waitBy) {
        super(parent, locator, waitBy);
        this.buttonAdd = new Button(this.actionControlsParent, By.xpath(".//*[@id='searchForm_shuttle_uwcode:add_shuttle_uwcode']"));
        this.buttonRemove = new Button(this.actionControlsParent, By.xpath(".//*[@id='searchForm_shuttle_uwcode:remove_shuttle_uwcode']"));
    }


}
