package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class RequestContext {

	@XmlElement(name = "Application")
	private String application;

	@XmlElement(name = "CorrelationId")
	private String correlationId;

	@XmlElement(name = "EventGroup")
	private String eventGroup;

	@XmlElement(name = "LOB")
	private String lob;

	@XmlElement(name = "RequestCategory")
	private String requestCategory;

	@XmlElement(name = "RequestCreationDateTime")
	private String requestCreationDateTime;

	@XmlElement(name = "UserFullName")
	private String userFullName;

	@XmlElement(name = "UserId")
	private String userId;

	public String getApplication() {
		return application;
	}

	public RequestContext setApplication(String application) {
		this.application = application;
		return this;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public RequestContext setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
		return this;
	}

	public String getEventGroup() {
		return eventGroup;
	}

	public RequestContext setEventGroup(String eventGroup) {
		this.eventGroup = eventGroup;
		return this;
	}

	public String getLob() {
		return lob;
	}

	public RequestContext setLob(String lob) {
		this.lob = lob;
		return this;
	}

	public String getRequestCategory() {
		return requestCategory;
	}

	public RequestContext setRequestCategory(String requestCategory) {
		this.requestCategory = requestCategory;
		return this;
	}

	public String getRequestCreationDateTime() {
		return requestCreationDateTime;
	}

	public RequestContext setRequestCreationDateTime(String requestCreationDateTime) {
		this.requestCreationDateTime = requestCreationDateTime;
		return this;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public RequestContext setUserFullName(String userFullName) {
		this.userFullName = userFullName;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public RequestContext setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof RequestContext)) {
			return false;
		}
		RequestContext that = (RequestContext) o;
		return Objects.equals(application, that.application) &&
				Objects.equals(correlationId, that.correlationId) &&
				Objects.equals(eventGroup, that.eventGroup) &&
				Objects.equals(lob, that.lob) &&
				Objects.equals(requestCategory, that.requestCategory) &&
				Objects.equals(requestCreationDateTime, that.requestCreationDateTime) &&
				Objects.equals(userFullName, that.userFullName) &&
				Objects.equals(userId, that.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(application, correlationId, eventGroup, lob, requestCategory, requestCreationDateTime, userFullName, userId);
	}

	@Override
	public String toString() {
		return "RequestContext{" +
				"application='" + application + '\'' +
				", correlationId='" + correlationId + '\'' +
				", eventGroup='" + eventGroup + '\'' +
				", lob='" + lob + '\'' +
				", requestCategory='" + requestCategory + '\'' +
				", requestCreationDateTime='" + requestCreationDateTime + '\'' +
				", userFullName='" + userFullName + '\'' +
				", userId='" + userId + '\'' +
				'}';
	}
}
