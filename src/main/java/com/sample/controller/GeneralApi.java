package com.sample.controller;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "/schedulerApis", description = "This is an API to schedule a job and listing already running jobs")
@Path("/schedulerApis")
@Produces({"application/json", "application/xml"})
public class GeneralApi {

	private static Scheduler scheduler;
	private static HashMap<String, Class<? extends Job>> allJobClassesMap = new HashMap<String, Class<? extends Job>>();
	
	@POST
	@Path("/scheduleJob")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Schedule a job", notes = "It schedules a background job according to given information about the job")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Job has been scheduled with the name of job"),
			@ApiResponse(code = 500, message = "Will show the error message") })
	public Response scheduleJob(@HeaderParam("JobClassName") String nameOfTheClass) 
	{
		try {
			if(scheduler == null)
				scheduler = new StdSchedulerFactory().getScheduler(); 
            
			JobKey key = new JobKey(nameOfTheClass);
            Class<? extends Job> className = allJobClassesMap.get(nameOfTheClass);
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
            
        }
		catch (Exception e) 
		{
			e.printStackTrace();
			return Response.status(500).entity(new Gson().toJson("Something went wrong while scheduling a job with name: " + nameOfTheClass + ". Error cause was " + e.getMessage())).build();
        }
		return Response.status(200).entity(new Gson().toJson("Job has been scheduled with given name: " + nameOfTheClass)).build();

	}
	
	@Path(value = "/listAllJobs")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "List all the jobs", notes = "It returns the list of jobs name which can be scheduled as a job using this application.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Returns a list of name of classes that can be schedule"),
			@ApiResponse(code = 500, message = "Will show the error message") })
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
		
		return Response.status(200).entity(new Gson().toJson(allJobClasses)).build();
	}
	
	
	@Path(value = "/listAllRunningJobs")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Show a map with all the running jobs name along with their description", notes = "It shows all the running jobs which are currntly running")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Shows a map"),
			@ApiResponse(code = 500, message = "Will show the error message") })
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
			return Response.status(500).entity(new Gson().toJson("Something went wrong while getting are the running jobs. Error cause was " + e.getMessage())).build();
		}
		
		if(allRunningJobsMap.isEmpty())
		{
			return Response.status(200).entity(new Gson().toJson("No job is running currently")).build();
		}
		
		return Response.status(200).entity(new Gson().toJson(allRunningJobsMap)).build();
	}
}
