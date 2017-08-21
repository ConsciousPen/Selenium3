package aaa.modules.preconditions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import aaa.helpers.constants.Groups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import toolkit.db.DBService;

public class CleanCache {
	private static Logger log = LoggerFactory.getLogger(CleanCache.class);
	private final static String sqlPath = "src/test/resources/clean_cache.sql";

	@Test(groups = Groups.PRECONDITION)
	public void cleanCache() {
		try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(sqlPath)))) {
			String line = null;
			while ((line = in.readLine()) != null) {
				DBService.get().executeUpdate(line);
			}
			log.info("DB update +++++ Clean Cache is completed successfully ++++++\n");
		} catch (IOException ie) {
			Assert.fail("An error ocured during reading 'clean_cache.sql' file", ie);
		}
	}
}
