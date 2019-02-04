package aaa.helpers.renewal;

/**
 * Used to control how many jobs should be run during the Renewal Helper's processes. <br>
 * All - Run all jobs in the renewal timeline. <br>
 * Minimum - Run image generation, Membership Validation @ Membership Timepoint 1 and 2.
 */
public enum RenewalHelper_Profile {
    All, Membership, IncludeSTG1and2;
}
