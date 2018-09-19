package aaa.modules.regression.service.helper.dtoClaim;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@ToString
public class Claim {

    @Getter
    @Setter
    private String claimNumber;

    @Getter
    @Setter
    private String policyReferenceNumber;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private Date dateOpened;

    @Getter
    @Setter
    private Date dateClosed;

    @Getter
    @Setter
    private Date dateOfLoss;

    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    private String matchCode;

    @Getter
    @Setter
    private DriverInformation driverInformation;

    @Getter
    @Setter
    private BigDecimal totalAmountPaid;

    @Getter
    @Setter
    private BigDecimal claimDeductible;

    @Getter
    @Setter
    private String liabilityCd;

    @SuppressWarnings("SpellCheckingInspection")
    @Getter
    @Setter
    private boolean subroFlag;

    @Getter
    @Setter
    private String lossSummary;

    @Getter
    @Setter
    private String driverOid;

}
