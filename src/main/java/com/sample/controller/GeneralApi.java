package com.sample.controller;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.Notification;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.reflections.Reflections;

import com.sample.jobs.NotificationJob;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import scala.collection.mutable.HashMap;

@Path("/schedulerApis")
public class GeneralApi {

	private static Scheduler scheduler;
	private static HashMap<String, Class<? extends Job>> allJobClassesMap = new HashMap<String, Class<? extends Job>>();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Say Hello World", notes = "Anything Else?")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 500, message = "Something wrong in Server") })
	public Response getMsg(@PathParam("param") String msg) {

		String output = "Jersey say : " + msg;

		return Response.status(200).entity(output).build();

	}

	@POST
	@Path("/scheduleJob")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Schedule a job", notes = "It schedule a background job according to given information about the job")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Job has been scheduled"),
			@ApiResponse(code = 500, message = "Something went wrong in Server") })
	public Response scheduleJob(@HeaderParam("JobClassName") String nameOfTheClass) 
	{

		try {
			if(scheduler == null)
				scheduler = new StdSchedulerFactory().getScheduler(); 
            
			JobKey key = new JobKey(nameOfTheClass);
            Class<? extends Job> className = allJobClassesMap.get(nameOfTheClass).get();
            JobDetail jobDetail = newJob(className)
            		.withIdentity(key)
            		.storeDurably()
            		.withDescription("This is new job named by: " + nameOfTheClass)
            		.build();
            
            TriggerKey triggerKey = new TriggerKey(nameOfTheClass);
            Trigger trigger = newTrigger()
            		.withIdentity(triggerKey)
            		.startNow()
            		.withSchedule(simpleSchedule()
          		          .withIntervalInSeconds(10)
        		          .repeatForever())
            		.forJob(jobDetail).
            		build();
            
            scheduler.start();
            scheduler.addJob(jobDetail, true);
            scheduler.scheduleJob(jobDetail, trigger);
            
        } catch (SchedulerException e) {
          e.printStackTrace();
        }
		return Response.status(200).entity("Job with given name: " + nameOfTheClass + " has been scheduled").build();

	}
	
	@Path(value = "/listAllJobs")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllJobs()
	{
		Reflections reflecstions = new Reflections("com.sample");
		Set<Class<? extends Job>> subTypes = reflecstions.getSubTypesOf(Job.class); 
		
		List<String> allJobClasses = new ArrayList<>();
		for(Class<? extends Job> classs : subTypes)
		{
			allJobClasses.add(classs.getSimpleName());
			allJobClassesMap.put(classs.getSimpleName(), classs);
		}
		
		return Response.status(200).entity(allJobClasses).build();
	}
	
	
	@Path(value = "/listAllRunningJobs")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllRunningJobs()
	{
		Map<String, String> allRunningJobsMap = new java.util.HashMap<>();
		try 
		{
			if(scheduler != null && scheduler.isShutdown() == false)
			{
				List<JobExecutionContext> allRunningJobs = scheduler.getCurrentlyExecutingJobs();
				for(JobExecutionContext context : allRunningJobs)
				{
					allRunningJobsMap.put(context.getJobDetail().getKey().getName(), context.getJobDetail().getDescription());
				}
			}
			
		} 
		catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Response.status(200).entity(allRunningJobsMap).build();
	}
}
