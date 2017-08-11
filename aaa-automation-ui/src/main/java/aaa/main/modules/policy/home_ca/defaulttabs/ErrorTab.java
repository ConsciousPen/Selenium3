package aaa.main.modules.policy.home_ca.defaulttabs;

import aaa.common.Tab;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.abstract_tabs.CommonErrorTab;


/**
 * Created by lkazarnovskiy on 8/8/2017.
 */
public class ErrorTab extends CommonErrorTab {

    public ErrorTab() {super(HomeCaMetaData.ErrorTab.class);}

    @Override
    public Tab submitTab() {
        buttonOverride.click();
        new BindTab().submitTab();
        return this;
    }

}
