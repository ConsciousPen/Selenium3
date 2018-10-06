package aaa.modules.regression.service.helper;

import static aaa.admin.modules.IAdmin.log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.http.client.utils.URIBuilder;
import com.exigen.ipb.etcsa.base.app.CSAAApplicationFactory;
import com.exigen.ipb.etcsa.base.app.impl.AdminApplication;
import aaa.common.enums.RestRequestMethodTypes;
import aaa.config.CsaaTestProperties;
import aaa.helpers.rest.JsonClient;
import aaa.helpers.rest.RestBodyRequest;
import aaa.helpers.rest.RestRequestInfo;
import aaa.helpers.rest.dtoAdmin.InstallmentFeesResponse;
import aaa.helpers.rest.dtoAdmin.RfiDocumentResponse;
import aaa.helpers.rest.dtoAdmin.responses.AAABodyStyleByYearMakeModelSeries;
import aaa.helpers.rest.dtoAdmin.responses.AAAMakeByYear;
import aaa.helpers.rest.dtoAdmin.responses.AAAModelByYearMake;
import aaa.helpers.rest.dtoAdmin.responses.AAASeriesByYearMakeModel;
import aaa.helpers.rest.dtoDxp.*;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;

public class HelperCommon {
	private static final String ADMIN_DOCUMENTS_RFI_DOCUMENTS_ENDPOINT = "/aaa-admin/services/aaa-policy-rs/v1/documents/rfi-documents/";
	private static final String ADMIN_UPDATE_POLICY_PREFERENCES_ENDPOINT = "/aaa-admin/services/aaa-policy-rs/v1/endorsements/update-policy-preferences/";
	private static final String ADMIN_INSTALLMENT_FEES_ENDPOINT = "/aaa-admin/services/aaa-billing-rs/v1/fees/installment-fees";

	private static final String DXP_LOOKUPS = "/api/v1/lookups/%s?productCd=%s&riskStateCd=%s";

	private static final String DXP_POLICIES_LOCK_UNLOCK_SERVICES = "/api/v1/policies/%s/lock";
	private static final String DXP_POLICIES_START_ENDORSEMENT_INFO = "/api/v1/policies/%s/start-endorsement-info";
	private static final String DXP_POLICIES_VIN_INFO = "/api/v1/policies/%s/vin-info/%s";

	private static final String DXP_POLICIES_ENDORSEMENT_VEHICLES_METADATA = "/api/v1/policies/%s/endorsement/vehicles/%s/metadata";
	private static final String DXP_POLICIES_ENDORSEMENT_DRIVERS_METADATA = "/api/v1/policies/%s/endorsement/drivers/%s/metadata";
	private static final String DXP_POLICIES_ENDORSEMENT_BIND = "/api/v1/policies/%s/endorsement/bind";
	private static final String DXP_POLICIES_ENDORSEMENT_RATE = "/api/v1/policies/%s/endorsement/rate";

	private static final String DXP_POLICIES_POLICY = "/api/v1/policies/%s";
	private static final String DXP_POLICIES_ENDORSEMENT = "/api/v1/policies/%s/endorsement";
	private static final String DXP_POLICIES_RENEWAL = "/api/v1/policies/%s/renewal";

	private static final String DXP_POLICIES_POLICY_VEHICLES = "/api/v1/policies/%s/vehicles";
	private static final String DXP_POLICIES_ENDORSEMENT_VEHICLES = "/api/v1/policies/%s/endorsement/vehicles";
	private static final String DXP_POLICIES_ENDORSEMENT_VEHICLES_OID = "/api/v1/policies/%s/endorsement/vehicles/%s";

	private static final String DXP_POLICIES_ENDORSEMENT_ASSIGNMENTS = "/api/v1/policies/%s/endorsement/assignments";

	private static final String DXP_POLICIES_ENDORSEMENT_TRANSACTION_INFORMATION = "/api/v1/policies/%s/endorsement/change-log";

