package aaa.modules.regression.service.helper.dtoDxp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Discount Information")
public class DiscountInfo {

    @ApiModelProperty(value = "Discount Code", example ="EMD")
    public String discountCd;

    @ApiModelProperty(value = "Discount Name", example ="eValue Discount")
    public String discountName;

}
