package aaa.helpers.rest.dtoClaim;

public class DriverInformation {

    private String firstName;

    private String lastName;

    private String licenseNumber;

    private String issuedState;

    private String relationToNamedInsured;

    private String dateOfBirth;
	private  String  claimDriverName;
    private  String  permissiveUse;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getIssuedState() {
        return issuedState;
    }

    public void setIssuedState(String issuedState) {
        this.issuedState = issuedState;
    }

    public String getRelationToNamedInsured() {
        return relationToNamedInsured;
    }

    public void setRelationToNamedInsured(String relationToNamedInsured) {
        this.relationToNamedInsured = relationToNamedInsured;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

	public String getClaimDriverName() {
		return claimDriverName;
	}

	public void setClaimDriverName(String claimDriverName) {
		this.claimDriverName = claimDriverName;
	}

    public String getPermissiveUse() {
        return permissiveUse;
    }

    public void setPermissiveUse(String permissiveUse) {
        this.permissiveUse = permissiveUse;
    }


    @Override
    public String toString() {
        return "DriverInformation{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", issuedState='" + issuedState + '\'' +
                ", relationToNamedInsured='" + relationToNamedInsured + '\'' +
                ", dateOfBirth=" + dateOfBirth + '\'' +
                ", claimDriverName=" + claimDriverName + '\'' +
                ", permissiveUse=" + permissiveUse +
                '}';
    }
}
