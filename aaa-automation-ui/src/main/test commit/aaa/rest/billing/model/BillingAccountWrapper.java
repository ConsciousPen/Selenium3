package aaa.rest.billing.model;

import aaa.rest.IModel;
import aaa.rest.wrapper.AbstractModelWrapper;
import aaa.rest.wrapper.ModelWrapper;

@ModelWrapper(modelClass = BillingAccount.class)
public class BillingAccountWrapper extends AbstractModelWrapper implements IModel {

    public BillingAccountWrapper() {
    }

    public BillingAccountWrapper fromIds(String... accountIds) {
        for (String accountId : accountIds) {
            getModels().add(new BillingAccount(accountId));
        }
        return this;
    }
}
