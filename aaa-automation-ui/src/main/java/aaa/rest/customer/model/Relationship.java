package aaa.rest.customer.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Relationship {

   private String id;
   private String relationshipCustomerNumber;
   private String relationshipRole;
   private String relationshipDescription;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelationshipCustomerNumber() {
        return relationshipCustomerNumber;
    }

    public void setRelationshipCustomerNumber(String relationshipCustomerNumber) {
        this.relationshipCustomerNumber = relationshipCustomerNumber;
    }

    public String getRelationshipRole() {
        return relationshipRole;
    }

    public void setRelationshipRole(String relationshipRole) {
        this.relationshipRole = relationshipRole;
    }

    public String getRelationshipDescription() {
        return relationshipDescription;
    }

    public void setRelationshipDescription(String relationshipDescription) {
        this.relationshipDescription = relationshipDescription;
    }

}
