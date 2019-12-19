package com.solactive.service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.solactive.api.TradeApiDelegate;
import com.solactive.exception.TickNotFoundException;
import com.solactive.exception.TradeStatsValidationException;
import com.solactive.model.ErrorResponse;
import com.solactive.model.Tick;
import com.solactive.model.TradeStats;
import com.solactive.util.Statistics;
import com.solactive.util.TradeProperties;
import com.solactive.util.TradeUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TradeService implements TradeApiDelegate{

	private ObjectMapper objectMapper = new ObjectMapper();
	
	private static int MONITOR_TIME_IN_MILLIS; 
	private static final int TICK_PURGE_EXECUTOR_IN_MILLIS = 60*1000;
	private ConcurrentNavigableMap<Long, CopyOnWriteArrayList<Tick>> tickMap = new ConcurrentSkipListMap<Long, CopyOnWriteArrayList<Tick>>();
	private TaskExecutor taskExecutor;
    
    @Autowired
	public TradeService(TradeProperties tradeProperties, TaskExecutor taskExecutor) {
    	MONITOR_TIME_IN_MILLIS = tradeProperties.getMonitorTimeInMillis();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
	
    // This method execute period schedule to purge the tick values stored in memory that are older than Monitor time(MONITOR_TIME_IN_MILLIS) 
	@Scheduled(fixedDelay=TICK_PURGE_EXECUTOR_IN_MILLIS)
    private void purgeOlderEntriesThanMonitorTime() {
	   long currentTime = System.currentTimeMillis();
	   purgeOlderEntries(currentTime);
    }
	
	// This method purge the tick values stored in memory that are older than Monitor period(MONITOR_TIME_IN_MILLIS) given the monitor end time
	private void purgeOlderEntries(long monitorEndTime) {
		long purgeTime = monitorEndTime - MONITOR_TIME_IN_MILLIS;
	    System.out.println("Purging ticks older than time "+purgeTime + " current time");
	    tickMap.headMap(purgeTime).clear();
	}
	
	/* This method receives and process the tick requests
	 *   If the tick timestamp was within monitor period(tradeProperties.getMonitorTimeInMillis()) then 201 status is returned
	 *   else if it is more than the monitor period then 204 status is returned
	 */
	public ResponseEntity addOrUpdateStatistics(Tick tick){
		long currentTime = System.currentTimeMillis();
		try{
			ErrorResponse errorResponse = TradeUtil.validateTickRequest(tick);
			if( null != errorResponse && null != errorResponse.getErrors()) {
				System.out.println(errorResponse);
				return new ResponseEntity(errorResponse.getErrors(), HttpStatus.BAD_REQUEST);
			}else {
				System.out.println(errorResponse);
			}
			long tickTime = tick.getTimestamp();
			System.out.println("tick details: "+tick); 	
			if( isOlderThanMonitorPeriod(currentTime, tickTime)) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}else {
				CopyOnWriteArrayList<Tick> tickList = tickMap.get(tickTime);
				if( CollectionUtils.isEmpty(tickList)) {
					tickList = new CopyOnWriteArrayList<Tick>();
				}
				tickList.add(tick);
				tickMap.put(tickTime, tickList);
				return new ResponseEntity<>(HttpStatus.CREATED);
			}
		}catch( TradeStatsValidationException te) {
			throw te;
		}catch(Exception e) {
			System.err.println("Problem while validating stats "+e.getMessage());
			e.printStackTrace();
		}finally {
			// TODO - Logging is not done yet. Enable below lines for logging execution time
			//long execTimeInMillis = System.currentTimeMillis() - currentTime;
			//System.out.println("Total execution time for tick update in millis : "+execTimeInMillis);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	// This method return statistics for the specific requested instrument
	public ResponseEntity<TradeStats> getInstrumentStatistics(String instrumentid){
		return calculateStatistics(instrumentid);
	}
	
	// This method return statistics considering all the instrument
	public ResponseEntity<TradeStats> getStatistics(){
		return calculateStatistics(null);
	}
	
	/* This method return statistics for the instrument
	 *	if the instrumentid is passed in the request then statistics is returned for the specific instruments over the monitor period(tradeProperties.getMonitorTimeInMillis())
	 *	else the statistics is returned considering all the instruments tick values   
	 */
	private ResponseEntity<TradeStats> calculateStatistics(String instrumentid){
		//long startTime = System.currentTimeMillis();
		try {
			purgeOlderEntries(System.currentTimeMillis());
			Statistics tradeStatistics = new Statistics();
			Stream<Tick> tickStream = tickMap.values().stream().flatMap(tick -> tick.stream());
			if( instrumentid != null ) {
				tickStream = tickStream.filter(tick -> tick.getInstrument().equals(instrumentid));
			}
			tickStream.forEach(tick -> tradeStatistics.updateStats(tick.getPrice()));

			if( tradeStatistics.getMin().get() == Double.POSITIVE_INFINITY ) {
				System.out.println("No trade recored found for given request");
				throw new TickNotFoundException("No trade stats found for given request");
			}else {
				// Comment/Uncomment below line to display/hide logging calculated statistics
				System.out.println("Trade statistics "+tradeStatistics); 

				String statsStr = objectMapper.writeValueAsString(tradeStatistics);
				TradeStats respTradeStats = objectMapper.readValue(statsStr, TradeStats.class);
				return new ResponseEntity<>(respTradeStats, HttpStatus.OK);
			}
		}catch( TickNotFoundException te) {
			throw te;
		}catch(Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			// TODO - Logging is not done yet. Enable below lines and funtion startline for logging execution time
			//long execTimeInMillis = System.currentTimeMillis() - startTime;
			//System.out.println("Total execution time for statistics in millis : "+execTimeInMillis);
		}
	}
	
	/* This method logs all the ticks that are received during the monitor period, 
	 *	It also contains few values between last purge time till monitor period that gets removed in next purge execution.
	 */	 
	private void logAllCurrentTicks() {
		tickMap.values().stream().flatMap(tick -> Stream.of(tick)).forEach(System.out::println);
	}
	
	// This method checks whether the trade time is older than the monitor time given the current time and trade time 
	private boolean isOlderThanMonitorPeriod(long currentTime, long tradeTime) {
		System.out.println(currentTime-tradeTime);
		if( currentTime-tradeTime <= MONITOR_TIME_IN_MILLIS )
			return false;
		return true;
	}

}