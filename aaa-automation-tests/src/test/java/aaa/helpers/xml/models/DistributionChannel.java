package aaa.helpers.xml.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://www.aaancnuie.com/DCS/2012/01/DocumentDistribution")
public class DistributionChannel {
	@XmlElement(name = "DeliveryStatus")
	String deliveryStatus;

	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	@Override
	public String toString() {
		return "DistributionChannel{" +
				"deliveryStatus='" + deliveryStatus + '\'' +
				'}';
	}
}
