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
			if(vinNumber.length()>8){
				vinNumber = vinNumber.substring(0,8) +"%";
			}
			String query = String.format(VehicleQueries.DELETE_FROM_VEHICLEREFDATAVIN_BY_VIN_AND_VERSION, vinNumber, vinVersion);
			DBService.get().executeUpdate(query);
		} catch (NoSuchElementException e) {
			log.error("VINs {} were not found in VehicleRefDdataVinControl.", vinNumber);
		}
	}
}
