package aaa.admin.metadata.agencyvendor;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public final  class AgencyTransferMetaData {
        public static final class AgencyTransferTab extends MetaData {
        public static final AssetDescriptor<StaticElement> TRANSFER_ID = declare("Transfer Id", StaticElement.class);
        public static final AssetDescriptor<StaticElement> STATUS = declare("Status", StaticElement.class);
        public static final AssetDescriptor<RadioGroup> TRANSFER_TYPE = declare("Transfer Type", RadioGroup.class);
        public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
        public static final AssetDescriptor<RadioGroup> TRANSFER_EFFECTIVE_UPON = declare("Transfer Effective Upon", RadioGroup.class);
        public static final AssetDescriptor<TextBox> TRANSFER_EFFECTIVE_DATE = declare("Transfer Effective Date", TextBox.class);
        public static final AssetDescriptor<RadioGroup> COMMISSION_IMPACT = declare("Commission Impact", RadioGroup.class);
        public static final AssetDescriptor<AssetList> TRANSFER_TARGET_SECTION = declare("TransferTargetSection", AssetList.class, TransferTargetSection.class, By.id("borTransferManagementForm:targetProducer"));
        public static final AssetDescriptor<AssetList> LOCATION_DIALOG = declare("LocationDialog", AssetList.class, LocationDialog.class);

        public static final class TransferTargetSection extends MetaData {
             public static final AssetDescriptor<ComboBox>  TARGET_INSURANCE_AGENT = declare("Insurance Agent", ComboBox.class, Waiters.AJAX, By.id("borTransferManagementForm:borTransferTarget_targetSubproducerCd"));
        }

        public static final class LocationDialog extends MetaData {
           public static final AssetDescriptor<Link> LOCATION_NAME = declare("Location Name", Link.class, Waiters.AJAX, false, By.id("borTransferManagementForm:changeSourceProducerCd"));
            public static final AssetDescriptor<TextBox> AGENCY_CODE = declare("Agency Code", TextBox.class, Waiters.AJAX,false, By.id("brokerSearchFromsource:brokerSearchCriteria_code"));
            public static final AssetDescriptor<Button> SEARCH_BORT = declare("Search", Button.class, Waiters.AJAX, false, By.id("brokerSearchFromsource:searchBtn"));
            public static final AssetDescriptor<Button> SELECT_BORT = declare("Agency Name", Button.class, Waiters.AJAX, false, By.id("brokerSearchFromsource:body_brokerSearchResultssource:0:name"));
            public static final AssetDescriptor<ComboBox> INSURANCE_AGENT = declare("Insurance Agent", ComboBox.class);
            public static final AssetDescriptor<Link> PREVIEW_REPORT = declare("Preview Report", Link.class, Waiters.AJAX, false, By.id("borTransferManagementForm:previewReport"));
        }
    }
}