	private static final String DXP_POLICIES_POLICY_PREMIUMS = "/api/v1/policies/%s/premiums";
	private static final String DXP_POLICIES_ENDORSEMENT_PREMIUMS = "/api/v1/policies/%s/endorsement/premiums";
	private static final String DXP_POLICIES_RENEWAL_PREMIUMS = "/api/v1/policies/%s/renewal/premiums";

	private static final String DXP_POLICIES_DRIVERS = "/api/v1/policies/%s/drivers";
	private static final String DXP_POLICIES_ENDORSEMENT_DRIVERS = "/api/v1/policies/%s/endorsement/drivers";
	private static final String DXP_POLICIES_UPDATE_DRIVERS = "/api/v1/policies/%s/endorsement/drivers/%s";
	private static final String DXP_POLICIES_ENDORSEMENT_REMOVE_DRIVER = "/api/v1/policies/%s/endorsement/drivers/%s";
	private static final String DXP_POLICIES_ENDORSEMENT_DRIVERS_REPORTS = "/api/v1/policies/%s/endorsement/drivers/%s/reports";
	private static final String DXP_POLICIES_ENDORSEMENT_DRIVERS_CANCEL_REMOVAL = "/api/v1/policies/%s/endorsement/drivers/%s/cancelRemoval";
	private static final String DXP_POLICIES_ENDORSEMENT_VEHICLES_CANCEL_REMOVAL = "/api/v1/policies/%s/endorsement/vehicles/%s/cancelRemoval";

	private static final String DXP_POLICIES_POLICY_COVERAGES = "/api/v1/policies/%s/coverages";
	private static final String DXP_POLICIES_ENDORSEMENT_COVERAGES = "/api/v1/policies/%s/endorsement/coverages";
	private static final String DXP_POLICIES_POLICY_VEHICLE_OID_COVERAGES = "/api/v1/policies/%s/vehicles/%s/coverages";
	private static final String DXP_POLICIES_ENDORSEMENT_UPDATE_COVERAGES = "/api/v1/policies/%s/endorsement/coverages";
	private static final String DXP_POLICIES_ENDORSEMENT_VEHICLE_OID_COVERAGES = "/api/v1/policies/%s/endorsement/vehicles/%s/coverages";

	private static final String AAA_VEHICLE_INFO_RS_PREFIX = "/aaa-admin/services/aaa-vehicle-info-rs/v1/vin-info/";
	private static final String DXP_RETRIEVE_MAKE_BY_YEAR = AAA_VEHICLE_INFO_RS_PREFIX + "make-by-year?year=%s&productCd=%s&stateCd=%s&formType=%s&effectiveDate=%s";
	private static final String DXP_RETRIEVE_MODEL_BY_YEAR_MAKE = AAA_VEHICLE_INFO_RS_PREFIX + "model-by-make-year?year=%s&make=%s&productCd=%s&stateCd=%s&formType=%s&effectiveDate=%s";
	private static final String DXP_SERIES_BY_YEAR_MAKE_MODEL = AAA_VEHICLE_INFO_RS_PREFIX + "series-by-make-year-model?year=%s&make=%s&model=%s&productCd=%s&stateCd=%s&formType=%s&effectiveDate=%s";
	private static final String DXP_RETRIEVE_BODYSTYLE_BY_YEAR_MAKE_MODEL_SERIES =
			AAA_VEHICLE_INFO_RS_PREFIX + "bodystyle-by-make-year-model?year=%s&make=%s&model=%s&Series=%s&productCd=%s&stateCd=%s&formType=%s&effectiveDate=%s";

	private static final String DXP_POLICIES_POLICY_DISCOUNTS = "/api/v1/policies/%s/discounts";
	private static final String DXP_POLICIES_ENDORSEMENT_DISCOUNTS = "/api/v1/policies/%s/endorsement/discounts";

