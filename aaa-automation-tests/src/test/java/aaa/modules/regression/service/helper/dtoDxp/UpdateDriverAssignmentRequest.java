package aaa.modules.regression.service.helper.dtoDxp;

import java.util.List;
import aaa.modules.regression.service.helper.RestBodyRequest;


/**
 * Request to update the Driver Assignment for a given vehicle/driver combination.
 * Multiple requests may be passed over in a single invocation of the service.
 * Created by gszdome on 5/16/2018.
 */
public class UpdateDriverAssignmentRequest implements RestBodyRequest {

	public List<DriverAssignmentRequest> assignmentRequests;
}
