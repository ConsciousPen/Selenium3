package aaa.helpers.claim.datamodel.claim;

public class ClaimCoverage {

    private String coverageId;
    private String coverageName;
    private String claimCoverageAmount;
    private String claimCoverageCurrencyCode;

    public String getCoverageId() {
        return coverageId;
    }

    public void setCoverageId(String coverageId) {
        this.coverageId = coverageId;
    }

    public String getCoverageName() {
        return coverageName;
    }

    public void setCoverageName(String coverageName) {
        this.coverageName = coverageName;
    }

    public String getClaimCoverageAmount() {
        return claimCoverageAmount;
    }

    public void setClaimCoverageAmount(String claimCoverageAmount) {
        this.claimCoverageAmount = claimCoverageAmount;
    }

    public String getClaimCoverageCurrencyCode() {
        return claimCoverageCurrencyCode;
    }

    public void setClaimCoverageCurrencyCode(String claimCoverageCurrencyCode) {
        this.claimCoverageCurrencyCode = claimCoverageCurrencyCode;
    }

    @Override
    public String toString() {
        return "ClaimCoverage{" +
                "coverageId='" + coverageId + '\'' +
                ", coverageName='" + coverageName + '\'' +
                ", claimCoverageAmount='" + claimCoverageAmount + '\'' +
                ", claimCoverageCurrencyCode='" + claimCoverageCurrencyCode + '\'' +
                '}';
    }
}
