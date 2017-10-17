package aaa.admin.modules.administration.generateproductschema.views;

import aaa.admin.modules.administration.generateproductschema.defaulttabs.GenerateProductSchema;
import aaa.common.Workspace;

public class DefaultView extends Workspace {

    public DefaultView() {
        super();
        registerTab(GenerateProductSchema.class);
    }
}
