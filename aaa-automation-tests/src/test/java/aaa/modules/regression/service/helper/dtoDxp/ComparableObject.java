package aaa.modules.regression.service.helper.dtoDxp;

import aaa.modules.regression.service.helper.RestBodyRequest;

public class ComparableObject <T extends RestBodyRequest> {
	public String changeType;
	public T data;
}
