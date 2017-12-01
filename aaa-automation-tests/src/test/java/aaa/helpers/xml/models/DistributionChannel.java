package aaa.helpers.xml.models;

import aaa.main.enums.DocGenEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = DocGenEnum.XmlnsNamespaces.DOC_URI2)
public class DistributionChannel {
	@XmlElement(name = "DeliveryStatus")
	String deliveryStatus;

	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	public DistributionChannel setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
		return this;
	}

	@Override
	public String toString() {
		return "DistributionChannel{" +
				"deliveryStatus='" + deliveryStatus + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DistributionChannel that = (DistributionChannel) o;

		return deliveryStatus != null ? deliveryStatus.equals(that.deliveryStatus) : that.deliveryStatus == null;
	}

	@Override
	public int hashCode() {
		return deliveryStatus != null ? deliveryStatus.hashCode() : 0;
	}
}
