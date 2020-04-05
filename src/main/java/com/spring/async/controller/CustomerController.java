package com.spring.async.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spring.async.model.Customer;
import com.spring.async.service.CustomerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/customers")
public class CustomerController 
{
	@Autowired
	private CustomerService customerService;
	
//----------------------------------------------------------[ASYNC METHOD FOR] GET DATA FROM CSV AND SAVE IN DB------
	
	@PostMapping()
	public ResponseEntity<Object> saveCustomers(@RequestParam("file") MultipartFile files)
	{		
		List<Customer> customers = customerService.getCustomers();		// NORMAL METHOD CALL TO GET ALL CUSTOMERS
		
		log.debug("Customers : ",customers);
		
		customerService.saveCustomers(files);		 // READ 3000 RECORDS FROM CSV AND SAVE INTO DB -> THROUGH ASYNC METHOD
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
//---------------------------------------------------------[ASYNC METHOD FOR] GET ALL CUSTOMERS DATA--------------
	
	@GetMapping()
	public CompletableFuture<Object> getCustomers()
	{
		return customerService.getCustomersUsingAsync().thenApply(ResponseEntity::ok);		
	}
	
//---------------------------------------------------------[ASYNC METHOD FOR] GET DATA FROM MULTIPLE ASYNC METHODS-------
	
	@GetMapping("/multiple")
	public ResponseEntity<Object> getMultipleCustomers()
	{
		CompletableFuture<List<Customer>> customerList1 = customerService.getCustomersUsingAsync();
		CompletableFuture<List<Customer>> customerList2 = customerService.getCustomersUsingAsync();
		CompletableFuture<List<Customer>> customerList3 = customerService.getCustomersUsingAsync();
		
		CompletableFuture.allOf(customerList1, customerList2, customerList3).join();
		 
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
