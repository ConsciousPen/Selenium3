/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.swaggerui.metadata;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class SwaggerUiMetaData {


    public static final class ServiceSelection extends MetaData {
        public static final AssetDescriptor<ComboBox> SERVICE_SELECTION = declare("Service Selection", ComboBox.class, By.id("baseUrl"));
        public static final AssetDescriptor<Link> PRODUCT_DATA_CONTEXTID_INSTANCES = declare("product-data{contextId}instances", Link.class);
        public static final AssetDescriptor<Link> PRODUCT_OPERATIONS = declare("product-operations", Link.class);
        public static final AssetDescriptor<Link> POST = declare("post", Link.class);
        public static final AssetDescriptor<Button> TRY_IT_OUT = declare("Try it out!", Button.class);
        public static final AssetDescriptor<TextBox> RESPONSE_CODE = declare("Response Code", TextBox.class);
    }


}
