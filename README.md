# Asynchronous Behavior in Spring Boot Application

We can achieve asynchronous behavior in spring boot application using two annotations like :

1) @EnableAsync 
2) @Async.

What is @EnableAsync ?
------------------------------------------------------------------------------------------------------------
This annotation and can be applied on application classes for asynchronous behavior. This annotation will look for methods marked with @Async annotation and run in background thread pools.

What is @Async ? 
------------------------------------------------------------------------------------------------------------
The @Async annotated methods can return CompletableFuture to hold the result of an asynchronous computation.

What is CompletableFuture ?
------------------------------------------------------------------------------------------------------------
A CompltableFuture is used for asynchronous programming. Asynchronous programming means writing non-blocking code. It runs a task on a separate thread than the main application thread and notifies the main thread about its progress, completion or failure.

In this way, the main thread does not block or wait for the completion of the task. Other tasks execute in parallel. Parallelism improves the performance of the program.

A CompletableFuture is a class in Java. It belongs to java.util.cocurrent package. It implements CompletionStage and Future interface.

Now configure to achieve asynchronous behavior in spring boot restful web application by using follow steps:

STEP-1 : Create Async Thread Pool :
------------------------------------------------------------------------------------------------------------
	@Configuration
	@EnableAsync
	public class AsyncConfig 
	{
		@Bean(name ="taskExecutor")
		public Executor taskExecutor()
		{
			ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
			executor.setCorePoolSize(2);
			executor.setMaxPoolSize(2);
			executor.setQueueCapacity(100);
			executor.setThreadNamePrefix("Thread-");
			executor.initialize();
			return executor;
		}
	}
	
STEP-2 : Create @Async Methods :
-----------------------------------------------------------------------------------------------------------
	@Async
	@Override
	public CompletableFuture<List<Customer>> saveCustomers(MultipartFile file) 
	{
		List<Customer> customers = null;
		Long start = System.currentTimeMillis();

		customers = customerDao.saveAllCustomer(CSVOperation.parseCSVFile(file)); // GET DATA FROM CSV AND SAVE IN DB		

		Long end = System.currentTimeMillis();
		log.info("Completation Time : {}", (end-start));

		return CompletableFuture.completedFuture(customers);
	}
----------------------------------------------------------------------------------------------------------
	@Async
	@Override	
	public CompletableFuture<List<Customer>> getCustomersUsingAsync()
	{
		List<Customer> customers = customerDao.getCustomers();

		try { Thread.sleep(50000);	} 
		catch (InterruptedException e) { e.printStackTrace(); }	// WAIT FOR SOME SECONDS

		return CompletableFuture.completedFuture(customers);
	}

STEP-3 : Create @Async Rest Controllers :
----------------------------------------------------------------------------------------------------------
	@PostMapping()
	public ResponseEntity<Object> saveCustomers(@RequestParam("file") MultipartFile files)
	{		
		List<Customer> customers = customerService.getCustomers();// NORMAL METHOD CALL TO GET ALL CUSTOMERS

		log.debug("Customers : ",customers);

		customerService.saveCustomers(files);// READ 3000 RECORDS FROM CSV AND SAVE INTO DB -> THROUGH ASYNC METHOD

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
[ASYNC METHOD FOR] GET ALL CUSTOMERS DATA :
---------------------------------------------------------------------------------------------------------
	@GetMapping()
	public CompletableFuture<Object> getCustomers()
	{
		return customerService.getCustomersUsingAsync().thenApply(ResponseEntity::ok);		
	}
	
[ASYNC METHOD FOR] GET DATA FROM MULTIPLE ASYNC METHODS :
-------------------------------------------------------------------------------------------------------
	@GetMapping("/multiple")
	public ResponseEntity<Object> getMultipleCustomers()
	{
		CompletableFuture<List<Customer>> customerList1 = customerService.getCustomersUsingAsync();
		CompletableFuture<List<Customer>> customerList2 = customerService.getCustomersUsingAsync();
		CompletableFuture<List<Customer>> customerList3 = customerService.getCustomersUsingAsync();

		CompletableFuture.allOf(customerList1, customerList2, customerList3).join();

		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
# Exception Handling :
------------------------------------------------------------------------------------------------------
1) If you get AsyncRequestTimeoutException :

Resolved [org.springframework.web.context.request.async.AsyncRequestTimeoutException]
	
	{
		"timestamp": "2020-04-05T06:23:33.897+0000",
		"status": 503,
		"error": "Service Unavailable",
		"message": "No message available",
		"trace": "org.springframework.web.context.request.async.AsyncRequestTimeoutException\r\n\tat org.springframework.web.context.request.async.TimeoutDeferredResultProcessingInterceptor.handleTimeout(TimeoutDeferredResultProcessingInterceptor.java:42)\r\n\tat org.springframework.web.context.request.async.DeferredResultInterceptorChain.triggerAfterTimeout(DeferredResultInterceptorChain.java:79)\r\n\tat org.springframework.web.context.request.async.WebAsyncManager.lambda$startDeferredResultProcessing$5(WebAsyncManager.java:428)\r\n\tat java.util.ArrayList.forEach(Unknown Source)\r\n\tat org.springframework.web.context.request.async.StandardServletAsyncWebRequest.onTimeout(StandardServletAsyncWebRequest.java:151)\r\n\tat org.apache.catalina.core.AsyncListenerWrapper.fireOnTimeout(AsyncListenerWrapper.java:44)\r\n\tat org.apache.catalina.core.AsyncContextImpl.timeout(AsyncContextImpl.java:139)\r\n\tat org.apache.catalina.connector.CoyoteAdapter.asyncDispatch(CoyoteAdapter.java:153)\r\n\tat org.apache.coyote.AbstractProcessor.dispatch(AbstractProcessor.java:237)\r\n\tat org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:59)\r\n\tat org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:868)\r\n\tat org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1639)\r\n\tat org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)\r\n\tat java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)\r\n\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)\r\n\tat org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)\r\n\tat java.lang.Thread.run(Unknown Source)\r\n",
		"path": "/customers"
	}
	
Soluation :
-----------------------------------------------------------------------------------------------------
Cause and Soluation : 

The default timeout in Tomcat is 30 seconds, so we are handel spring async request-timeout limlit or we can simple define following code in application.properties file.

	spring.mvc.async.request-timeout=-1
	
