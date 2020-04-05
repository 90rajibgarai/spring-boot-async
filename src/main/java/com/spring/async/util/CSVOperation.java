package com.spring.async.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.spring.async.model.Customer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVOperation 
{
	public static List<Customer> parseCSVFile(MultipartFile file)
	{
		final List<Customer> customers = new ArrayList<>();		
        try 
        {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) 
            {
                String line;
                
                while ((line = br.readLine()) != null) 
                {
                    final String[] data = line.split(",");
                    
                    final Customer customer = new Customer();
                    
                    customer.setName(data[0]);
                    customer.setGender(data[1]);
                    customer.setEmail(data[2]);
                    customer.setMobile(data[3]);
                    customers.add(customer);
                }
                return customers;
            }
        } 
        catch (final IOException e) 
        {
            log.error("Failed to parse CSV file {}", e);
        }
		return customers;
	}
}
