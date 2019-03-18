package aaa.helpers.xml.model.pasdoc;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof RequestContext)) {
			return false;
		}
		RequestContext that = (RequestContext) o;
		return application.equals(that.application) &&
				correlationId.equals(that.correlationId) &&
				eventGroup.equals(that.eventGroup) &&
				lob.equals(that.lob) &&
				requestCategory.equals(that.requestCategory) &&
				requestCreationDateTime.equals(that.requestCreationDateTime) &&
				userFullName.equals(that.userFullName) &&
				userId.equals(that.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(application, correlationId, eventGroup, lob, requestCategory, requestCreationDateTime, userFullName, userId);
	}
}
