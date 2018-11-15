package aaa.helpers.rest.dtoDxp;

import java.util.Map;

public class ComparableCoverage extends ComparableObject<PolicyCoverage> {
	public Map<String, ComparableObject<Coverage>> subCoverages;
}
