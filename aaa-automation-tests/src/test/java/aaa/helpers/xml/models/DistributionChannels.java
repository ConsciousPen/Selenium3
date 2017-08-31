package aaa.helpers.xml.models;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://www.aaancnuie.com/DCS/2012/01/DocumentDistribution")
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
