package aaa.helpers.rest.dtoDxp;

import java.util.List;

/**
 * DTO displays the coverage details for a given policy. This includes an array of policy level coverages and then
 * an array of vehicles and their coverages.
 * Created by gszdome on 3/26/2018.
 */
public class PolicyCoverageInfo{

    public List<Coverage> policyCoverages;
    public List<VehicleCoverageInfo> vehicleLevelCoverages;
    public List<Coverage> driverCoverages;
    public List<ValidationError> validations;
}
