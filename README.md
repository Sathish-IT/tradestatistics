# Trade Statistics Service
The Trade stats service provides financial index statistics calulation based on tick request received and provides statistics data for the last 60 seconds (sliding time interval) on received ticks. The tick will be received at any point of time, prior to current timestamp as well. The tick API and statistics API provides concurrent access support to serve from many instruments.

# How to Build
#### **Note:** This project currently requires Java 11

##### Using command line argument
Navigate to the checkout directory
```bash
cd <check-out-dir>/tradestatistics
mvn clean install
```
Now executable jar tradestats-service-0.0.1-SNAPSHOT.jar is generated at server/target directory.

# Launch the service #
#### **Note:** This project currently requires Java 11

### Using command line argument
Navigate to the target directory
```bash
cd <check-out-dir>/tradestatistics/server/target
java -jar tradestats-service-0.0.1-SNAPSHOT.jar
```

Now the service will be start running at port 25000

## Configuration
The following properties can be configured either through command line arguments or application.properties file

|Name|Description|default|
|----|-----------|-------|
|server.port|service port|25000|
|trade.monitor-time-in-millis|time to expire tick from statistics calculation|60000|

## Swagger UI
The project is integrated with swagger and all the API can be viewed and accessed in the swagger ui.
Once the service is started running then use the below url to access Swagger UI.

http://localhost:25000/swagger-ui.html

## Assumptions
The following were assumed on the trade statistics service implementation
1. By default min double value is assumed to be Postiive double infinity
   and max double value is assumed to be Negative double infinity for input comparisions.
2. Older data purge scheduler is configured to run for every 60 seconds
3. Double precision is not implemented but double validation is done, so given decimal precision is followed in the statistics response
4. It is assumed the service is running only on HTTP mode.
5. java 11 is the required version for building service and corresponding maven version is required.

## Future Enhancements
The following areas could be improved further
1. Logger support with log4j so that different levels of logging could be done and used in scalability/normal mode test setup with info/debug/warn level kind of support. When sysout is replaced with logger then performance would be improved further.
2. HTTPS support could be provided for Security improvement on API access.
3. Performance would be improved when using modelmapper and avg calculation could be done only at the time of mapping response object as it requires only sum and count values. It avoids avg calculation that is made during every iterations. As modelmapper doesnot lombok for mapping the AtomicDecimal to decimal values it is not done during implemenations. If that is resolved there could be considerable performance improvements. 
4. Queueing input tick request could improve performance a little though still concurrent map is used for storing the tick values.
5. Junit for scalability can be added and improved from existing test cases and few more general test cases could be added.
6. Integrate Sonar for code quality analysis and reports.

## Challenge
I like this challenge as it touches and evaluate skillset in many areas. Multithreading is really interesting. Its really Good. 
