package aaa.modules.regression.postconditions;

import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.modules.BaseTest;
import toolkit.db.DBService;

public class DatabaseCleanHelper implements TestVinUploadPostConditions {
	protected static Logger log = LoggerFactory.getLogger(BaseTest.class);

	public static void cleanVinUploadTables(String configNames){
		try {
			String vehicleRefDataModelId = DBService.get().getValue(String.format(SELECT_ID_FROM_VEHICLEREFDATAMODEL, configNames)).get();
			DBService.get().executeUpdate(String.format(DELETE_FROM_VEHICLEREFDATAVIN_BY_VERSION, configNames));
			DBService.get().executeUpdate(String.format(DELETE_FROM_VEHICLEREFDATAMODEL_BY_ID, vehicleRefDataModelId));
			DBService.get().executeUpdate(String.format(DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_VERSION, configNames));
			DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_BY_EXPIRATION_DATE, state));
		} catch (NoSuchElementException e) {
			log.error("VINs with version names {} are not found in VIN table. VIN table part of DB cleaner was not executed", configNames);
		}
			}
}
