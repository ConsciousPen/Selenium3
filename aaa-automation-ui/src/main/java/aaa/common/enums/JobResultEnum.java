/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.enums;

public class JobResultEnum {

	private JobResultEnum() {}

	public enum JobStatus {
		RUNNING("Running"),
		WAITING("Waiting"),
		PASSED("Passed");

		String id;

		JobStatus(String id) {
			this.id = id;
		}

		public String get() {
			return id;
		}
	}

	public static final class JobStatisticsConstants {
		public static final String DATE = "date";
		public static final String TIME = "time";
		public static final String PROCESSED_COUNT = "processedCount";
		public static final String SUCCESS_COUNT = "successCount";
		public static final String ERROR_COUNT = "errorCount";
	}

}