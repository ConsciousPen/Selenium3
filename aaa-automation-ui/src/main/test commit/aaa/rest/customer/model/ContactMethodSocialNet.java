package aaa.rest.customer.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import aaa.rest.IModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactMethodSocialNet extends ContactMethod implements IModel {

    public ContactMethodSocialNet() {
    }

    public ContactMethodSocialNet(ContactMethodSocialNet contactMethod) {
        super(contactMethod);
    }
}