	private static final String DXP_BILLING_CURRENT_BILL = "/api/v1/billing/%s/current-bill";
	private static final String DXP_BILLING_ACCOUNT_INFO = "/api/v1/accounts/%s";
	private static final String DXP_BILLING_INSTALLMENTS_INFO = "/api/v1/accounts/%s/installments";

	/**
	 *  public String url;
	 * 	public String sessionId;
	 * 	public Object bodyRequest;
	 * 	public Class<T> responseType;
	 * 	public int status = Response.Status.OK.getStatusCode();
	 *
	 * @param url
	 * @param bodyRequest
	 * @param responseType
	 * @param status
	 * @param <T>
	 * @return
	 */
	private static <T> RestRequestInfo<T> buildRequest(String url, RestBodyRequest bodyRequest, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = url;
		restRequestInfo.bodyRequest = bodyRequest;
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		return restRequestInfo;
	}

	/**
	 *
	 *  public String url;
	 * 	public Object bodyRequest;
	 * 	public Class<T> responseType;
	 * 	public int status = Response.Status.OK.getStatusCode();
	 *
	 * @param url
	 * @param responseType
	 * @param status
	 * @param <T>
	 * @return
	 */
	private static <T> RestRequestInfo<T> buildRequest(String url, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = url;
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		return restRequestInfo;
	}

	/**
	 *
	 *  public String url;
	 * 	public Object bodyRequest;
	 * 	public Class<T> responseType;
	 * 	public int status = Response.Status.OK.getStatusCode();
	 *
	 * @param url
	 * @param responseType
	 * @param status
	 * @param <T>
	 * @return
	 */
	private static <T> RestRequestInfo<T> buildRequest(String url, Class<T> responseType, String sessionId, int status) {
		RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = url;
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		restRequestInfo.sessionId = sessionId;
		return restRequestInfo;
	}

	public static <T> T sendPostRequest(String url, RestBodyRequest bodyRequest, Class<T> responseType, int status) {
		RestRequestInfo<T> request = buildRequest(url, bodyRequest, responseType, status);
		return JsonClient.sendJsonRequest(request, RestRequestMethodTypes.POST);
	}

