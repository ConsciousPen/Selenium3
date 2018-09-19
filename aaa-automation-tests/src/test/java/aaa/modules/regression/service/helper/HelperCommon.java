package aaa.modules.regression.service.helper;

import static aaa.admin.modules.IAdmin.log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.xerces.impl.dv.util.Base64;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import com.exigen.ipb.etcsa.base.app.CSAAApplicationFactory;
import com.exigen.ipb.etcsa.base.app.impl.AdminApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jna.platform.win32.Guid;
import aaa.config.CsaaTestProperties;
import aaa.modules.regression.service.helper.dtoAdmin.InstallmentFeesResponse;
import aaa.modules.regression.service.helper.dtoAdmin.RfiDocumentResponse;
import aaa.modules.regression.service.helper.dtoAdmin.responses.AAABodyStyleByYearMakeModelSeries;
import aaa.modules.regression.service.helper.dtoAdmin.responses.AAAMakeByYear;
import aaa.modules.regression.service.helper.dtoAdmin.responses.AAAModelByYearMake;
import aaa.modules.regression.service.helper.dtoAdmin.responses.AAASeriesByYearMakeModel;
import aaa.modules.regression.service.helper.dtoClaim.ClaimsAssignmentResponse;
import aaa.modules.regression.service.helper.dtoDxp.*;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;

public class HelperCommon {
	private static final String ADMIN_DOCUMENTS_RFI_DOCUMENTS_ENDPOINT = "/aaa-admin/services/aaa-policy-rs/v1/documents/rfi-documents/";
	private static final String ADMIN_UPDATE_POLICY_PREFERENCES_ENDPOINT = "/aaa-admin/services/aaa-policy-rs/v1/endorsements/update-policy-preferences/";
	private static final String ADMIN_INSTALLMENT_FEES_ENDPOINT = "/aaa-admin/services/aaa-billing-rs/v1/fees/installment-fees";

	private static final String APPLICATION_CONTEXT_HEADER = "X-ApplicationContext";
	private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();
	private static final ObjectMapper PRETTY_PRINT_OBJECT_MAPPER = new ObjectMapper();

	private static final String DXP_LOOKUPS = "/api/v1/lookups/%s?productCd=%s&riskStateCd=%s";

	private static final String DXP_POLICIES_LOCK_UNLOCK_SERVICES = "/api/v1/policies/%s/lock";
	private static final String DXP_POLICIES_CONTACT_INFO = "/api/v1/policies/%s/contact-info";
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

	static {
		PRETTY_PRINT_OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
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
		return runJsonRequestGetAdmin(requestUrl, RfiDocumentResponse[].class);
	}

	public static InstallmentFeesResponse[] executeInstallmentFeesRequest(String productCode, String state, String date) {
		String requestUrl = urlBuilderAdmin(ADMIN_INSTALLMENT_FEES_ENDPOINT) + "?productCode=" + productCode + "&riskState=" + state + "&effectiveDate=" + date;
		return runJsonRequestGetAdmin(requestUrl, InstallmentFeesResponse[].class);
	}

	public static String updatePolicyPreferences(String policyNumber, int status) {
		String requestUrl = urlBuilderAdmin(ADMIN_UPDATE_POLICY_PREFERENCES_ENDPOINT + policyNumber);
		return runJsonRequestPostAdmin(requestUrl, null, String.class, status);
	}

