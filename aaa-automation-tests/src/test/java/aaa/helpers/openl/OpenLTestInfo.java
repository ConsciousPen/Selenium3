package aaa.helpers.openl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import aaa.helpers.openl.model.OpenLPolicy;
import toolkit.exceptions.IstfException;

public class OpenLTestInfo<P extends OpenLPolicy> {
	private String state;
	private String openLFilePath;
	private List<P> openLPolicies;
	private Throwable exception;
	private String customerNumber;
	
	OpenLTestInfo() {}
	
	OpenLTestInfo(String state, String openLFilePath, List<P> openLPolicies) {
		this.state = state;
		this.openLFilePath = openLFilePath;
		this.openLPolicies = new ArrayList<>(openLPolicies);
	}
	
	public String getState() {
		return state;
	}
	
	void setState(String state) {
		this.state = state;
	}
	
	public String getOpenLFilePath() {
		return this.openLFilePath;
	}
	
	void setOpenLFilePath(String openLFilePath) {
		this.openLFilePath = openLFilePath;
	}
	
	public List<P> getOpenLPolicies() {
		return Collections.unmodifiableList(openLPolicies);
	}
	
	void setOpenLPolicies(List<P> openLPolicies) {
		this.openLPolicies = new ArrayList<>(openLPolicies);
	}
	
	public boolean isFailed() {
		return this.exception != null;
	}
	
	public Throwable getException() {
		return this.exception;
	}
	
	void setException(Throwable exception) {
		this.exception = exception;
	}
	
	public P getOpenLPolicy(int number) {
		return this.openLPolicies.stream().filter(p -> p.getNumber() == number).findFirst().orElseThrow(() -> new IstfException("There is no policy with number " + number));
	}
	
	public String getCustomerNumber() {
		return customerNumber;
	}
	
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}
}
