/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.enums;

public final class CEMConstants {

    private CEMConstants() {}

    public static final class CEMCampaignsTable {
        public static final String CAMPAIGN_NAME = "Campaign Name";
        public static final String CAMPAIGN_ID = "Campaign ID";
        public static final String STATUS = "Status";
    }

    public static final class CEMCampaignInfoTable {
        public static final String RELATIONSHIP = "Relationship";
    }

    public static final class CEMCampaignRelationshipInfoTable {
        public static final String CAMPAIGN_ID = "Campaign ID";
        public static final String ACTION = "Action";
    }

    public static final class CEMCommunicationsTable {
        public static final String ID = "ID";
    }

    public static final class CEMCommunicationInfoDetailsTable {
        public static final String COMMUNICATION_DESCRIPTION = "Communication Description";
    }

    public static final class CEMGroupsInformationTable {
        public static final String GROUP_TYPE = "Group Type";
        public static final String ACTIONS = "Actions";
    }

    public static final class CEMMajorLargeAccountTable {
        public static final String DESIGNATION_TYPE = "Designation Type";
        public static final String ACTION = "Action";
    }
}
