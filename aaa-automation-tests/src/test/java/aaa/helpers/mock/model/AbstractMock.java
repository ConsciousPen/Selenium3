package aaa.helpers.mock.model;

import static toolkit.verification.CustomAssertions.assertThat;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import aaa.helpers.mock.MockRequest;
import aaa.helpers.mock.MockResponse;
import aaa.helpers.mock.MockType;
import aaa.utils.excel.bind.BindHelper;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;
import toolkit.exceptions.IstfException;

public class AbstractMock implements UpdatableMock {
	@ExcelTransient
	private MockType type;

	@Override
	public MockType getType() {
		if (this.type == null) {
			type = Arrays.stream(MockType.values()).filter(mockType -> mockType.getMockModel().equals(this.getClass())).findFirst()
					.orElseThrow(() -> new IstfException(String.format("Class \"%s\" is related to unknown mock type", this.getClass().getName())));
		}
		return this.type;
	}

	@Override
	public UpdatableMock merge(UpdatableMock otherMock) {
		assertThat(otherMock).as("Unable to merge objects of different classes").hasSameClassAs(this);

		//TODO-dchubkov: if otherMock have no IDs assume it is single request/responces mock (check request is only one object?)
		List<List<Object>> otherResponsesSets = getResponsesSetsWithSameIDs(otherMock);
		List<String> thisIds = getIds(getRequests(this));

		//TODO-dchubkov: add processing of multiple request/responses pairs
		for (List<Object> otherResponses : otherResponsesSets) {
			Object otherRequest = getId(otherResponses);
			boolean hasNotMatchedRequest = false;
			boolean hasNotMatchedResponces = false;

			List<Object> thisResponses = getResponces(this);
			for (Object otherResponse : otherResponses) {
				for (Field responseField : BindHelper.getAllAccessibleFieldsFromThisAndSuperClasses(getClass())) {
					if (responseField.isAnnotationPresent(ExcelColumnElement.class) && responseField.getAnnotation(ExcelColumnElement.class).isPrimaryKey()) {
						continue;
					}
					//TODO-dchubkov: if generated field - also continue

					thisResponses = filterObjectsBy(responseField, BindHelper.getFieldValue(responseField, otherResponse), otherResponse, thisResponses, otherResponses.size());
					if (thisResponses.isEmpty()) {
						hasNotMatchedResponces = true;
						break;
					}
				}
			}

			if (!hasNotMatchedResponces) {
				List<Object> thisRequests = getRequests(getIds(thisResponses), this);
				//List<Object> thisRequests = getRequests(getIds(thisResponses), this);
				//List<Object> thisRequests = getRequests(this);
				for (Field requestField : BindHelper.getAllAccessibleFieldsFromThisAndSuperClasses(getClass())) {
					if (requestField.isAnnotationPresent(ExcelColumnElement.class) && requestField.getAnnotation(ExcelColumnElement.class).isPrimaryKey()) {
						continue;
					}
					//TODO-dchubkov: if generated field - also continue
					//TODO-dchubkov: check generated fields are the same in request and responce (e.g. membership number)

					thisRequests = filterObjectsBy(requestField, BindHelper.getFieldValue(requestField, otherRequest), otherRequest, thisRequests, 1);
					if (thisRequests.isEmpty()) {
						hasNotMatchedRequest = true;
						break;
					}
				}
			}

			if (hasNotMatchedRequest || hasNotMatchedResponces) {
				String newId = generateNewId(thisIds, otherRequest, otherResponses);
				//TODO-dchubkov: add values for generated fields
				thisIds.add(newId);
				addRequest(otherRequest);
				addResponces(otherResponses);
			}

		}

		//TODO-dchubkov: process other table objects
		//TODO-dchubkov: return null if no updated were done
		return this;
	}

	private void addResponces(List<Object> otherResponses) {
		//TODO-dchubkov: to be done...
	}

	private void addRequest(Object otherRequest) {
		//TODO-dchubkov: to be done...
	}

	private String generateNewId(List<String> thisIds, Object otherRequest, List<Object> otherResponses) {
		//TODO-dchubkov: to be done...
		return null;
	}

	private List<Object> getRequests(List<String> ids, AbstractMock abstractMock) {
		//TODO-dchubkov: to be done...
		return null;
	}

	private List<Object> filterObjectsBy(Field responseField, Object fieldValue, Object otherResponse, List<Object> thisResponses, int size) {
		//TODO-dchubkov: to be done...
		return null;
	}

	private List<Object> getResponces(AbstractMock abstractMock) {
		//TODO-dchubkov: to be done...
		return null;
	}

	private Object getId(List<Object> otherResponses) {
		//TODO-dchubkov: to be done...
		return null;
	}

	private List<String> getIds(List<?> requests) {
		//TODO-dchubkov: to be done...
		return null;
	}

	private List<?> getRequests(UpdatableMock mock) {
		//TODO-dchubkov: if no MockRequest annotation found try find field by "Request" name pattern?
		//TODO-dchubkov: add exception messages
		Field requestsField = BindHelper.getAllAccessibleFields(getClass(), true).stream().filter(f -> f.isAnnotationPresent(MockRequest.class))
				.findFirst().orElseThrow(() -> new IstfException("................................"));

		return BindHelper.getValueAsList(requestsField, mock);
	}

	private List<List<Object>> getResponsesSetsWithSameIDs(UpdatableMock mock) {
		List<List<Object>> responsesSets = new ArrayList<>();
		//TODO-dchubkov: if no MockResponse annotation found try find field by "Response" name pattern?
		//TODO-dchubkov: add exception messages
		Field responsesField = BindHelper.getAllAccessibleFields(getClass(), true).stream().filter(f -> f.isAnnotationPresent(MockResponse.class))
				.findFirst().orElseThrow(() -> new IstfException("................................"));

		Field idField = BindHelper.getAllAccessibleFieldsFromThisAndSuperClasses(BindHelper.getFieldType(responsesField)).stream()
				.filter(f -> f.isAnnotationPresent(ExcelColumnElement.class) && f.getAnnotation(ExcelColumnElement.class).isPrimaryKey()).findFirst()
				.orElseThrow(() -> new IstfException("................................"));

		List<?> allResponses = BindHelper.getValueAsList(responsesField, mock);
		List<String> responsesIDs = allResponses.stream().map(r -> String.valueOf(BindHelper.getFieldValue(idField, r))).distinct().collect(Collectors.toList());

		for (String id : responsesIDs) {
			List<Object> responsesWithSameId = allResponses.stream().filter(r -> id.equals(String.valueOf(BindHelper.getFieldValue(idField, r)))).collect(Collectors.toList());
			responsesSets.add(responsesWithSameId);
		}

		return responsesSets;
	}
}
