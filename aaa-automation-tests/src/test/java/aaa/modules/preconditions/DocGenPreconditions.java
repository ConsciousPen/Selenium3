package aaa.modules.preconditions;


import aaa.helpers.constants.Groups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import toolkit.db.DBService;

public class DocGenPreconditions {
	private static Logger log = LoggerFactory.getLogger(DocGenPreconditions.class);
	
	
	@Test(groups = Groups.PRECONDITION)
	public void enableRefundDoc() {
		
		String SQL_UPDATE = "UPDATE LOOKUPVALUE SET DISPLAYVALUE = 'FALSE' WHERE LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST "
				+ "WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'pcDisbursementEngine'";
		
		DBService.get().executeUpdate(SQL_UPDATE);
		
		
		log.info("DB update +++++ To enable refund doc lookup AAARolloutEligibilityLookup is updated (pcDisbursementEngine is set to FALSE) ++++++\n");
	}
	
	

}