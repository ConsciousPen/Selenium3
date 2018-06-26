package aaa.helpers.product;

import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.helpers.db.queries.VehicleQueries;
import aaa.modules.BaseTest;
import toolkit.db.DBService;

public class DatabaseCleanHelper {

	protected static Logger log = LoggerFactory.getLogger(BaseTest.class);

	public static void cleanVehicleRefDataVinTable(String vinNumber, String vinVersion) {
		try {
			if (vinNumber.length() > 8) {
				vinNumber = vinNumber.substring(0, 8) + "%";
			}

			if (vinNumber != null && !vinNumber.isEmpty()) {
				String query = String.format(VehicleQueries.DELETE_FROM_VEHICLEREFDATAVIN_BY_VIN_AND_VERSION, vinNumber, vinVersion);
				DBService.get().executeUpdate(query);
			} else {
				log.info("Vin was not processed : {}", vinNumber);
			}

		} catch (NoSuchElementException e) {
			log.error("VINs {} were not found in VehicleRefDdataVinControl.", vinNumber);
		}
	}

    public static void deleteVehicleRefDataVinTableByVinAndMaketext(String vinNumber, String makeText) {
        try {
            if (vinNumber.length() > 8) {
                vinNumber = vinNumber.substring(0, 8) + "%";
            }
            String query = String.format(VehicleQueries.DELETE_VEHICLEREFDATAVIN_BY_VIN_MAKETEXT, vinNumber, makeText);
            DBService.get().executeUpdate(query);
        } catch (NoSuchElementException e) {
            log.error("VINs {} were not found in VehicleRefDdataVin.", vinNumber);
        }
    }

    public static void updateVehicleRefDataVinTableByVinAndMaketext(String validFlag, String vinNumber, String vinVersion, String makeText) {
        try {
            if (vinNumber.length() > 8) {
                vinNumber = vinNumber.substring(0, 8) + "%";
            }
            String query = String.format(VehicleQueries.UPDATE_VEHICLEREFDATAVIN_VALID_BY_VIN_VERSION_MAKETEXT, validFlag, vinNumber, vinVersion, makeText);
            DBService.get().executeUpdate(query);
        } catch (NoSuchElementException e) {
            log.error("VINs {} were not found in VehicleRefDdataVin.", vinNumber);
        }
    }
}
