package aaa.modules.regression.sales.helper;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.main.enums.DefaultVinVersions;

public class VinUploadCleanUpMethods {
	protected static Logger log = LoggerFactory.getLogger(VinUploadCleanUpMethods.class);

	/**
	 *
	 * @param listOfVinIds
	 * @param version
	 */
	public static void deleteVinsById(List<String> listOfVinIds, DefaultVinVersions.DefaultVersions version) {
		for (String id : listOfVinIds) {
			if (id != null && !id.isEmpty()) {
				DatabaseCleanHelper.cleanVehicleRefDataVinTable(id, version.get());
			}else{

			}
		}
	}

	/**
	 *
	 * @param listOfVinNumbers
	 * @param version
	 */
	public static void deleteVinByVinNumberAndVersion(List<String> listOfVinNumbers, DefaultVinVersions.DefaultVersions version) {
		for (String vin : listOfVinNumbers) {
			if (vin != null && !vin.isEmpty()) {
				DatabaseCleanHelper.cleanVehicleRefDataVinTable(vin, version.get());
			}
		}
	}

}
