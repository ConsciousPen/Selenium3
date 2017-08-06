package aaa.modules.preconditions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import toolkit.utils.DBHelper;

public class CleanCache {
	private static DBHelper dbHelper = new DBHelper();
	private static Logger log = LoggerFactory.getLogger(CleanCache.class);
	private final static String sqlPath = "src/test/resources/clean_cache.sql";

	@Test
	public void cleanCache() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(sqlPath)));
			String line = null;
			dbHelper.connect();
			while ((line = in.readLine()) != null) {
				dbHelper.executeUpdate(line);
			}
			log.info("DB update +++++ Clean Cache is completed successfully ++++++\n");
			in.close();
			dbHelper.disconnect();
		} catch (IOException ie) {
			dbHelper.disconnect();
			Assert.fail("An error ocured during reading 'clean_cache.sql' file", ie);
		}
		catch (Exception e){
			dbHelper.disconnect();
			Assert.fail("An Excetion occured during SQL execution", e);
		}
	}
}