	public static ValidateEndorsementResponse startEndorsement(String policyNumber, String endorsementDate) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_START_ENDORSEMENT_INFO, policyNumber));
		if (endorsementDate != null) {
			requestUrl = requestUrl + "?endorsementDate=" + endorsementDate;
		}
		return runJsonRequestGetDxp(requestUrl, ValidateEndorsementResponse.class);
	}

	public static ValidateEndorsementResponse startEndorsement(String policyNumber, String endorsementDate, String sessionId) {
		RestRequestInfo<ValidateEndorsementResponse> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.responseType = ValidateEndorsementResponse.class;
		restRequestInfo.sessionId = sessionId;
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_POLICIES_START_ENDORSEMENT_INFO, policyNumber));
		if (endorsementDate != null) {
			restRequestInfo.url = restRequestInfo.url + "?endorsementDate=" + endorsementDate;
		}
		return runJsonRequestGetDxp(restRequestInfo);
	}

	public static ErrorResponseDto startEndorsementError(String policyNumber, String endorsementDate, int status) {
		RestRequestInfo<ErrorResponseDto> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_POLICIES_START_ENDORSEMENT_INFO, policyNumber));
		restRequestInfo.status = status;
		restRequestInfo.responseType = ErrorResponseDto.class;
		if (endorsementDate != null) {
			restRequestInfo.url = restRequestInfo.url + "?endorsementDate=" + endorsementDate;
		}
		return runJsonRequestGetDxp(restRequestInfo);
	}

	public static VehicleUpdateResponseDto updateVehicle(String policyNumber, String oid, VehicleUpdateDto request) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES_OID, policyNumber, oid));
		return runJsonRequestPatchDxp(requestUrl, request, VehicleUpdateResponseDto.class);
	}

	public static <T> T replaceVehicle(String policyNumber, String oid, ReplaceVehicleRequest request, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES_OID, policyNumber, oid));
		restRequestInfo.bodyRequest = request;
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		return runJsonRequestMethodDxp(restRequestInfo, RequestMethod.PUT);
	}

	/**
	 * @deprecated use {@link #replaceVehicle(String, String, ReplaceVehicleRequest, Class, int)}
	 */
	@Deprecated
	public static VehicleUpdateResponseDto replaceVehicle(String policyNumber, String oid, ReplaceVehicleRequest request) {
		log.info("Replace vehicle params: policyNumber: " + policyNumber + ", oid: " + oid);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES_OID, policyNumber, oid));
		return runJsonRequestPutDxp(requestUrl, request, VehicleUpdateResponseDto.class);
	}

	public static VehicleUpdateResponseDto deleteVehicle(String policyNumber, String oid) {
		log.info("Delete vehicle params: policyNumber: " + policyNumber + ", oid: " + oid);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES_OID, policyNumber, oid));
		return runJsonRequestDeleteDxp(requestUrl, VehicleUpdateResponseDto.class);
	}

	public static AAAVehicleVinInfoRestResponseWrapper executeVinInfo(String policyNumber, String vin, String endorsementDate) {
		log.info("VIN info params: policyNumber: " + policyNumber + ", vin: " + vin + ", endorsementDate: " + endorsementDate);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_VIN_INFO, policyNumber, vin));
		if (endorsementDate != null) {
			requestUrl = requestUrl + "?endorsementDate=" + endorsementDate;
		}
		return runJsonRequestGetDxp(requestUrl, AAAVehicleVinInfoRestResponseWrapper.class);
	}

	public static AttributeMetadata[] viewEndoresmentVehiclesMetaData(String policyNumber, String oid) {
		log.info("Vehicle MetaData params: policyNumber: " + policyNumber + ", oid: " + oid);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES_METADATA, policyNumber, oid));
		return runJsonRequestGetDxp(requestUrl, AttributeMetadata[].class);
	}

	public static AttributeMetadata[] viewEndorsementDriversMetaData(String policyNumber, String oid) {
		log.info("Driver MetaData params: policyNumber: " + policyNumber + ", oid: " + oid);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_DRIVERS_METADATA, policyNumber, oid));
		return runJsonRequestGetDxp(requestUrl, AttributeMetadata[].class);
	}

	public static PolicyLockUnlockDto executePolicyLockService(String policyNumber, int status, String sessionId) {
		RestRequestInfo<PolicyLockUnlockDto> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_POLICIES_LOCK_UNLOCK_SERVICES, policyNumber));
		restRequestInfo.responseType = PolicyLockUnlockDto.class;
		restRequestInfo.status = status;
		restRequestInfo.sessionId = sessionId;
        return runJsonRequestMethodDxp(restRequestInfo, RequestMethod.POST);
    }

	public static PolicyLockUnlockDto executePolicyUnlockService(String policyNumber, int status, String sessionId) {
		RestRequestInfo<PolicyLockUnlockDto> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_POLICIES_LOCK_UNLOCK_SERVICES, policyNumber));
		restRequestInfo.responseType = PolicyLockUnlockDto.class;
		restRequestInfo.status = status;
		restRequestInfo.sessionId = sessionId;
		return runJsonRequestMethodDxp(restRequestInfo, RequestMethod.DELETE);
	}

	public static ViewVehicleResponse viewPolicyVehicles(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_POLICY_VEHICLES, policyNumber));
		return runJsonRequestGetDxp(requestUrl, ViewVehicleResponse.class);
	}

	public static ViewVehicleResponse viewEndorsementVehicles(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES, policyNumber));
		return runJsonRequestGetDxp(requestUrl, ViewVehicleResponse.class);
	}

	public static <T> T addVehicle(String policyNumber, Vehicle request, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES, policyNumber));
		restRequestInfo.bodyRequest = request;
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		return runJsonRequestMethodDxp(restRequestInfo, RequestMethod.POST);
	}

	/**
	 * @deprecated use {@link #addVehicle(String, Vehicle, Class, int)}
	 */
	@Deprecated
	public static Vehicle executeEndorsementAddVehicle(String policyNumber, String purchaseDate, String vin) {
		log.info("Add Vehicles params: policyNumber: " + policyNumber + ", purchaseDate: " + purchaseDate + ", vin: " + vin);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES, policyNumber));
		Vehicle request = new Vehicle();
		request.purchaseDate = purchaseDate;
		request.vehIdentificationNo = vin;
		return runJsonRequestPostDxp(requestUrl, request, Vehicle.class, 201);
	}

	/**
	 * @deprecated use {@link #addVehicle(String, Vehicle, Class, int)}
	 */
	@Deprecated
	public static ErrorResponseDto viewAddVehicleServiceErrors(String policyNumber, String purchaseDate, String vin) {
		log.info("Add Vehicles params: policyNumber: " + policyNumber + ", purchaseDate: " + purchaseDate + ", vin: " + vin);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES, policyNumber));
		Vehicle request = new Vehicle();
		request.purchaseDate = purchaseDate;
		request.vehIdentificationNo = vin;
		return runJsonRequestPostDxp(requestUrl, request, ErrorResponseDto.class, 422);
	}

	/**
	 * @deprecated use {@link #addVehicle(String, Vehicle, Class, int)}
	 */
	@Deprecated
	public static Vehicle executeEndorsementAddVehicle(String policyNumber, Vehicle request) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLES, policyNumber));
		return runJsonRequestPostDxp(requestUrl, request, Vehicle.class, 201);
	}

	public static ErrorResponseDto viewEndorsementAssignmentsError(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_ASSIGNMENTS, policyNumber));
		return runJsonRequestGetDxp(requestUrl, ErrorResponseDto.class, status);
	}

	public static <T> T addDriver(String policyNumber, AddDriverRequest request, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_DRIVERS, policyNumber));
		restRequestInfo.bodyRequest = request;
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		return runJsonRequestMethodDxp(restRequestInfo, RequestMethod.POST);
	}

	/**
	 * @deprecated use {@link #addDriver(String, AddDriverRequest, Class, int)}
	 */
	@Deprecated
	public static DriversDto executeEndorsementAddDriver(String policyNumber, AddDriverRequest request) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_DRIVERS, policyNumber));
		return runJsonRequestPostDxp(requestUrl, request, DriversDto.class, 201);
	}

	/**
	 * @deprecated use {@link #addDriver(String, AddDriverRequest, Class, int)}
	 */
	@Deprecated
	public static ErrorResponseDto executeEndorsementAddDriverError(String policyNumber, AddDriverRequest request) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_DRIVERS, policyNumber));
		return runJsonRequestPostDxp(requestUrl, request, ErrorResponseDto.class, 422);
	}

	public static DriverWithRuleSets updateDriver(String policyNumber, String oid, UpdateDriverRequest request) {
		log.info("Update Driver params: policyNumber: " + policyNumber + ", oid: " + oid);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_UPDATE_DRIVERS, policyNumber, oid));
		return runJsonRequestPatchDxp(requestUrl, request, DriverWithRuleSets.class);
	}

	public static DriversDto removeDriver(String policyNumber, String oid, RemoveDriverRequest request) {
		log.info("Remove Driver params: policyNumber: " + policyNumber + ", oid: " + oid);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_REMOVE_DRIVER, policyNumber, oid));
		return runJsonRequestDeleteDxp(requestUrl, DriversDto.class, request, 200);
	}

	public static ErrorResponseDto removeDriverError(String policyNumber, String oid, RemoveDriverRequest request) {
		log.info("Remove Driver params: policyNumber: " + policyNumber + ", oid: " + oid);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_REMOVE_DRIVER, policyNumber, oid));
		return runJsonRequestDeleteDxp(requestUrl, ErrorResponseDto.class, request, 422);
	}

	public static ViewDriverAssignmentResponse viewEndorsementAssignments(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_ASSIGNMENTS, policyNumber));
		return runJsonRequestGetDxp(requestUrl, ViewDriverAssignmentResponse.class);
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
		return runJsonRequestPostDxp(requestUrl, request, ViewDriverAssignmentResponse.class, 200);
	}

	public static ViewDriversResponse viewPolicyDrivers(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_DRIVERS, policyNumber));
		return runJsonRequestGetDxp(requestUrl, ViewDriversResponse.class);
	}

	public static ViewDriversResponse viewEndorsementDrivers(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_DRIVERS, policyNumber));
		return runJsonRequestGetDxp(requestUrl, ViewDriversResponse.class);
	}

	public static <T> T orderReports(String policyNumber, String driverOid, Class<T> responseType, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_DRIVERS_REPORTS, policyNumber, driverOid));
		OrderReportsRequest request = new OrderReportsRequest();
		request.policyNumber = policyNumber;
		request.driverOid = driverOid;
		return runJsonRequestPostDxp(requestUrl, request, responseType, status);
	}

	public static <T> T viewPolicyCoverages(String policyNumber, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_POLICIES_POLICY_COVERAGES, policyNumber));
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		return runJsonRequestMethodDxp(restRequestInfo, RequestMethod.GET);
	}

	/**
	 * @deprecated use {@link #viewPolicyCoverages(String, Class, int)}
	 */
	@Deprecated
	static PolicyCoverageInfo viewPolicyCoverages(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_POLICY_COVERAGES, policyNumber));
		return runJsonRequestGetDxp(requestUrl, PolicyCoverageInfo.class);
	}

	public static <T> T viewPolicyCoveragesByVehicle(String policyNumber, String oid, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_POLICIES_POLICY_VEHICLE_OID_COVERAGES, policyNumber, oid));
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		return runJsonRequestMethodDxp(restRequestInfo, RequestMethod.GET);
	}

	/**
	 * @deprecated use {@link #viewPolicyCoveragesByVehicle(String, String, Class, int)}
	 */
	@Deprecated
	static PolicyCoverageInfo viewPolicyCoveragesByVehicle(String policyNumber, String oid) {
		log.info("Update policy coverages by vehicle: policyNumber: " + policyNumber + ", oid: " + oid);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_POLICY_VEHICLE_OID_COVERAGES, policyNumber, oid));
		return runJsonRequestGetDxp(requestUrl, PolicyCoverageInfo.class);
	}

	public static <T> T viewEndorsementCoverages(String policyNumber, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_COVERAGES, policyNumber));
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		return runJsonRequestMethodDxp(restRequestInfo, RequestMethod.GET);
	}

	/**
	 * @deprecated use {@link #viewEndorsementCoverages(String, Class, int)}
	 */
	@Deprecated
	static PolicyCoverageInfo viewEndorsementCoverages(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_COVERAGES, policyNumber));
		return runJsonRequestGetDxp(requestUrl, PolicyCoverageInfo.class);
	}

	public static <T> T viewEndorsementCoveragesByVehicle(String policyNumber, String vehicleOid, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLE_OID_COVERAGES, policyNumber, vehicleOid));
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		return runJsonRequestMethodDxp(restRequestInfo, RequestMethod.GET);
	}

	/**
	 * @deprecated use {@link #viewEndorsementCoveragesByVehicle(String, String, Class, int)}
	 */
	@Deprecated
	static PolicyCoverageInfo viewEndorsementCoveragesByVehicle(String policyNumber, String newVehicleOid) {
		log.info("Update endorsement coverages by vehicle: policyNumber: " + policyNumber + ", newVehicleOid: " + newVehicleOid);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLE_OID_COVERAGES, policyNumber, newVehicleOid));
		return runJsonRequestGetDxp(requestUrl, PolicyCoverageInfo.class);
	}

	public static <T> T updateEndorsementCoveragesByVehicle(String policyNumber, String vehicleOid, UpdateCoverageRequest request, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLE_OID_COVERAGES, policyNumber, vehicleOid));
		restRequestInfo.bodyRequest = request;
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		return runJsonRequestMethodDxp(restRequestInfo, RequestMethod.PATCH);
	}

	/**
	 * @deprecated use {@link #updateEndorsementCoveragesByVehicle(String, String, UpdateCoverageRequest, Class, int)}
	 */
	@Deprecated
	static PolicyCoverageInfo updateEndorsementCoveragesByVehicle(String policyNumber, String vehicleOid, String coverageCode, String availableLimits) {
		log.info("Update coverage by vehicle: policyNumber: " + policyNumber + ", vehicleOid: " + vehicleOid + ", coverageCode: " + coverageCode + ", availableLimits: " + availableLimits);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_VEHICLE_OID_COVERAGES, policyNumber, vehicleOid));
		UpdateCoverageRequest request = new UpdateCoverageRequest();
		request.coverageCd = coverageCode;
		request.limit = availableLimits;
		return runJsonRequestPatchDxp(requestUrl, request, PolicyCoverageInfo.class);
	}

	public static <T> T updateEndorsementCoverage(String policyNumber, UpdateCoverageRequest request, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_UPDATE_COVERAGES, policyNumber));
		restRequestInfo.bodyRequest = request;
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		return runJsonRequestMethodDxp(restRequestInfo, RequestMethod.PATCH);
	}

	/**
	 * @deprecated use {@link #updateEndorsementCoverage(String, UpdateCoverageRequest, Class, int)}
	 */
	@Deprecated
	static PolicyCoverageInfo updatePolicyLevelCoverageEndorsement(String policyNumber, String coverageCode, String availableLimits) {
		log.info("Update policy evel coverage: policyNumber: " + policyNumber + ", coverageCode: " + coverageCode + ", availableLimits: " + availableLimits);
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_UPDATE_COVERAGES, policyNumber));
		UpdateCoverageRequest request = new UpdateCoverageRequest();
		request.coverageCd = coverageCode;
		request.limit = availableLimits;
		return runJsonRequestPatchDxp(requestUrl, request, PolicyCoverageInfo.class);
	}

	public static PolicyPremiumInfo[] viewPolicyPremiums(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_POLICY_PREMIUMS, policyNumber));
		return runJsonRequestGetDxp(requestUrl, PolicyPremiumInfo[].class);
	}

	public static PolicySummary viewPendingEndorsementImageInfo(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT, policyNumber));
		return runJsonRequestGetDxp(requestUrl, PolicySummary.class);
	}

	public static PolicyPremiumInfo[] viewEndorsementPremiums(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_PREMIUMS, policyNumber));
		return runJsonRequestGetDxp(requestUrl, PolicyPremiumInfo[].class);
	}

	public static PolicyPremiumInfo[] viewRenewalPremiums(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_RENEWAL_PREMIUMS, policyNumber));
		return runJsonRequestGetDxp(requestUrl, PolicyPremiumInfo[].class);
	}

	public static ErrorResponseDto viewRenewalPremiumsError(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_RENEWAL_PREMIUMS, policyNumber));
		return runJsonRequestGetDxp(requestUrl, ErrorResponseDto.class, status);
	}

	public static ErrorResponseDto viewEndorsementPremiumsError(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_PREMIUMS, policyNumber));
		return runJsonRequestGetDxp(requestUrl, ErrorResponseDto.class, status);
	}

	static ComparablePolicy viewEndorsementChangeLog(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_TRANSACTION_INFORMATION, policyNumber));
		return runJsonRequestGetDxp(requestUrl, ComparablePolicy.class);
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
		return runJsonRequestPostDxp(requestUrl, request, PolicySummary.class, Response.Status.CREATED.getStatusCode());
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
		return runJsonRequestGetDxp(requestUrl, PolicySummary.class, code);
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, String> executeLookupValidate(String lookupName, String productCd, String riskStateCd, String effectiveDate) {
		String requestUrl = urlBuilderDxp(String.format(DXP_LOOKUPS, lookupName, productCd, riskStateCd));
		if (effectiveDate != null) {
			requestUrl = requestUrl + "&effectiveDate=" + effectiveDate;
		}
		return runJsonRequestGetDxp(requestUrl, HashMap.class);
	}

	public static PolicyPremiumInfo[] endorsementRate(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_RATE, policyNumber));
		return runJsonRequestPostDxp(requestUrl, null, PolicyPremiumInfo[].class, status);
	}

	public static ErrorResponseDto endorsementRateError(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_RATE, policyNumber));
		return runJsonRequestPostDxp(requestUrl, null, ErrorResponseDto.class, 422);
	}

	public static PolicySummary endorsementBind(String policyNumber, String authorizedBy, int status) {
		AAABindEndorsementRequestDTO request = new AAABindEndorsementRequestDTO();
		request.authorizedBy = authorizedBy;
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_BIND, policyNumber));
		return runJsonRequestPostDxp(requestUrl, request, PolicySummary.class, status);
	}

	public static String deleteEndorsement(String policyNumber, int status) {
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT, policyNumber));
		return runJsonRequestDeleteDxp(requestUrl, String.class, status);
	}

	public static ErrorResponseDto endorsementBindError(String policyNumber, String authorizedBy, int status) {
		AAABindEndorsementRequestDTO request = new AAABindEndorsementRequestDTO();
		request.authorizedBy = authorizedBy;
		String requestUrl = urlBuilderDxp(String.format(DXP_POLICIES_ENDORSEMENT_BIND, policyNumber));
		return runJsonRequestPostDxp(requestUrl, request, ErrorResponseDto.class, status);
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
		return runJsonRequestGetDxp(requestUrl, DiscountSummary.class, status);
	}

	public static AccountDetails billingAccountInfoService(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_BILLING_ACCOUNT_INFO, policyNumber));
		return runJsonRequestGetDxp(requestUrl, AccountDetails.class);
	}

	public static Installment[] billingInstallmentsInfo(String policyNumber) {
		String requestUrl = urlBuilderDxp(String.format(DXP_BILLING_INSTALLMENTS_INFO, policyNumber));
		return runJsonRequestGetDxp(requestUrl, Installment[].class);
	}

	public static String runJsonRequestPostDxp(String url, RestBodyRequest bodyRequest) {
        return runJsonRequestPostDxp(url, bodyRequest, String.class, Response.Status.OK.getStatusCode());
    }

    private static <T> T runJsonRequestPostDxp(String url, RestBodyRequest bodyRequest, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = url;
		restRequestInfo.bodyRequest = bodyRequest;
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
        return runJsonRequestMethodDxp(restRequestInfo, RequestMethod.POST);
    }

