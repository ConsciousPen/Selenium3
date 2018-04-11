package aaa.modules.regression.service.helper.dtoDxp;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * This DTO covers the vehicle level coverage information. Each entity includes the OID of the vehicle it
 * covers as well as the coverages that are tied to that specific vehicle instance.
 * Created by gszdome on 3/26/2018.
 */
public class VehicleCoverageInfo {

    @ApiModelProperty(value = "OID", example = "moNsX3IYP-LrcTxUBUpGjQ")
    public String oid;

    public List<Coverage> coverages;
}
