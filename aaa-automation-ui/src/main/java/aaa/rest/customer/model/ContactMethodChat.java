package aaa.rest.customer.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import aaa.rest.IModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactMethodChat extends ContactMethod implements IModel {

    public ContactMethodChat() {
    }

    public ContactMethodChat(ContactMethodChat contactMethod) {
        super(contactMethod);
    }
}
