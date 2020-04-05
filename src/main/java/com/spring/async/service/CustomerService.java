package com.spring.async.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.multipart.MultipartFile;

import com.spring.async.model.Customer;

public interface CustomerService 
{
	public CompletableFuture<List<Customer>> saveCustomers(MultipartFile file);
	
	public List<Customer> getCustomers();

	public CompletableFuture<List<Customer>> getCustomersUsingAsync();
}
