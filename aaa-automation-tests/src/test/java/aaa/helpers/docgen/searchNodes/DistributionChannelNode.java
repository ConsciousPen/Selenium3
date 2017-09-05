package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import aaa.helpers.xml.models.DistributionChannel;
import aaa.helpers.xml.models.DocumentPackage;

public final class DistributionChannelNode extends SearchBy<DistributionChannelNode, DistributionChannel> {
	public DistributionChannelNode deliveryStatus(String value) {
		return addCondition(dec -> Objects.equals(dec.getDeliveryStatus(), value));
	}

	@Override
	public List<DistributionChannel> search(List<DocumentPackage> documentsList) {
		List<DistributionChannel> filteredDc = new ArrayList<>();
		for (DocumentPackage dp : documentPackage.search(documentsList)) {
			filteredDc.addAll(dp.getDistributionChannels().stream().filter(getConditionAndClear()).collect(Collectors.toList()));
		}
		return filteredDc;
	}
}
