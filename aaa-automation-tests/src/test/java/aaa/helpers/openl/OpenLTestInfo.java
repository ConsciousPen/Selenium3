package aaa.helpers.openl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import aaa.helpers.openl.model.OpenLPolicy;

public class OpenLTestInfo<P extends OpenLPolicy> {
	private String openLFilePath;
	private List<P> openLPolicies;
	private Throwable exception;
	
	OpenLTestInfo(String openLFilePath, List<P> openLPolicies) {
		this.openLFilePath = openLFilePath;
		this.openLPolicies = new ArrayList<>(openLPolicies);
	}
	
	OpenLTestInfo(String openLFilePath, Throwable exception) {
		this.openLFilePath = openLFilePath;
		this.exception = exception;
	}
	
	public String getOpenLFilePath() {
		return this.openLFilePath;
	}
	
	public List<P> getOpenLPolicies() {
		return Collections.unmodifiableList(this.openLPolicies);
	}
	
	public boolean isFailed() {
		return exception != null;
	}
	
	public Throwable getException() {
		return exception;
	}
}
