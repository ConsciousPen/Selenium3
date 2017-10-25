package aaa.helpers.docgen.searchNodes;

import aaa.helpers.AaaMarkupParser;
import aaa.helpers.xml.models.StandardDocumentRequest;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;

public abstract class SearchBy<N, D> {
	public static StandardDocumentRequestNode standardDocumentRequest = new StandardDocumentRequestNode();

	private static ThreadLocal<Map<String, String>> commonSearchCriteriaMap = ThreadLocal.withInitial(LinkedHashMap::new);
	private ThreadLocal<Map<String, Pair<Function<D, String>, String>>> conditionsMap = ThreadLocal.withInitial(LinkedHashMap::new);

	public abstract List<D> search(StandardDocumentRequest sDocumentRequest);
	protected abstract String getNodePath();

	@SuppressWarnings("unchecked")
	protected final N addCondition(String conditionName, Function<D, String> conditionFunction, String expectedValue) {
		String searchCriteriaPath = getNodePath() + "\\" + conditionName;
		conditionsMap.get().put(searchCriteriaPath, Pair.of(conditionFunction, expectedValue));
		commonSearchCriteriaMap.get().put(searchCriteriaPath, expectedValue);
		return (N) this;
	}

	protected void clearConditions() {
		conditionsMap.get().clear();
	}

	public List<D> filter(D input) {
		return filter(Collections.singletonList(input));
	}

	public List<D> filter(List<D> inputList) {
		if (conditionsMap.get().isEmpty()) {
			return inputList;
		}

		List<D> filteredList = new ArrayList<>();
		for (D d : inputList) {
			boolean allConditionsAreMet = true;
			for (Map.Entry<String, Pair<Function<D, String>, String>> condition : conditionsMap.get().entrySet()) {
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
		for (Map.Entry<String, String> searchCriteria : commonSearchCriteriaMap.get().entrySet()) {
			conditionsOutput.append("  \"").append(searchCriteria.getKey()).append("\" -> \"").append(searchCriteria.getValue()).append("\"\n");
		}
		conditionsOutput.append("}");
		commonSearchCriteriaMap.get().clear();
		return conditionsOutput.toString();
	}

	private boolean isConditionMet(String actualValue, String expectedValue) {
		if (Objects.nonNull(actualValue) && Objects.nonNull(expectedValue) && expectedValue.startsWith(AaaMarkupParser.CONTAINS_PREFIX)) {
			return actualValue.contains(expectedValue.replaceAll(AaaMarkupParser.CONTAINS_PREFIX, ""));
		}
		return Objects.equals(actualValue, expectedValue);
	}
}
