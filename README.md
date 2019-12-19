# Trade Stats Service
The Trade stats service provides financial index statistics calulation based on tick request received and statistics is calculated from the last 60 seconds (sliding time interval). The tick will be received at any point of time, prior to current timestamp as well. The tick API and statistics API provides concurrent access support to serve from many instruments.

# Build
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

## Configuration
The following properties can be configured either through command line arguments or application.properties file

|Name|Description|default|
|----|-----------|-------|
|trade.monitor-time-in-millis|time to expire tick for statistics calculation|60000|
```
