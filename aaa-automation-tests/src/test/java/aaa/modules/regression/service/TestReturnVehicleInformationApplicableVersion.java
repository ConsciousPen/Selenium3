/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.rest.aaaAdmin.services.aaaVehicleInfoRs.v1.vinInfo.AAAVehicleServices;
import aaa.helpers.rest.aaaAdmin.services.aaaVehicleInfoRs.v1.vinInfo.model.AAABodyStyleByYearMakeModelSeries;
import aaa.helpers.rest.aaaAdmin.services.aaaVehicleInfoRs.v1.vinInfo.model.AAAMakeByYear;
import aaa.helpers.rest.aaaAdmin.services.aaaVehicleInfoRs.v1.vinInfo.model.AAAModelByYearMake;
import aaa.helpers.rest.aaaAdmin.services.aaaVehicleInfoRs.v1.vinInfo.model.AAASeriesByYearMakeModel;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.utils.TestInfo;

public class TestReturnVehicleInformationApplicableVersion extends PolicyBaseTest {

	private static final String EFFECTIVE_DATE = "2018-12-31";
	private static final String YEAR = "2001";
	private static final String PRODUCT_CD = "AAA_SS";
	private static final String STATE_CD = "AZ";
	private static final String MAKE = "BMW";
	private static final String FORM_TYPE = "";
	private static final String MODEL = "740I";
	private static final String SERIES = "740IL%20PROTECTION";

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12465"})
	public void pas12465_MakeByYear(@Optional("AZ") String state) {

		AAAMakeByYear aaaMakeByYearResponse =
				AAAVehicleServices.getMakes(YEAR, PRODUCT_CD, STATE_CD, FORM_TYPE, EFFECTIVE_DATE);

		assertThat(aaaMakeByYearResponse.getMakes()).isNotNull();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12465"})
	public void pas12465_ModelByMakeYear(@Optional("AZ") String state) {

		AAAModelByYearMake aaaModelByMakeYearResponse =
				AAAVehicleServices.getModels(YEAR,MAKE, PRODUCT_CD, STATE_CD, FORM_TYPE, EFFECTIVE_DATE);

		assertThat(aaaModelByMakeYearResponse.getModels()).isNotNull();
	}


	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12465"})
	public void pas12465_BodyStyleByYearMakeModelSeries(@Optional("AZ") String state) {

		AAABodyStyleByYearMakeModelSeries aaaBodyStyleByYearMakeModelSeriesResponse =
				AAAVehicleServices.getBodyStyle(YEAR, MAKE, MODEL, SERIES, PRODUCT_CD, STATE_CD, FORM_TYPE, EFFECTIVE_DATE);

		assertThat(aaaBodyStyleByYearMakeModelSeriesResponse.getBodyStyles()).isNotNull();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12465"})
	public void pas12465_SeriesByYearMakeModel(@Optional("AZ") String state) {

		AAASeriesByYearMakeModel aaaSeriesByYearMakeModel =
				AAAVehicleServices.getSeries(YEAR, MAKE, MODEL, PRODUCT_CD, STATE_CD, FORM_TYPE, EFFECTIVE_DATE);

		assertThat(aaaSeriesByYearMakeModel.getSeries()).isNotNull();
	}

}
