package com.solactive.service;


import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.solactive.Launcher;
import com.solactive.exception.TradeStatsValidationException;
import com.solactive.model.Tick;
import com.solactive.util.TestUtils;
import com.solactive.util.TradeProperties;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Launcher.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TradeServiceTest{

	@Autowired
	private TradeService tradeService;
	
	@Autowired
	private TradeProperties tradeProperties;
	
	@Autowired
	private TaskExecutor taskExecutor;
	
	private final int MAX_CONCURRENT_REQUEST = 20000;
	
    
    @Test
    public void verifyTickStatsTest() {
    	ResponseEntity postRespEntiry = tradeService.addOrUpdateStatistics(TestUtils.getTickRequest());
        assertEquals(postRespEntiry.getStatusCode(), HttpStatus.CREATED);
    }
    	
	@Test
    public void addNewTickEntry() {
		ResponseEntity responseEntity = tradeService.addOrUpdateStatistics(TestUtils.getTickRequest());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);
    }
	
	@Test
    public void updateExistingTickEntry() {
		Tick tick = TestUtils.getTickRequest();
        ResponseEntity responseEntity = tradeService.addOrUpdateStatistics(tick);
        tick.setPrice(tick.getPrice()*2);
        tick.setTimestamp(System.currentTimeMillis());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);
    }
	
	@Test
    public void expiredTickAddTest() {
		Tick tick = TestUtils.getTickRequest();
		tick.setTimestamp(tick.getTimestamp()-2*tradeProperties.getMonitorTimeInMillis()); 
        ResponseEntity responseEntity = tradeService.addOrUpdateStatistics(tick);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NO_CONTENT);
    }
	
	@Test
    public void expiredTickUpdateTest() {
		Tick tick = TestUtils.getTickRequest();
		tick.setTimestamp(tick.getTimestamp()-2*tradeProperties.getMonitorTimeInMillis());
		//Insert check
        ResponseEntity responseEntity = tradeService.addOrUpdateStatistics(tick);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NO_CONTENT);
        
        //Update check
        tick.setTimestamp(tick.getTimestamp()-tradeProperties.getMonitorTimeInMillis()); 
        responseEntity = tradeService.addOrUpdateStatistics(tick);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NO_CONTENT);
    }
	
	 @Test
	 public void validateTickRequest() {
		 // Validating null tick request
		 ResponseEntity responseEntity = tradeService.addOrUpdateStatistics(null);
	     assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
	     System.out.println("responseEntity "+responseEntity.getBody());
	     //assertEquals(responseEntity.get, HttpStatus.BAD_REQUEST);
	 
	     // Validating null instrument
	     Tick tick = TestUtils.getTickRequest();
		 tick.setInstrument(null);
		 responseEntity = tradeService.addOrUpdateStatistics(tick);
	     assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
	     
	     // Validating null price
	     tick = TestUtils.getTickRequest();
	 	 tick.setPrice(null);
	 	 responseEntity = tradeService.addOrUpdateStatistics(tick);
	     assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
	  
	     // Validating null timestamp
	     tick = TestUtils.getTickRequest();
		 tick.setTimestamp(null);
		 responseEntity = tradeService.addOrUpdateStatistics(tick);
	     assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
	 }

}