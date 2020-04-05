package com.spring.async.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.async.model.Customer;
import com.spring.async.repository.CustomerRepository;

@Service
public class CustomerDaoImpl implements CustomerDao
{
	@Autowired
	private CustomerRepository customerRepository;	
	
	@Override
	public List<Customer> saveAllCustomer(List<Customer> customers) 
	{		
		return (List<Customer>) customerRepository.saveAll(customers);
	}

	@Override
	public List<Customer> getCustomers()
	{
		return (List<Customer>) customerRepository.findAll();
	}

}
