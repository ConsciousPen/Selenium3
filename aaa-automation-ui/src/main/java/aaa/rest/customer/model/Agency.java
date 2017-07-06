package aaa.rest.customer.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Agency {

    private String agencyCode;

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Agency agency = (Agency) o;

        return agencyCode != null ? agencyCode.equals(agency.agencyCode) : agency.agencyCode == null;
    }

    @Override public int hashCode() {
        return agencyCode != null ? agencyCode.hashCode() : 0;
    }

    @Override public String toString() {
        return "Agency{" +
                "agencyCode='" + agencyCode + '\'' +
                '}';
    }
}
