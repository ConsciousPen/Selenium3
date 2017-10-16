package aaa.helpers.docgen.searchNodes;

import aaa.helpers.xml.models.DistributionChannel;
import aaa.helpers.xml.models.StandardDocumentRequest;

import java.util.ArrayList;
import java.util.List;

public final class DistributionChannelNode extends SearchBy<DistributionChannelNode, DistributionChannel> {
	public DistributionChannelNode deliveryStatus(String value) {
		return addCondition("DeliveryStatus", DistributionChannel::getDeliveryStatus, value);
	}

	@Override
	public List<DistributionChannel> search(StandardDocumentRequest sDocumentRequest) {
		List<DistributionChannel> filteredDc = new ArrayList<>();
		standardDocumentRequest.documentPackage.search(sDocumentRequest).forEach(l -> filteredDc.addAll(filter(l.getDistributionChannels())));
		clearConditions();
		return filteredDc;
	}

	@Override
	public String getNodePath() {
		return "\\standardDocumentRequest\\DocumentPackage\\DistributionChannel";
	}
}
