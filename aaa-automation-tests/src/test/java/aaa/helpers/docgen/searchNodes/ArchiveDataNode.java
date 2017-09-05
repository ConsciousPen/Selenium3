package aaa.helpers.docgen.searchNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import aaa.helpers.xml.models.ArchiveData;
import aaa.helpers.xml.models.DocumentPackage;

public final class ArchiveDataNode extends SearchBy<ArchiveDataNode, ArchiveData> {
	public DocumentDataElementADNode documentDataElement = new DocumentDataElementADNode();

	public ArchiveDataNode sectionName(String value) {
		return addCondition(ad -> Objects.equals(ad.getSectionName(), value));
	}

	@Override
	public List<ArchiveData> search(List<DocumentPackage> documentsList) {
		Predicate<ArchiveData> copiedCondition = getConditionAndClear();
		List<ArchiveData> filteredAd = new ArrayList<>();
		for (DocumentPackage dp : documentPackage.search(documentsList)) {
			if (copiedCondition.test(dp.getArchiveData())) {
				filteredAd.add(dp.getArchiveData());
			}
		}
		return filteredAd;
	}
}
