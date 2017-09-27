package aaa.helpers.docgen.searchNodes;

import aaa.helpers.AaaMarkupParser;
import aaa.helpers.xml.models.StandardDocumentRequest;
import javafx.util.Pair;

import java.util.*;
import java.util.function.Function;

public abstract class SearchBy<N, D> {
	public static StandardDocumentRequestNode standardDocumentRequest = new StandardDocumentRequestNode();
	protected static Map<String, String> commonSearchCriteriaMap = new LinkedHashMap<>();
	protected Map<String, Pair<Function<D, String>, String>> conditionsMap = new LinkedHashMap<>();

	protected abstract String getNodePath();

	public abstract List<D> search(StandardDocumentRequest sDocumentRequest);

	@SuppressWarnings("unchecked")
	protected final N addCondition(String conditionName, Function<D, String> conditionFunction, String expectedValue) {
		String searchCriteriaPath = getNodePath() + "\\" + conditionName;
		conditionsMap.put(searchCriteriaPath, new Pair<>(conditionFunction, expectedValue));
		commonSearchCriteriaMap.put(searchCriteriaPath, expectedValue);
		return (N) this;
	}

	public List<D> filter(D input) {
		return filter(Collections.singletonList(input));
	}

	public List<D> filter(List<D> inputList) {
		if (conditionsMap.isEmpty()) {
			return inputList;
		}

		List<D> filteredList = new ArrayList<>();
		for (D d : inputList) {
			boolean allConditionsAreMet = true;
			for (Map.Entry<String, Pair<Function<D, String>, String>> condition : conditionsMap.entrySet()) {
				if (!isConditionMet(condition.getValue().getKey().apply(d), condition.getValue().getValue())) {
					allConditionsAreMet = false;
					break;
				}
			}

			if (allConditionsAreMet) {
				filteredList.add(d);
			}
		}
		return filteredList;
	}

	@Override
	public String toString() {
		StringBuilder conditionsOutput = new StringBuilder("SearchBy{\n");
		for (Map.Entry<String, String> searchCriteria : commonSearchCriteriaMap.entrySet()) {
			conditionsOutput.append("  \"").append(searchCriteria.getKey()).append("\" -> \"").append(searchCriteria.getValue()).append("\"\n");
		}
		conditionsOutput.append("}");
		commonSearchCriteriaMap.clear();
		return conditionsOutput.toString();
	}

	private boolean isConditionMet(String actualValue, String expectedValue) {
		if (Objects.nonNull(actualValue) && Objects.nonNull(expectedValue) && expectedValue.startsWith(AaaMarkupParser.CONTAINS_PREFIX)) {
			return actualValue.contains(expectedValue.replaceAll(AaaMarkupParser.CONTAINS_PREFIX, ""));
		}
		return Objects.equals(actualValue, expectedValue);
	}
}
