package aaa.modules.regression.service.helper.dtoClaim;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class DriverInformation {

    @Getter
    @Setter
    private String firstName;

    @Getter
    @Setter
    private String lastName;

    @Getter
    @Setter
    private String licenseNumber;

    @Getter
    @Setter
    private String issuedState;

    @Getter
    @Setter
    private String relationToNamedInsured;

    @Getter
    @Setter
    private Date dateOfBirth;
}
