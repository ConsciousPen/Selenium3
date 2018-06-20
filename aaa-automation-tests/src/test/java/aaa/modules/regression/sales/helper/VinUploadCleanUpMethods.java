package aaa.modules.regression.sales.helper;

import static aaa.helpers.db.queries.VehicleQueries.DELETE_VEHICLEREFDATAVIN_BY_ID;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.main.enums.DefaultVinVersions;
import toolkit.db.DBService;

public class VinUploadCleanUpMethods {
	protected static Logger log = LoggerFactory.getLogger(VinUploadCleanUpMethods.class);

	/**
	 *
	 * @param listOfVinIds
	 * @param version
	 */
	public static void deleteVinsById(List<String> listOfVinIds) {
		for (String id : listOfVinIds) {
			if (id != null && !id.isEmpty()) {
				DBService.get().executeUpdate(String.format(DELETE_VEHICLEREFDATAVIN_BY_ID, id));
			}else{
				log.info(id + " : id is not present\n\n");
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
			else{
				log.info(vin + " : vin is not present\n\n");
			}
		}
	}

}
