package com.spring.async.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.spring.async.model.Customer;

@Repository
public interface CustomerRepository  extends CrudRepository<Customer, Integer>
{

}
