package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import aaa.helpers.xml.models.DistributionChannel;
import aaa.helpers.xml.models.DocumentPackage;
import aaa.helpers.xml.models.StandardDocumentRequest;

public final class DistributionChannelNode extends SearchBy<DistributionChannelNode, DistributionChannel> {
	public DistributionChannelNode deliveryStatus(String value) {
		return addCondition(dec -> Objects.equals(dec.getDeliveryStatus(), value));
	}

	@Override
	public List<DistributionChannel> search(StandardDocumentRequest sDocumentRequest) {
		Predicate<DistributionChannel> copiedCondition = getConditionAndClear();
		List<DistributionChannel> filteredDc = new ArrayList<>();
		for (DocumentPackage dp : standardDocumentRequest.documentPackage.search(sDocumentRequest)) {
			filteredDc.addAll(dp.getDistributionChannels().stream().filter(copiedCondition).collect(Collectors.toList()));
		}
		return filteredDc;
	}
}
