package com.spring.async.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.spring.async.dao.CustomerDao;
import com.spring.async.model.Customer;
import com.spring.async.util.CSVOperation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService 
{
	@Autowired
	private CustomerDao customerDao;	
	
//--------------------------------------------------------------[ASYNC METHOD FOR] GET DATA FROM CSV AND SAVE IN DB------
	@Override	
	@Async
	public CompletableFuture<List<Customer>> saveCustomers(MultipartFile file) 
	{
		List<Customer> customers = null;
		
		Long start = System.currentTimeMillis();
					
		customers = customerDao.saveAllCustomer(CSVOperation.parseCSVFile(file));	// GET DATA FROM CSV AND SAVE IN DB		
		
		Long end = System.currentTimeMillis();
		
		log.info("Completation Time : {}", (end-start));
		
		return CompletableFuture.completedFuture(customers);
	}

//--------------------------------------------------------------[NORMAL METHOD FOR] GET ALL CUSTOMERS DATA-------------
	@Override
	public List<Customer> getCustomers()
	{
		return customerDao.getCustomers();
	}

//--------------------------------------------------------------[ASYNC METHOD FOR] GET ALL CUSTOMERS DATA--------------
	@Override
	@Async
	public CompletableFuture<List<Customer>> getCustomersUsingAsync()
	{
		List<Customer> customers = customerDao.getCustomers();
		
		try { Thread.sleep(50000);	} 
		catch (InterruptedException e) { e.printStackTrace(); }	// WAIT FOR SOME SECONDS
		
		return CompletableFuture.completedFuture(customers);
	}

}
