package aaa.helpers.rest.aaaAdmin.services.aaaVehicleInfoRs.v1.vinInfo;

import aaa.admin.modules.IAdmin;
import aaa.helpers.rest.JsonClient;
import aaa.helpers.rest.aaaAdmin.services.aaaVehicleInfoRs.v1.vinInfo.model.AAABodyStyleByYearMakeModelSeries;
import aaa.helpers.rest.aaaAdmin.services.aaaVehicleInfoRs.v1.vinInfo.model.AAAMakeByYear;
import aaa.helpers.rest.aaaAdmin.services.aaaVehicleInfoRs.v1.vinInfo.model.AAAModelByYearMake;
import aaa.helpers.rest.aaaAdmin.services.aaaVehicleInfoRs.v1.vinInfo.model.AAASeriesByYearMakeModel;
import aaa.modules.regression.service.helper.HelperCommon;

public class AAAVehicleServices {
	private static final String AAA_VEHICLE_INFO_RS_PREFIX = "/aaa-admin/services/aaa-vehicle-info-rs/v1/vin-info/";

	private static final String DXP_RETRIEVE_BODYSTYLE_BY_YEAR_MAKE_MODEL_SERIES =
			AAA_VEHICLE_INFO_RS_PREFIX + "bodystyle-by-make-year-model/QUESTION_MARK/year=%s&make=%s&model=%s&Series=%s&productCd=%s&stateCd=%s&formType=%s&effectiveDate=%s";
	private static final String DXP_SERIES_BY_YEAR_MAKE_MODEL = AAA_VEHICLE_INFO_RS_PREFIX + "series-by-make-year-model/QUESTION_MARK/year=%s&make=%s&model=%s&productCd=%s&stateCd=%s&formType=%s&effectiveDate=%s";
	private static final String DXP_RETRIEVE_MODEL_BY_YEAR_MAKE = AAA_VEHICLE_INFO_RS_PREFIX + "model-by-make-year/QUESTION_MARK/year=%s&make=%s&productCd=%s&stateCd=%s&formType=%s&effectiveDate=%s";
	private static final String DXP_RETRIEVE_MAKE_BY_YEAR = AAA_VEHICLE_INFO_RS_PREFIX + "make-by-year/QUESTION_MARK/year=%s&productCd=%s&stateCd=%s&formType=%s&effectiveDate=%s";

	/**
	 * Hitting "/aaa-admin/services/aaa-vehicle-info-rs/v1/vin-info/" get series
	 * @param year
	 * @param make
	 * @param model
	 * @param productCd
	 * @param stateCd
	 * @param formType
	 * @param effectiveDate
	 * @return
	 */

	public static AAASeriesByYearMakeModel getSeries(String year, String make, String model, String productCd, String stateCd, String formType, String effectiveDate) {
		String url = HelperCommon.urlBuilderAdmin(String.format(DXP_SERIES_BY_YEAR_MAKE_MODEL, year, make, model, productCd, stateCd, formType, effectiveDate)).replace("/QUESTION_MARK/","?").trim();
		IAdmin.log.info("Hitting {}", url);
		return JsonClient.sendGetRequest(url, AAASeriesByYearMakeModel.class);
	}

	/**
	 * Hitting /aaa-admin/services/aaa-vehicle-info-rs/v1/vin-info/ to get makes
	 * @param year
	 * @param productCd
	 * @param stateCd
	 * @param formType
	 * @param effectiveDate
	 * @return
	 */

	public static AAAMakeByYear getMakes(String year, String productCd, String stateCd, String formType, String effectiveDate) {
		String url = HelperCommon.urlBuilderAdmin(String.format(DXP_RETRIEVE_MAKE_BY_YEAR, year, productCd, stateCd, formType, effectiveDate)).replace("/QUESTION_MARK/", "?").trim();
		IAdmin.log.info("hitting url {}", url);
		return JsonClient.sendGetRequest(url, AAAMakeByYear.class);
	}

	/**
	 * Hitting /aaa-admin/services/aaa-vehicle-info-rs/v1/vin-info/ get models
	 * @param year
	 * @param make
	 * @param productCd
	 * @param stateCd
	 * @param formType
	 * @param effectiveDate
	 * @return
	 */
	public static AAAModelByYearMake getModels(String year, String make, String productCd, String stateCd, String formType, String effectiveDate) {
		String url = HelperCommon.urlBuilderAdmin(String.format(DXP_RETRIEVE_MODEL_BY_YEAR_MAKE, year, make, productCd, stateCd, formType, effectiveDate)).replace("/QUESTION_MARK/","?").trim();
		IAdmin.log.info("Hitting {}", url);
		return JsonClient.sendGetRequest(HelperCommon.cutFormType(formType, url), AAAModelByYearMake.class);
	}

	/**
	 * Hitting "/aaa-admin/services/aaa-vehicle-info-rs/v1/vin-info/" to get body styles
	 * @param year
	 * @param make
	 * @param model
	 * @param series
	 * @param productCd
	 * @param stateCd
	 * @param formType
	 * @param effectiveDate
	 * @return
	 */
	public static AAABodyStyleByYearMakeModelSeries getBodyStyle(String year, String make, String model, String series, String productCd, String stateCd, String formType, String effectiveDate) {
		String url = HelperCommon.urlBuilderAdmin(String.format(DXP_RETRIEVE_BODYSTYLE_BY_YEAR_MAKE_MODEL_SERIES, year, make, model, series, productCd, stateCd, formType, effectiveDate)).replace("/QUESTION_MARK/","?").trim();
		IAdmin.log.info("Hitting {}", url);
		return JsonClient.sendGetRequest(HelperCommon.cutFormType(formType, url), AAABodyStyleByYearMakeModelSeries.class);
	}
}
