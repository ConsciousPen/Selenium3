/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.rest.dtoAdmin.responses.AAABodyStyleByYearMakeModelSeries;
import aaa.helpers.rest.dtoAdmin.responses.AAAMakeByYear;
import aaa.helpers.rest.dtoAdmin.responses.AAAModelByYearMake;
import aaa.helpers.rest.dtoAdmin.responses.AAASeriesByYearMakeModel;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.service.helper.HelperCommon;
import toolkit.utils.TestInfo;

public class TestReturnVehicleInformationApplicableVersion extends PolicyBaseTest {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}


	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12465"})
	public void pas12465_MakeByYear(@Optional("CA") String state) {

		AAAMakeByYear aaaMakeByYearResponse =
				HelperCommon.getMakes("2001", "AAA_SS", "UT", "", "2000-01-01");

		assertThat(aaaMakeByYearResponse).isNotNull();
		log.info("\n\nList of Makes : " + aaaMakeByYearResponse.getListMake().toString() + "\n");
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12465"})
	public void pas12465_ModelByMakeYear(@Optional("CA") String state) {

		AAAModelByYearMake aaaModelByMakeYearResponse =
				HelperCommon.getModels("2001","BMW", "AAA_SS", "AZ","", "2000-01-01" );

		assertThat(aaaModelByMakeYearResponse).isNotNull();
		log.info("\n\nList of Makes : " + aaaModelByMakeYearResponse.getModels().toString() + "\n");
	}


	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12465"})
	public void pas12465_BodyStyleByYearMakeModelSeries(@Optional("CA") String state) {

		AAABodyStyleByYearMakeModelSeries aaaBodyStyleByYearMakeModelSeriesResponse =
				HelperCommon.getBodyStyle("2001", "BMW", "740I", "740IL%20PROTECTION", "AAA_SS", "AZ","", "2017-12-31");

		assertThat(aaaBodyStyleByYearMakeModelSeriesResponse).isNotNull();
		log.info("\n\nList of Body Styles : " + aaaBodyStyleByYearMakeModelSeriesResponse.getBodyStyle() + "\n");
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12465"})
	public void pas12465_SeriesByYearMakeModel(@Optional("CA") String state) {

		AAASeriesByYearMakeModel aaaSeriesByYearMakeModel =
				HelperCommon.getSeries("2001", "BMW", "740I", "AAA_SS", "AZ","", "2017-12-31");

		assertThat(aaaSeriesByYearMakeModel).isNotNull();
		log.info("\n\nSeries : " + aaaSeriesByYearMakeModel.getSeries() + "\n");
	}


}
