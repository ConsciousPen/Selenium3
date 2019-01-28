/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.admin.pages.general.GeneralAsyncTasksPage;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.admin.pages.general.GeneralSchedulerPage.Job;
import aaa.common.enums.NavigationEnum.AdminAppLeftMenu;
import aaa.common.pages.NavigationPage;

public final class JobRunner {

    private static Logger log = LoggerFactory.getLogger(JobRunner.class);

    private static List<Job> jobs = Collections.synchronizedList(new ArrayList<Job>());
	private static boolean isAsyncManagerRestarted;
    private static LocalDateTime currentPhase;

    private JobRunner() {}

    public static void executeJob(Job job) {
        executeJob(job, false);
    }

    public static void executeJob(Job job, boolean forceExecution) {
        if (TimeSetterUtil.getInstance().isPEF()) {
            LocalDateTime phaseTime = TimeSetterUtil.getInstance().getPhaseStartTime();
            if (!phaseTime.equals(currentPhase)) {
                jobs.clear();
                isAsyncManagerRestarted = false;
                currentPhase = phaseTime;
            }
        } else {
            jobs.clear();
            isAsyncManagerRestarted = false;
        }

        if (forceExecution && jobs.contains(job)) {
            jobs.remove(job);
        }

        synchronized (job) {
            if (jobs.contains(job)) {
                log.info("[JOBS] Job " + job.get() + " has been already executed.");
            } else {
                if (!isAsyncManagerRestarted) {
                    restartAsyncManager();
                }

                boolean isExecutedSuccessfully = false;
                try {
                    log.info("[JOBS] Starting job " + job.get() + " execution");

                    GeneralSchedulerPage.runJob(job);
                    isExecutedSuccessfully = true;

                    log.info("[JOBS] Job " + job.get() + " executed successfully");
                } catch (Exception ex) {
                    log.info("[JOBS] Job " + job.get() + " failed to execute");
                }
                if (isExecutedSuccessfully) {
                    jobs.add(job);
                }
                //GeneralSchedulerPage.eliminateJob(job);
            }
        }
    }

    public static void restartAsyncManager() {
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.GENERAL_ASYNC_TASKS.get());
        if (!GeneralAsyncTasksPage.linkStopManager.isPresent()) {
            GeneralAsyncTasksPage.linkStartManager.click();
        }
        GeneralAsyncTasksPage.linkStopManager.click();
        GeneralAsyncTasksPage.linkStartManager.click();
    }
}
