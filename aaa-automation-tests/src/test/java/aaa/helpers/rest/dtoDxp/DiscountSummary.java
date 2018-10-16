package aaa.helpers.rest.dtoDxp;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * API DTO to display discount summary info.
 */
@ApiModel(description = "Discount Summary Information")
public class DiscountSummary {

    @ApiModelProperty(value = "Policy Discounts", example ="policyDiscounts: [{ \"discountCd\": \"EMD\", \"discountName\": \"eValue Discount\" }]")
    public List<DiscountInfo> policyDiscounts;

    @ApiModelProperty(value = "Vehicle Discounts", example ="vehicleDiscounts: [{ \"discountCd\": \"ANT\", \"discountName\": \"Anti-Theft Discount\" }]")
    public List<DiscountInfo> vehicleDiscounts;

    @ApiModelProperty(value = "Driver Discounts", example ="driverDiscounts: [{ \"discountCd\": \"GSD\", \"discountName\": \"Good Student Discount\" }]")
    public List<DiscountInfo> driverDiscounts;

}
