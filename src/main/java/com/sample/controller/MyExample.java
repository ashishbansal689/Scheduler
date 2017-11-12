package com.sample.controller;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Properties;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.jdbcjobstore.JobStoreTX;

public class MyExample implements Job{

	public static void main(String[] args) throws SchedulerException {
		
		 Properties prop = new Properties();

	        prop.setProperty("org.quartz.jobStore.class", 
	                          "org.quartz.impl.jdbcjobstore.JobStoreTX");
	        prop.setProperty("org.quartz.jobStore.driverDelegateClass", 
	                          "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
	        prop.setProperty("org.quartz.jobStore.dataSource", "tasksDataStore");
	        prop.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
	        prop.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
	        prop.setProperty("org.quartz.jobStore.isClustered", "false");
	        prop.setProperty("org.quartz.threadPool.threadCount", "4");
	        prop.setProperty("org.quartz.jobStore.useProperties", "true");

	        prop.setProperty("org.quartz.dataSource.tasksDataStore.driver", 
	             "org.postgresql.Driver");
	        prop.setProperty("org.quartz.dataSource.tasksDataStore.URL", "jdbc:postgresql://localhost:5432/Learning");
	        prop.setProperty("org.quartz.dataSource.tasksDataStore.user", "postgres");
	        prop.setProperty("org.quartz.dataSource.tasksDataStore.password", 
	             "postgres");
	        prop.setProperty("org.quartz.dataSource.tasksDataStore.maxConnections", "20");
	        
		Scheduler scheduler = new StdSchedulerFactory(prop).getScheduler();
		
	

		JobStoreTX store = new JobStoreTX();
		
		JobKey jobKey = new JobKey("dummyJobName2", "group2");
		
		JobDataMap map = new JobDataMap();
		map.put("data", "THis is first");
		 // define the job and tie it to our HelloJob class
		  JobDetail job = newJob(MyExample.class)
		      .withIdentity(jobKey)
		      .withDescription("This is my third job to be executed")
		      .setJobData(map)
		      .build();

		  TriggerKey triggerKey = new TriggerKey("trigger");
		  // Trigger the job to run now, and then every 40 seconds
		  Trigger trigger = newTrigger()
		      .withIdentity(triggerKey)
		      .startNow()
		      .withSchedule(simpleSchedule()
		          .withIntervalInSeconds(40)
		          .repeatForever())
		      .build();

			/*scheduler.getListenerManager().addJobListener(
		    		new MyExample(), KeyMatcher.keyEquals(jobKey)
		    	);
			scheduler.start();*/
			
		  // Tell quartz to schedule the job using our trigger
		  scheduler.scheduleJob(job, trigger);

	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		System.out.println("Job is running");
		
	}
}