	public static <T> T sendPatchRequest(String url, RestBodyRequest bodyRequest, Class<T> responseType) {
		RestRequestInfo<T> restRequestInfo = buildRequest(url,bodyRequest,responseType, Response.Status.OK.getStatusCode());
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.PATCH);
	}

	public static <T> T sendGetRequest(String url, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = buildRequest(url, responseType, status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.GET);
	}

	public static <T> T sendGetRequest(String url, Class<T> responseType) {
		return sendGetRequest(url ,responseType, Response.Status.OK.getStatusCode());
	}

	public static <T> T sendDeleteRequest(String url, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = buildRequest(url, responseType, status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.DELETE);
	}

	public static <T> T sendDeleteRequest(String url, Class<T> responseType, RestBodyRequest request, int status) {
		RestRequestInfo<T> restRequestInfo = buildRequest(url, request, responseType,status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.DELETE);
	}

	private static AdminApplication adminApp() {
		return CSAAApplicationFactory.get().adminApp();
	}

	private static String urlBuilderDxp(String endpointUrlPart) {
		if (Boolean.valueOf(PropertyProvider.getProperty(CsaaTestProperties.SCRUM_ENVS_SSH)).equals(true)) {
			return PropertyProvider.getProperty(CsaaTestProperties.DXP_PROTOCOL) + PropertyProvider.getProperty(CsaaTestProperties.APP_HOST).replace(PropertyProvider.getProperty(CsaaTestProperties.DOMAIN_NAME), "") + PropertyProvider.getProperty(CsaaTestProperties.DXP_PORT) + endpointUrlPart;
		}
		return PropertyProvider.getProperty(CsaaTestProperties.DOMAIN_NAME) + endpointUrlPart;
	}

	private static String urlBuilderAdmin(String endpointUrlPart) {
		return new URIBuilder().setScheme(adminApp().getProtocol()).setHost(adminApp().getHost()).setPort(adminApp().getPort()).setPath(endpointUrlPart).toString();
	}

	public static RfiDocumentResponse[] executeRequestRfi(String policyNumber, String date) {
		String requestUrl = urlBuilderAdmin(ADMIN_DOCUMENTS_RFI_DOCUMENTS_ENDPOINT) + policyNumber + "/" + date;
		return JsonClient.runJsonRequestGetAdmin(requestUrl, RfiDocumentResponse[].class);
	}

	public static InstallmentFeesResponse[] executeInstallmentFeesRequest(String productCode, String state, String date) {
		String requestUrl = urlBuilderAdmin(ADMIN_INSTALLMENT_FEES_ENDPOINT) + "?productCode=" + productCode + "&riskState=" + state + "&effectiveDate=" + date;
		return JsonClient.runJsonRequestGetAdmin(requestUrl, InstallmentFeesResponse[].class);
	}

	public static String updatePolicyPreferences(String policyNumber, int status) {
		String requestUrl = urlBuilderAdmin(ADMIN_UPDATE_POLICY_PREFERENCES_ENDPOINT + policyNumber);
		return JsonClient.runJsonRequestPostAdmin(requestUrl, null, String.class, status);
	}

	public static ValidateEndorsementResponse startEndorsement(String policyNumber, String endorsementDate) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_START_ENDORSEMENT_INFO, policyNumber));
		if (endorsementDate != null) {
			requestUrl = requestUrl + "?endorsementDate=" + endorsementDate;
		}
		return sendGetRequest(requestUrl, ValidateEndorsementResponse.class);
	}

	public static ValidateEndorsementResponse startEndorsement(String policyNumber, String endorsementDate, String sessionId) {
		RestRequestInfo<ValidateEndorsementResponse> restRequestInfo = buildRequest(urlBuilderDxp(String.format(DXP_POLICIES_START_ENDORSEMENT_INFO, policyNumber)), ValidateEndorsementResponse.class, Response.Status.OK.getStatusCode());
		restRequestInfo.sessionId = sessionId;
		if (endorsementDate != null) {
			restRequestInfo.url = restRequestInfo.url + "?endorsementDate=" + endorsementDate;
		}
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.GET);
	}

	public static ErrorResponseDto startEndorsementError(String policyNumber, String endorsementDate, int status) {
		RestRequestInfo<ErrorResponseDto> restRequestInfo = buildRequest(urlBuilderDxp(String.format(DXP_POLICIES_START_ENDORSEMENT_INFO, policyNumber)), ErrorResponseDto.class, status);
		if (endorsementDate != null) {
			restRequestInfo.url = restRequestInfo.url + "?endorsementDate=" + endorsementDate;
		}
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.GET);
	}

	public static VehicleUpdateResponseDto updateVehicle(String policyNumber, String oid, VehicleUpdateDto request) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES_OID, policyNumber, oid));
		return sendPatchRequest(requestUrl, request, VehicleUpdateResponseDto.class);
	}

	public static <T> T replaceVehicle(String policyNumber, String oid, ReplaceVehicleRequest request, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = buildRequest(urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES_OID, policyNumber, oid)), request, responseType, status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.PUT);
	}

	public static <T> T deleteVehicle(String policyNumber, String oid, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = buildRequest(urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES_OID, policyNumber, oid)), null, responseType, status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.DELETE);
	}

	public static <T> T revertVehicle(String policyNumber, String vehicleOid, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = buildRequest(urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES_CANCEL_REMOVAL, policyNumber, vehicleOid)), null, responseType, status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.POST);
	}

	public static <T> T addVehicle(String policyNumber, Vehicle request, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo =
				buildRequest(urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES, policyNumber)), request, responseType, status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.POST);
	}

	public static <T> T addDriver(String policyNumber, AddDriverRequest request, Class<T> responseType) {
		return addDriver(policyNumber, request, responseType, 201);
	}

	public static <T> T addDriver(String policyNumber, AddDriverRequest request, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo =
				buildRequest(urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_DRIVERS, policyNumber)), request, responseType,status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.POST);
	}

	public static <T> T viewPolicyCoverages(String policyNumber, Class<T> responseType) {
		return viewPolicyCoverages(policyNumber, responseType, Response.Status.OK.getStatusCode());
	}

	public static <T> T viewPolicyCoverages(String policyNumber, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo =
				buildRequest(urlBuilderDxp(String.format(DXP_POLICIES_POLICY_COVERAGES, policyNumber)), responseType, status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.GET);
	}

	public static AAAVehicleVinInfoRestResponseWrapper executeVinInfo(String policyNumber, String vin, String endorsementDate) {
		log.info("VIN info params: policyNumber: " + policyNumber + ", vin: " + vin + ", endorsementDate: " + endorsementDate);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_VIN_INFO, policyNumber, vin));
		if (endorsementDate != null) {
			requestUrl = requestUrl + "?endorsementDate=" + endorsementDate;
		}
		return sendGetRequest(requestUrl, AAAVehicleVinInfoRestResponseWrapper.class);
	}

	public static AttributeMetadata[] viewEndorsementVehiclesMetaData(String policyNumber, String oid) {
		log.info("Vehicle MetaData params: policyNumber: " + policyNumber + ", oid: " + oid);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES_METADATA, policyNumber, oid));
		return sendGetRequest(requestUrl, AttributeMetadata[].class);
	}

	public static AttributeMetadata[] viewEndorsementDriversMetaData(String policyNumber, String oid) {
		log.info("Driver MetaData params: policyNumber: " + policyNumber + ", oid: " + oid);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_DRIVERS_METADATA, policyNumber, oid));
		return sendGetRequest(requestUrl, AttributeMetadata[].class);
	}

	public static PolicyLockUnlockDto executePolicyLockService(String policyNumber, int status, String sessionId) {
		RestRequestInfo<PolicyLockUnlockDto> restRequestInfo =
				buildRequest(urlBuilderDxp(String.format(DXP_POLICIES_LOCK_UNLOCK_SERVICES, policyNumber)),PolicyLockUnlockDto.class,sessionId, status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.POST);
	}

	public static PolicyLockUnlockDto executePolicyUnlockService(String policyNumber, int status, String sessionId) {
		RestRequestInfo<PolicyLockUnlockDto> restRequestInfo =
				buildRequest(urlBuilderDxp(String.format(DXP_POLICIES_LOCK_UNLOCK_SERVICES, policyNumber)),PolicyLockUnlockDto.class,sessionId, status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.DELETE);
	}

	public static ViewVehicleResponse viewPolicyVehicles(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_POLICY_VEHICLES, policyNumber));
		return sendGetRequest(requestUrl, ViewVehicleResponse.class);
	}

	public static ViewVehicleResponse viewEndorsementVehicles(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES, policyNumber));
		return sendGetRequest(requestUrl, ViewVehicleResponse.class);
	}

	public static ErrorResponseDto viewEndorsementAssignmentsError(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_ASSIGNMENTS, policyNumber));
		return sendGetRequest(requestUrl, ErrorResponseDto.class, status);
	}

	public static DriverWithRuleSets updateDriver(String policyNumber, String oid, UpdateDriverRequest request) {
		log.info("Update Driver params: policyNumber: " + policyNumber + ", oid: " + oid);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_UPDATE_DRIVERS, policyNumber, oid));
		return sendPatchRequest(requestUrl, request, DriverWithRuleSets.class);
	}

	public static DriversDto removeDriver(String policyNumber, String oid, RemoveDriverRequest request) {
		log.info("Remove Driver params: policyNumber: " + policyNumber + ", oid: " + oid);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_REMOVE_DRIVER, policyNumber, oid));
		return sendDeleteRequest(requestUrl, DriversDto.class, request, 200);
	}

	public static ErrorResponseDto removeDriverError(String policyNumber, String oid, RemoveDriverRequest request) {
		log.info("Remove Driver params: policyNumber: " + policyNumber + ", oid: " + oid);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_REMOVE_DRIVER, policyNumber, oid));
		return sendDeleteRequest(requestUrl, ErrorResponseDto.class, request, 422);
	}

	public static ViewDriverAssignmentResponse viewEndorsementAssignments(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_ASSIGNMENTS, policyNumber));
		return sendGetRequest(requestUrl, ViewDriverAssignmentResponse.class);
	}

	public static <T> T revertDriver(String policyNumber, String driverOid, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo =
				buildRequest(urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_DRIVERS_CANCEL_REMOVAL, policyNumber, driverOid)), responseType, status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.POST);
	}

	public static ViewDriverAssignmentResponse updateDriverAssignment(String policyNumber, String vehicleOid, List<String> driverOids) {
		log.info("Update Driver Assignment: policyNumber: " + policyNumber + ", vehicleOid: " + vehicleOid + ", driverOids: " + driverOids);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_ASSIGNMENTS, policyNumber));
		UpdateDriverAssignmentRequest request = new UpdateDriverAssignmentRequest();
		request.assignmentRequests = new ArrayList<>();
		DriverAssignmentRequest assignmentDto = new DriverAssignmentRequest();
		assignmentDto.driverOids = driverOids;
		assignmentDto.vehicleOid = vehicleOid;
		request.assignmentRequests.add(assignmentDto);
		return sendPostRequest(requestUrl, request, ViewDriverAssignmentResponse.class, 200);
	}

	public static ViewDriversResponse viewPolicyDrivers(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_DRIVERS, policyNumber));
		return sendGetRequest(requestUrl, ViewDriversResponse.class);
	}

	public static ViewDriversResponse viewEndorsementDrivers(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_DRIVERS, policyNumber));
		return sendGetRequest(requestUrl, ViewDriversResponse.class);
	}

	public static <T> T orderReports(String policyNumber, String driverOid, Class<T> responseType, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_DRIVERS_REPORTS, policyNumber, driverOid));
		OrderReportsRequest request = new OrderReportsRequest();
		request.policyNumber = policyNumber;
		request.driverOid = driverOid;
		return sendPostRequest(requestUrl, request, responseType, status);
	}

	public static <T> T viewPolicyCoveragesByVehicle(String policyNumber, String oid, Class<T> responseType) {
		return viewPolicyCoveragesByVehicle(policyNumber, oid, responseType, Response.Status.OK.getStatusCode());
	}

	public static <T> T viewPolicyCoveragesByVehicle(String policyNumber, String oid, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo =
				buildRequest(urlBuilderDxp(String.format(DXP_POLICIES_POLICY_VEHICLE_OID_COVERAGES, policyNumber, oid)), responseType, status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.GET);
	}

	public static <T> T viewEndorsementCoverages(String policyNumber, Class<T> responseType) {
		return viewEndorsementCoverages(policyNumber,responseType, Response.Status.OK.getStatusCode());
	}

	public static <T> T viewEndorsementCoverages(String policyNumber, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo =
				buildRequest(urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_COVERAGES, policyNumber)), responseType, status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.GET);
	}

	public static <T> T viewEndorsementCoveragesByVehicle(String policyNumber, String vehicleOid, Class<T> responseType) {
		return viewEndorsementCoveragesByVehicle(policyNumber, vehicleOid, responseType, Response.Status.OK.getStatusCode());
	}

	public static <T> T viewEndorsementCoveragesByVehicle(String policyNumber, String vehicleOid, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo =
				buildRequest(urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLE_OID_COVERAGES, policyNumber, vehicleOid)), responseType, status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.GET);
	}

	public static <T> T updateEndorsementCoveragesByVehicle(String policyNumber, String vehicleOid, UpdateCoverageRequest request, Class<T> responseType) {
		return updateEndorsementCoveragesByVehicle(policyNumber, vehicleOid, request, responseType, Response.Status.OK.getStatusCode());
	}

	public static <T> T updateEndorsementCoveragesByVehicle(String policyNumber, String vehicleOid, UpdateCoverageRequest request, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo =
				buildRequest(urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLE_OID_COVERAGES, policyNumber, vehicleOid)), request,responseType, status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.PATCH);
	}

	public static <T> T updateEndorsementCoverage(String policyNumber, UpdateCoverageRequest request, Class<T> responseType) {
		return updateEndorsementCoverage(policyNumber, request, responseType, Response.Status.OK.getStatusCode());
	}

	public static <T> T updateEndorsementCoverage(String policyNumber, UpdateCoverageRequest request, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo =
				buildRequest(urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_UPDATE_COVERAGES, policyNumber)), request, responseType, status);
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.PATCH);
	}

	public static PolicyPremiumInfo[] viewPolicyPremiums(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_POLICY_PREMIUMS, policyNumber));
		return sendGetRequest(requestUrl, PolicyPremiumInfo[].class);
	}

	public static PolicySummary viewPendingEndorsementImageInfo(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT, policyNumber));
		return sendGetRequest(requestUrl, PolicySummary.class);
	}

	public static PolicyPremiumInfo[] viewEndorsementPremiums(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_PREMIUMS, policyNumber));
		return sendGetRequest(requestUrl, PolicyPremiumInfo[].class);
	}

	public static PolicyPremiumInfo[] viewRenewalPremiums(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_RENEWAL_PREMIUMS, policyNumber));
		return sendGetRequest(requestUrl, PolicyPremiumInfo[].class);
	}

	public static ErrorResponseDto viewRenewalPremiumsError(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_RENEWAL_PREMIUMS, policyNumber));
		return sendGetRequest(requestUrl, ErrorResponseDto.class, status);
	}

	public static ErrorResponseDto viewEndorsementPremiumsError(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_PREMIUMS, policyNumber));
		return sendGetRequest(requestUrl, ErrorResponseDto.class, status);
	}

	static ComparablePolicy viewEndorsementChangeLog(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_TRANSACTION_INFORMATION, policyNumber));
		return sendGetRequest(requestUrl, ComparablePolicy.class);
	}

	public static PolicySummary createEndorsement(String policyNumber, String endorsementDate) {
		AAAEndorseRequest request = new AAAEndorseRequest();
		request.endorsementDate = endorsementDate;
		request.endorsementReason = "OTHPB";
		request.endorsementReasonOther = "Some reason why endorsement was done";
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT, policyNumber));
		if (endorsementDate != null) {
			requestUrl = requestUrl + "?endorsementDate=" + endorsementDate;
		}
		return sendPostRequest(requestUrl, request, PolicySummary.class, Response.Status.CREATED.getStatusCode());
	}

	@SuppressWarnings("unchecked")
	public static PolicySummary viewPolicyRenewalSummary(String policyNumber, String term, int code) {
		String endPoint;
		if ("policy".equals(term)) {
			endPoint = DXP_POLICIES_POLICY;
		} else {
			endPoint = DXP_POLICIES_RENEWAL;
		}
		String requestUrl = urlBuilderDxp(String.format(endPoint, policyNumber));
		return sendGetRequest(requestUrl, PolicySummary.class, code);
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, String> executeLookupValidate(String lookupName, String productCd, String riskStateCd, String effectiveDate) {
		String requestUrl = urlBuilderDxp(String.format(DXP_LOOKUPS, lookupName, productCd, riskStateCd));
		if (effectiveDate != null) {
			requestUrl = requestUrl + "&effectiveDate=" + effectiveDate;
		}
		return sendGetRequest(requestUrl, HashMap.class);
	}

	public static PolicyPremiumInfo[] endorsementRate(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_RATE, policyNumber));
		return sendPostRequest(requestUrl, null, PolicyPremiumInfo[].class, status);
	}

	public static ErrorResponseDto endorsementRateError(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_RATE, policyNumber));
		return sendPostRequest(requestUrl, null, ErrorResponseDto.class, 422);
	}

	public static PolicySummary endorsementBind(String policyNumber, String authorizedBy, int status) {
		AAABindEndorsementRequestDTO request = new AAABindEndorsementRequestDTO();
		request.authorizedBy = authorizedBy;
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_BIND, policyNumber));
		return sendPostRequest(requestUrl, request, PolicySummary.class, status);
	}

	public static String deleteEndorsement(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT, policyNumber));
		return sendDeleteRequest(requestUrl, String.class, status);
	}

	public static ErrorResponseDto endorsementBindError(String policyNumber, String authorizedBy, int status) {
		AAABindEndorsementRequestDTO request = new AAABindEndorsementRequestDTO();
		request.authorizedBy = authorizedBy;
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_BIND, policyNumber));
		return sendPostRequest(requestUrl, request, ErrorResponseDto.class, status);
	}

	public static DiscountSummary viewDiscounts(String policyNumber, String transaction, int status) {
		String requestUrl;
		if ("policy".equals(transaction)) {
			requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_POLICY_DISCOUNTS, policyNumber));
		} else if ("endorsement".equals(transaction)) {

			requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_DISCOUNTS, policyNumber));
		} else {
			throw new IstfException("invalid transaction type for DiscountSummary service");
		}
		return sendGetRequest(requestUrl, DiscountSummary.class, status);
	}

	public static AccountDetails billingAccountInfoService(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_BILLING_ACCOUNT_INFO, policyNumber));
		return sendGetRequest(requestUrl, AccountDetails.class);
	}

	public static Installment[] billingInstallmentsInfo(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_BILLING_INSTALLMENTS_INFO, policyNumber));
		return sendGetRequest(requestUrl, Installment[].class);
	}



	public static AAAMakeByYear getMakes(String year, String productCd, String stateCd, String formType, String effectiveDate) {
		String url = urlBuilderAdmin(String.format(DXP_RETRIEVE_MAKE_BY_YEAR, year, productCd, stateCd, formType, effectiveDate));

		return JsonClient.runJsonRequestGetAdmin(cutFormType(formType, url), AAAMakeByYear.class);
	}

	public static String cutFormType(String formType, String url) {
		if (formType.isEmpty() || formType == null) {
			url = url.replaceAll("&formType=", "");
		}
		return url;
	}

	public static AAAModelByYearMake getModels(String year, String make, String productCd, String stateCd, String formType, String effectiveDate) {
		String url = urlBuilderAdmin(String.format(DXP_RETRIEVE_MODEL_BY_YEAR_MAKE, year, make, productCd, stateCd, formType, effectiveDate));

		return JsonClient.runJsonRequestGetAdmin(cutFormType(formType, url), AAAModelByYearMake.class);
	}

	/**
	 * @param formType can be null or empty
	 */
	public static AAABodyStyleByYearMakeModelSeries getBodyStyle(String year, String make, String model, String series, String productCd, String stateCd, String formType, String effectiveDate) {
		String url = urlBuilderAdmin(String.format(DXP_RETRIEVE_BODYSTYLE_BY_YEAR_MAKE_MODEL_SERIES, year, make, model, series, productCd, stateCd, formType, effectiveDate));

		return JsonClient.runJsonRequestGetAdmin(cutFormType(formType, url), AAABodyStyleByYearMakeModelSeries.class);
	}

	public static AAASeriesByYearMakeModel getSeries(String year, String make, String model, String productCd, String stateCd, String formType, String effectiveDate) {
		String url = urlBuilderAdmin(String.format(DXP_SERIES_BY_YEAR_MAKE_MODEL, year, make, model, productCd, stateCd, formType, effectiveDate));

		return JsonClient.runJsonRequestGetAdmin(url, AAASeriesByYearMakeModel.class);
	}

}