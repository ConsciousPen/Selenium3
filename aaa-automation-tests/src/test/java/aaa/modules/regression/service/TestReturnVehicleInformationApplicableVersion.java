/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.dtoAdmin.responses.AAABodyStyleByYearMakeModelSeries;
import aaa.modules.regression.service.helper.dtoAdmin.responses.AAAMakeByYear;
import aaa.modules.regression.service.helper.dtoAdmin.responses.AAAModelByYearMake;
import aaa.modules.regression.service.helper.dtoAdmin.responses.AAASeriesByYearMakeModel;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestReturnVehicleInformationApplicableVersion extends PolicyBaseTest {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}


	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9716"})
	public void pas12708_MakeByYear(@Optional("CA") String state) {

		AAAMakeByYear aaaMakeByYearResponse = HelperCommon.getMakes("2001");

		log.info("\n\nList of Makes : " + aaaMakeByYearResponse.getListMake().toString() + "\n");
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9716"})
	public void pas12708_ModelByMakeYear(@Optional("CA") String state) {

		AAAModelByYearMake aaaModelByMakeYearResponse = HelperCommon.getModels("BMW", "2001");

		log.info("\n\nList of Makes : " + aaaModelByMakeYearResponse.getModels().toString() + "\n");
	}


	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9716"})
	public void pas12708_BodyStyleByYearMakeModelSeries(@Optional("CA") String state) {

		AAABodyStyleByYearMakeModelSeries aaaBodyStyleByYearMakeModelSeriesResponse =
				HelperCommon.getBodyStyle("2001", "BMW", "740I", "740IL%20PROTECTION");

		log.info("\n\nBodyStyle : " + aaaBodyStyleByYearMakeModelSeriesResponse.getBodyStyle() + "\n");
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9716"})
	public void pas12708_SeriesByYearMakeModel(@Optional("CA") String state) {

		AAASeriesByYearMakeModel aaaSeriesByYearMakeModel =
				HelperCommon.getSeries("2001", "BMW", "740I");

		log.info("\n\nBodyStyle : " + aaaSeriesByYearMakeModel.getSeries() + "\n");
	}


}
