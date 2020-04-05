package com.spring.async.dao;

import java.util.List;

import com.spring.async.model.Customer;

public interface CustomerDao 
{
	public List<Customer> saveAllCustomer(List<Customer> customers);

	public List<Customer> getCustomers();

}
