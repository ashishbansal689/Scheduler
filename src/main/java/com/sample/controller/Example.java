package com.sample.controller;

import java.util.Set;

import org.quartz.Job;
import org.reflections.Reflections;

public class Example {

	public static void main(String[] args) {
		Reflections reflecstions = new Reflections("com.sample");
		
		Set<Class<? extends Job>> subTypes = reflecstions.getSubTypesOf(Job.class); 
		
		for(Class<? extends Job> classs : subTypes)
		{
			System.out.println(classs.getSimpleName());
		}
	}
}
