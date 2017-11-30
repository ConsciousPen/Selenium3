package aaa.helpers.openl.testdata_builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.openl.model.OpenLPolicy;
import toolkit.exceptions.IstfException;

abstract class AutoSSTestDataGenerator<P extends OpenLPolicy> extends TestDataGenerator<P> {
	private Random random = new Random();

	protected String getGeneralTabResidence(boolean isHomeOwner) {
		List<String> isHomeOwnerOptions = Arrays.asList("Own Home", "Own Condo", "Own Mobile Home");
		List<String> notHomeOwnerOptions = Arrays.asList("Rents Multi-Family Dwelling", "Rents Single-Family Dwelling", "Lives with Parent", "Other");
		if (isHomeOwner) {
			return isHomeOwnerOptions.get(random.nextInt(isHomeOwnerOptions.size()));
		}
		return notHomeOwnerOptions.get(random.nextInt(notHomeOwnerOptions.size()));
	}

	protected String getGeneralTabTerm(int term) {
		switch (term) {
			case 12:
				return "Annual";
			case 6:
				return "Semi-Annual";
			default:
				throw new IstfException("Unable to build test data. Unsupported openL policy term: " + term);
		}
	}

	protected String generalTabIsAdvanceShopping(boolean isAdvanceShopping) {
		if (isAdvanceShopping) {
			throw new IstfException("Unknown mapping for isAdvanceShopping = true");
		}
		return "No Discount";
	}

	protected String getGeneralTabPriorBILimit(String priorBILimit) {
		List<String> priorBILimit100xx = new ArrayList<>();
		priorBILimit100xx.add("None");
		priorBILimit100xx.add(new Dollar(15_000) + "/" + new Dollar(30_000));
		priorBILimit100xx.add(new Dollar(20_000) + "/" + new Dollar(40_000));
		priorBILimit100xx.add(new Dollar(25_000) + "/" + new Dollar(50_000));
		priorBILimit100xx.add(new Dollar(30_000) + "/" + new Dollar(60_000));
		priorBILimit100xx.add(new Dollar(50_000) + "/" + new Dollar(100_000));
		switch (priorBILimit) {
			case "100/XX":
				return priorBILimit100xx.get(random.nextInt(priorBILimit100xx.size()));
			//TODO-dchubkov: add other cases:
			default:
				throw new IstfException("Unknown mapping for priorBILimit = " + priorBILimit);
		}
	}
}
