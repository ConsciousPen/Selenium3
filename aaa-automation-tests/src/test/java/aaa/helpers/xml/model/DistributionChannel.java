package aaa.helpers.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import aaa.main.enums.DocGenEnum;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = DocGenEnum.XmlnsNamespaces.DOC_URI2)
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
