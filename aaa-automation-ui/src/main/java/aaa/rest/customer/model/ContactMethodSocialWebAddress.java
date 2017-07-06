package aaa.rest.customer.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import aaa.rest.IModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactMethodSocialWebAddress extends ContactMethod implements IModel {

    public ContactMethodSocialWebAddress() {
    }

    public ContactMethodSocialWebAddress(ContactMethodSocialWebAddress contactMethod) {
        super(contactMethod);
    }
}