//	public static DriversDto runJsonRequestPostClaims(String url, String jsonFileName, int status)
	public static ClaimsAssignmentResponse runJsonRequestPostClaims(String url) {
		RestRequestInfo<ClaimsAssignmentResponse> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = url; //https://claims-assignment.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-claims
		restRequestInfo.bodyRequest =  "{\n"
				+ "   \"claims\":[\n"
				+ "      {\n"
				+ "         \"dateOfLoss\":\"2019-01-11\",\n"
				+ "         \"policyReferenceNumber\":\"AZSS952918543\",\n"
				+ "         \"cause\":\"cha\",\n"
				+ "         \"totalAmountPaid\":2200,\n"
				+ "         \"dateClosed\":\"2019-07-31\",\n"
				+ "         \"type\":\"Accident\",\n"
				+ "         \"claimDeductible\":500,\n"
				+ "         \"driverInformation\":{\n"
				+ "            \"relationToNamedInsured\":\"INSURED\",\n"
				+ "            \"firstName\":\"Bilbo\",\n"
				+ "            \"lastName\":\" \",\n"
				+ "            \"issuedState\":\"AZ\",\n"
				+ "            \"dateOfBirth\":\"1997-05-09\",\n"
				+ "            \"licenseNumber\":\"A98769878\"\n"
				+ "         },\n"
				+ "         \"dateOpened\":\"2019-01-02\",\n"
				+ "         \"lossSummary\":\"crashed into waving inflatable arm guy\",\n"
				+ "         \"amountpaid\":2200,\n"
				+ "         \"claimNumber\":\"1TAZ1111OHS\",\n"
				+ "         \"status\":\"CLOSED\"\n"
				+ "      },\n"
				+ "      {\n"
				+ "         \"dateOfLoss\":\"2019-01-11\",\n"
				+ "         \"policyReferenceNumber\":\"AZSS952918543\",\n"
				+ "         \"cause\":\"cha\",\n"
				+ "         \"totalAmountPaid\":2200,\n"
				+ "         \"dateClosed\":\"2019-07-31\",\n"
				+ "         \"type\":\"Accident\",\n"
				+ "         \"claimDeductible\":500,\n"
				+ "         \"driverInformation\":{\n"
				+ "            \"relationToNamedInsured\":\"BROTHER\",\n"
				+ "            \"firstName\":\"Bruce\",\n"
				+ "            \"lastName\":\" \",\n"
				+ "            \"issuedState\":\"AZ\",\n"
				+ "            \"dateOfBirth\":\"1997-08-30\",\n"
				+ "            \"licenseNumber\":\"A12345222\"\n"
				+ "         },\n"
				+ "         \"dateOpened\":\"2019-01-02\",\n"
				+ "         \"lossSummary\":\"collided with 1 passenger bus\",\n"
				+ "         \"amountpaid\":2200,\n"
				+ "         \"claimNumber\":\"7TZ02222OHS\",\n"
				+ "         \"status\":\"CLOSED\"\n"
				+ "      },\n"
				+ "      {\n"
				+ "         \"dateOfLoss\":\"2019-01-11\",\n"
				+ "         \"policyReferenceNumber\":\"AZSS952918543\",\n"
				+ "         \"cause\":\"cha\",\n"
				+ "         \"totalAmountPaid\":2200,\n"
				+ "         \"dateClosed\":\"2019-07-31\",\n"
				+ "         \"type\":\"Accident\",\n"
				+ "         \"claimDeductible\":500,\n"
				+ "         \"driverInformation\":{\n"
				+ "            \"relationToNamedInsured\":\"SISTER\",\n"
				+ "            \"firstName\":\"Tony\",\n"
				+ "            \"lastName\":\" \",\n"
				+ "            \"issuedState\":\"AZ\",\n"
				+ "            \"dateOfBirth\":\"1997-08-30\",\n"
				+ "            \"licenseNumber\":\"A12345333\"\n"
				+ "         },\n"
				+ "         \"dateOpened\":\"2019-01-02\",\n"
				+ "         \"lossSummary\":\"collided with 10 parked vehicles\",\n"
				+ "         \"amountpaid\":2200,\n"
				+ "         \"claimNumber\":\"3TAZ3333OHS\",\n"
				+ "         \"status\":\"CLOSED\"\n"
				+ "      }\n"
				+ "   ],\n"
				+ "   \"policy\":{\n"
				+ "      \"termEndDate\":\"2022-07-07\",\n"
				+ "      \"policyNumber\":\"AZSS952918543\",\n"
				+ "      \"termEffectiveDate\":\"2021-07-07\",\n"
				+ "      \"drivers\":[\n"
				+ "         {\n"
				+ "            \"firstName\":\"Fernando-GEHJJ\",\n"
				+ "            \"lastName\":\"Smith\",\n"
				+ "            \"relationshipToFNI\":\"IN\",\n"
				+ "            \"licenseNumber\":\"A12345678\",\n"
				+ "            \"middleName\":\"A12345678\",\n"
				+ "            \"driverType\":\"afr\",\n"
				+ "            \"existingActivities\":[\n"
				+ "\n"
				+ "            ]\n"
				+ "         },\n"
				+ "         {\n"
				+ "            \"firstName\":\"Bruce\",\n"
				+ "            \"lastName\":\"Banner\",\n"
				+ "            \"relationshipToFNI\":\"SI\",\n"
				+ "            \"licenseNumber\":\"A12345222\",\n"
				+ "            \"middleName\":\"A12345222\",\n"
				+ "            \"driverType\":\"afr\",\n"
				+ "            \"existingActivities\":[\n"
				+ "               {\n"
				+ "                  \"description\":\"AAF\",\n"
				+ "                  \"source\":\"CSAAClaims\",\n"
				+ "                  \"oid\":\"hvwnBuC3FFSTZzIeR7VE9w\",\n"
				+ "                  \"type\":\"afa\",\n"
				+ "                  \"claimNumber\":\"7TZ02222OHS\",\n"
				+ "                  \"LossPaymentAmout\":2200\n"
				+ "               }\n"
				+ "            ]\n"
				+ "         },\n"
				+ "         {\n"
				+ "            \"firstName\":\"Tony\",\n"
				+ "            \"lastName\":\"Stark\",\n"
				+ "            \"relationshipToFNI\":\"SI\",\n"
				+ "            \"licenseNumber\":\"A12345333\",\n"
				+ "            \"middleName\":\"A12345333\",\n"
				+ "            \"driverType\":\"afr\",\n"
				+ "            \"existingActivities\":[\n"
				+ "               {\n"
				+ "                  \"description\":\"AAF\",\n"
				+ "                  \"source\":\"CSAAClaims\",\n"
				+ "                  \"oid\":\"oK6HhBpt67oFTmQ-vqxCzA\",\n"
				+ "                  \"type\":\"afa\",\n"
				+ "                  \"claimNumber\":\"3TAZ3333OHS\",\n"
				+ "                  \"LossPaymentAmout\":2200\n"
				+ "               }\n"
				+ "            ]\n"
				+ "         }\n"
				+ "      ],\n"
				+ "      \"annualTerm\":true\n"
				+ "   }\n"
				+ "}";
		restRequestInfo.responseType = ClaimsAssignmentResponse.class; //TODO - 200 is the code
		return runJsonRequestMethodDxp(restRequestInfo, RequestMethod.POST);
	}






	public static <T> T runJsonRequestPatchDxp(String url, RestBodyRequest bodyRequest, Class<T> responseType) {
		return runJsonRequestPatchDxp(url, bodyRequest, responseType, Response.Status.OK.getStatusCode());
	}

	//WORKING
	public static <T> T runJsonRequestPatchDxp(String url, RestBodyRequest request, Class<T> responseType, int status) {
		Client client = null;
		Response response = null;
		log.info("Request: " + asJson(request));
		try {
			client = ClientBuilder.newClient().property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true).register(JacksonJsonProvider.class);

			String token = getBearerToken();

			response = client.target(url)
					.request()
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.method("PATCH", Entity.json(request));
			response.bufferEntity();
			T responseObj = response.readEntity(responseType);
			log.info(response.toString());
			if (response.getStatus() != status) {
				//handle error
				throw new IstfException("PATCH json response failed");
			}
			return responseObj;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	public static <T> T runJsonRequestPutDxp(String url, RestBodyRequest bodyRequest, Class<T> responseType) {
		return runJsonRequestPutDxp(url, bodyRequest, responseType, Response.Status.OK.getStatusCode());
	}

	/**
	 *  Migrate to {@link #runJsonRequestMethodDxp(RestRequestInfo, RequestMethod)}
	 */
	@Deprecated
	public static <T> T runJsonRequestPutDxp(String url, RestBodyRequest request, Class<T> responseType, int status) {
		Client client = null;
		Response response = null;
		log.info("Request: " + asJson(request));
		try {
			client = ClientBuilder.newClient().property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true).register(JacksonJsonProvider.class);

			String token = getBearerToken();

			response = client.target(url)
					.request()
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.method("PUT", Entity.json(request));
			T responseObj = readBufferedEntity(response, responseType);
			log.info(response.toString());
			if (response.getStatus() != status) {
				//handle error
				throw new IstfException("PATCH json response failed");
			}
			return responseObj;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	/**
	 *  Migrate to {@link #runJsonRequestMethodDxp(RestRequestInfo, RequestMethod)}
	 */
	@Deprecated
	public static <T> T runJsonRequestPostAdmin(String url, RestBodyRequest request, Class<T> responseType, int status) {
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			WebTarget target = client.target(url);

			response = target
					.request()
					.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encode("qa:qa".getBytes()))
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.method("POST", Entity.json(request));
			T responseObj = readBufferedEntity(response, responseType);
			log.info(response.toString());
			if (response.getStatus() != status) {
				//handle error
				throw new IstfException("POST?PATCH? json response failed");
			}
			return responseObj;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	public static <T> T runJsonRequestDeleteDxp(String url, Class<T> responseType) {
		return runJsonRequestDeleteDxp(url, responseType, Response.Status.OK.getStatusCode());
	}

	public static <T> T runJsonRequestDeleteDxp(String url, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = url;
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		return runJsonRequestMethodDxp(restRequestInfo, RequestMethod.DELETE);
	}

	public static <T> T runJsonRequestDeleteDxp(String url, Class<T> responseType, RestBodyRequest request, int status) {
		RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = url;
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		restRequestInfo.bodyRequest = request;
		return runJsonRequestMethodDxp(restRequestInfo, RequestMethod.DELETE);
	}

	public static <T> T runJsonRequestGetDxp(String url, Class<T> responseType) {
		return runJsonRequestGetDxp(url, responseType, Response.Status.OK.getStatusCode());
	}

	public static <T> T runJsonRequestGetDxp(String url, Class<T> responseType, int status) {
		RestRequestInfo<T> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = url;
		restRequestInfo.responseType = responseType;
		restRequestInfo.status = status;
		return runJsonRequestGetDxp(restRequestInfo);
	}

	public static <T> T runJsonRequestGetDxp(RestRequestInfo<T> request) {
		return runJsonRequestMethodDxp(request, RequestMethod.GET);
	}

	private static <T> T runJsonRequestGetAdmin(String url, Class<T> returnClazz) {
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			WebTarget target = client.target(url);

			response = target
					.request()
					.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encode("qa:qa".getBytes()))
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.get();
			T result = response.readEntity(returnClazz);
			log.info(response.toString());
			if (response.getStatus() != Response.Status.OK.getStatusCode()) {
				//handle error
				throw new IstfException("GET json request failed");
			}
			return result;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	private static Invocation.Builder createJsonRequest(Client client, String url, String sessionId) {
		Invocation.Builder builder = client.target(url).request().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		if (BooleanUtils.toBoolean(PropertyProvider.getProperty(CsaaTestProperties.OAUTH2_ENABLED))) {
			String token = getBearerToken();
			if (StringUtils.isNotEmpty(token)) {
				builder = builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			}
		}
		return builder.header(APPLICATION_CONTEXT_HEADER, createApplicationContext(sessionId));
	}

	private static String getBearerToken() {
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			WebTarget target = client.target(PropertyProvider.getProperty(CsaaTestProperties.WIRE_MOCK_STUB_URL_TEMPLATE) + PropertyProvider.getProperty(CsaaTestProperties.PING_HOST));
			response = target
					.request()
					.header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED)
					.post(Entity.json(GetOAuth2TokenRequest.create().asUrlEncoded()));

			Map result = response.readEntity(HashMap.class);

			return result.get("access_token").toString();
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	private static String createApplicationContext(String sessionId) {
		try {
			ApplicationContext applicationContext = new ApplicationContext();
			applicationContext.address = "AutomationTest";
			applicationContext.application = "MyPolicy";
			applicationContext.correlationId = Guid.GUID.newGuid().toString();
			applicationContext.sessionId = sessionId;
			return DEFAULT_OBJECT_MAPPER.writeValueAsString(applicationContext);
		} catch (JsonProcessingException e) {
			throw new IstfException("Failed to create application context");
		}
	}

	public static AAAMakeByYear getMakes(String year, String productCd, String stateCd, String formType, String effectiveDate) {
		String url = urlBuilderAdmin(String.format(DXP_RETRIEVE_MAKE_BY_YEAR, year, productCd, stateCd, formType, effectiveDate));

		return runJsonRequestGetAdmin(cutFormType(formType, url), AAAMakeByYear.class);
	}

	public static String cutFormType(String formType, String url) {
		if (formType.isEmpty() || formType == null) {
			url = url.replaceAll("&formType=", "");
		}
		return url;
	}

	public static AAAModelByYearMake getModels(String year, String make, String productCd, String stateCd, String formType, String effectiveDate) {
		String url = urlBuilderAdmin(String.format(DXP_RETRIEVE_MODEL_BY_YEAR_MAKE, year, make, productCd, stateCd, formType, effectiveDate));

		return runJsonRequestGetAdmin(cutFormType(formType, url), AAAModelByYearMake.class);
	}

	/**
	 * @param formType can be null or empty
	 */
	public static AAABodyStyleByYearMakeModelSeries getBodyStyle(String year, String make, String model, String series, String productCd, String stateCd, String formType, String effectiveDate) {
		String url = urlBuilderAdmin(String.format(DXP_RETRIEVE_BODYSTYLE_BY_YEAR_MAKE_MODEL_SERIES, year, make, model, series, productCd, stateCd, formType, effectiveDate));

		return runJsonRequestGetAdmin(cutFormType(formType, url), AAABodyStyleByYearMakeModelSeries.class);
	}

	public static AAASeriesByYearMakeModel getSeries(String year, String make, String model, String productCd, String stateCd, String formType, String effectiveDate) {
		String url = urlBuilderAdmin(String.format(DXP_SERIES_BY_YEAR_MAKE_MODEL, year, make, model, productCd, stateCd, formType, effectiveDate));

		return runJsonRequestGetAdmin(url, AAASeriesByYearMakeModel.class);
	}

	private static String asJson(Object object) {
		try {
			return PRETTY_PRINT_OBJECT_MAPPER.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error("Failed to parse request/response as json", e);
			return null;
		}
	}

	/**
	 * Generic request method implementation for specific request and method type.
	 * @param request - rest request to send.
	 * @param requestMethod - request method to use.
	 * @param <T> - response body class type.
	 * @return response instance of specific class.
	 */
	public static <T> T runJsonRequestMethodDxp(RestRequestInfo<T> request, RequestMethod requestMethod) {
		Client client = null;
		Response response = null;
		try {
			log.info("Request: " + asJson(request));
			if (RequestMethod.DELETE == requestMethod) {
				ClientConfig config = new ClientConfig();
				config.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
				client = ClientBuilder.newClient(config).register(JacksonJsonProvider.class);
			} else {
				client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			}
			if(requestMethod == RequestMethod.PATCH) {
				client = client.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
			}
			Invocation.Builder jsonRequest = createJsonRequest(client, request.url, request.sessionId);
			String methodName = requestMethod.name();
			if (request.bodyRequest != null) {
				response = jsonRequest.method(methodName, Entity.json(request.bodyRequest));
			} else {
				response = jsonRequest.method(methodName);
			}
			T result = readBufferedEntity(response, request.responseType);
			log.info(response.toString());
			if (response.getStatus() != request.status) {
				//handle error
				throw new IstfException(methodName + " json request failed");
			}
			log.info("Response: " + asJson(result));
			return result;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	/**
	 * Buffers method body and try to read response of expected type. If {@link ProcessingException} is thrown method 
	 * attempts to parse and log body as string and rethrown the exception.
	 * Exception will not be caught if not expected nor error response body is parsed.
	 * @param response service response to read from
	 * @param responseType expected response class
	 * @param <T> - expected response type
	 * @return response instance of specific class
	 */
	private static <T> T readBufferedEntity(Response response, Class<T> responseType) {
		if (response.bufferEntity()) {
			try {
				return response.readEntity(responseType);
			} catch (ProcessingException e) {
				log.error("Actual response: " + System.lineSeparator() + asJson(response.readEntity(String.class)));
			}
		} 
		return response.readEntity(responseType);
	}
}
