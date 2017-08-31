package aaa.helpers.xml.models;

import aaa.main.enums.DocGenEnum;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = DocGenEnum.XmlnsNamespaces.DOC_URI2)
public class DistributionChannels {
	@XmlElement(name = "DistributionChannel")
	List<DistributionChannel> DistributionChannels = new ArrayList<>();

	public List<DistributionChannel> getDistributionChannels() {
		return DistributionChannels;
	}

	public void setDistributionChannels(List<DistributionChannel> distributionChannels) {
		DistributionChannels = distributionChannels;
	}

	@Override
	public String toString() {
		return "DistributionChannels{" +
				"DistributionChannels=" + DistributionChannels +
				'}';
	}
}
