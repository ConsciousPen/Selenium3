package aaa.helpers.rest.dtoDxp;

import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Vadym Zhytkevych on 8/9/2018.
 */
@ApiModel(description = "Ordered report information")
public class Report  {

    @ApiModelProperty(value = "Accident violation date", example = "2016-07-09", required = true)
     public Date accidentViolationDt;

    @ApiModelProperty(value = "Report conviction date", example = "2016-09-09", required = true)
     public Date convictionDt;

    @ApiModelProperty(value = "Source of reports", example = "CLUE", required = true)
    public String activitySource;
}
