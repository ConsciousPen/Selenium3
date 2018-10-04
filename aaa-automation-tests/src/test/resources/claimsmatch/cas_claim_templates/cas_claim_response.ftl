<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<bcr:ClaimBatchResponse
        xmlns:AAANCNU_Common_version1="http://www.aaancnuit.com.AAANCNU_Common_version1"
        xmlns:bcr="http://www.aaancnuit.com.BatchClaimFeedResponse_version1"
        xmlns:batch="http://www.aaancnuit.com.AAANCNU_BatchCommon_version1"
        xsi:schemaLocation="http://www.aaancnuit.com.AAANCNU_Common_version1 Common_version1.xsd http://www.aaancnuit.com.BatchClaimFeedResponse_version1 BatchClaimFeedResponse_version1.xsd http://www.aaancnuit.com.AAANCNU_BatchCommon_version1 BatchCommon_version1.xsd"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<#if claimResponse.claimLineItemList?has_content>
    <#list claimResponse.claimLineItemList as claimLineItem>
        <bcr:ClaimLineItem>
            <batch:lineItemErrorInfo>
                <AAANCNU_Common_version1:errorCode>0</AAANCNU_Common_version1:errorCode>
                <AAANCNU_Common_version1:severity>Non Critical</AAANCNU_Common_version1:severity>
                <AAANCNU_Common_version1:errorMessageText></AAANCNU_Common_version1:errorMessageText>
                <AAANCNU_Common_version1:friendlyErrorMessage></AAANCNU_Common_version1:friendlyErrorMessage>
                <AAANCNU_Common_version1:serviceName>Claims Batch Retrieval</AAANCNU_Common_version1:serviceName>
                <AAANCNU_Common_version1:sourceSystem>GWCC</AAANCNU_Common_version1:sourceSystem>
            </batch:lineItemErrorInfo>
            <bcr:product>${claimLineItem.product}</bcr:product>
            <bcr:agreementNumber>${claimLineItem.agreementNumber}</bcr:agreementNumber>
            <#if claimLineItem.claimList?has_content>
                <#list claimLineItem.claimList as claim>
                    <bcr:claims>
                        <bcr:claimPolicyReferenceNumber>${claim.claimPolicyReferenceNumber}</bcr:claimPolicyReferenceNumber>
                        <bcr:claimNumber>${claim.claimNumber}</bcr:claimNumber>
                        <bcr:claimPrefix>${claim.claimPrefix}</bcr:claimPrefix>
                        <bcr:claimType>${claim.claimType}</bcr:claimType>
                        <bcr:claimCause>${claim.claimCause}</bcr:claimCause>
                        <bcr:claimOpenDate>${claim.claimOpenDate}</bcr:claimOpenDate>
                        <bcr:claimCloseDate>${claim.claimCloseDate}</bcr:claimCloseDate>
                        <bcr:claimDateOfLoss>${claim.claimDateOfLoss}</bcr:claimDateOfLoss>
                        <bcr:claimStatusCode>${claim.claimStatusCode}</bcr:claimStatusCode>
                        <bcr:accidentFault>${claim.accidentFault}</bcr:accidentFault>
                        <bcr:lossSummary>${claim.lossSummary}</bcr:lossSummary>
                        <bcr:claimDeductible>
                            <AAANCNU_Common_version1:amount>${claim.claimDeductibleAmount}</AAANCNU_Common_version1:amount>
                            <AAANCNU_Common_version1:currencyCode>${claim.claimDeductibleCurrencyCode}</AAANCNU_Common_version1:currencyCode>
                        </bcr:claimDeductible>
                        <bcr:subroFlag>${claim.subroFlag}</bcr:subroFlag>
                        <bcr:totalAmountPaid>
                            <AAANCNU_Common_version1:amount>${claim.totalAmountPaid}</AAANCNU_Common_version1:amount>
                            <AAANCNU_Common_version1:currencyCode>${claim.totalAmountPaidCurrencyCode}</AAANCNU_Common_version1:currencyCode>
                        </bcr:totalAmountPaid>
                        <bcr:driverInformation>
                            <bcr:driverName>${claim.driverName}</bcr:driverName>
                            <bcr:drivingLicenseNumber>${claim.drivingLicenseNumber}</bcr:drivingLicenseNumber>
                            <bcr:drivingLicenseState>${claim.drivingLicenseState}</bcr:drivingLicenseState>
                            <bcr:driverAgeAsOfDateOfLoss>${claim.driverAgeAsOfDateOfLoss}</bcr:driverAgeAsOfDateOfLoss>
                            <bcr:driverDateOfBirth>${claim.driverDateOfBirth}</bcr:driverDateOfBirth>
                            <bcr:driverRelationToInsured>${claim.driverRelationToInsured}</bcr:driverRelationToInsured>
                        </bcr:driverInformation>
                        <bcr:vehicleInformation>
                            <bcr:vehicleSerialNumber>${claim.vehicleSerialNumber}</bcr:vehicleSerialNumber>
                            <bcr:vehicleBodyType>${claim.vehicleBodyType}</bcr:vehicleBodyType>
                            <bcr:vehicleMake>${claim.vehicleMake}</bcr:vehicleMake>
                            <bcr:vehicleManufacturedYear>${claim.vehicleManufacturedYear}</bcr:vehicleManufacturedYear>
                            <bcr:vehicleDescription>${claim.vehicleDescription}</bcr:vehicleDescription>
                        </bcr:vehicleInformation>
                        <#if claim.claimCoverageList?has_content>
                                <#list claim.claimCoverageList as coverage>
                                    <bcr:claimCoverages>
                                        <bcr:coverageId>${coverage.coverageId}</bcr:coverageId>
                                        <bcr:coverageName>${coverage.coverageName}</bcr:coverageName>
                                        <bcr:amountPaid>
                                            <AAANCNU_Common_version1:amount>${coverage.claimCoverageAmount}</AAANCNU_Common_version1:amount>
                                            <AAANCNU_Common_version1:currencyCode>${coverage.claimCoverageCurrencyCode}</AAANCNU_Common_version1:currencyCode>
                                        </bcr:amountPaid>
                                    </bcr:claimCoverages>
                                </#list>
                        </#if>
                    </bcr:claims>
                </#list>
            </#if>
        </bcr:ClaimLineItem>
    </#list>
</#if>
</bcr:ClaimBatchResponse>