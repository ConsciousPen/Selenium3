package aaa.helpers.rest.dtoDxp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Vadym Zhytkevych on 8/9/2018.
 */
@ApiModel(description = "Ordered report information")
public class DrivingRecord {

    private static final String DATE_ONLY_FORMAT = "yyyy-MM-dd";

    @ApiModelProperty(value = "Report accident date", example = "2016-09-09", required = true)
    public String accidentDate;

    @ApiModelProperty(value = "Source of drivingRecords", example = "CLUE", required = true)
    public String activitySource;
}
