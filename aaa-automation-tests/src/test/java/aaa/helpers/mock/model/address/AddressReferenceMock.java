package aaa.helpers.mock.model.address;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import aaa.helpers.mock.model.AbstractMock;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public class AddressReferenceMock extends AbstractMock {
	@ExcelTransient
	public static final String FILE_NAME = "AddressReferenceMockData.xls";

	private List<AddressReference> addressReferences;

	@Override
	public String getFileName() {
		return FILE_NAME;
	}

	public List<AddressReference> getAddressReferences() {
		return Collections.unmodifiableList(addressReferences);
	}

	public void setAddressReferences(List<AddressReference> addressReferences) {
		this.addressReferences = new ArrayList<>(addressReferences);
	}

	public boolean hasAddress(String postalCode, String state) {
		return getAddressReferences().stream().anyMatch(a -> Objects.equals(postalCode, a.getPostalCode()) && Objects.equals(state, a.getState()));
	}

	@Override
	public String toString() {
		return "AddressReferenceMock{" +
				"addressReferences=" + addressReferences +
				'}';
	}
}
