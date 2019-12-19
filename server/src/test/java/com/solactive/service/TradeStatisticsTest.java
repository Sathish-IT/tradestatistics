package com.solactive.service;


import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solactive.Launcher;
import com.solactive.model.Tick;
import com.solactive.model.TradeStats;
import com.solactive.util.TestUtils;
import com.solactive.util.TradeProperties;
import org.springframework.test.annotation.DirtiesContext;


@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = {Launcher.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TradeStatisticsTest{

	@Autowired
	private TradeService tradeService;
	
	@Autowired
	private TradeProperties tradeProperties;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private AtomicInteger counter = new AtomicInteger(1);
	double leftLimit = 1D;
    double rightLimit = 100000D;
    
	@Test
    public void checkTickExpiresAfterMonitorTime() throws InterruptedException {
		//Verify new tick post
		Tick tick = TestUtils.getTickMinRequest();
		tick.setInstrument("common-ins-1");
		long startTimeStamp = tick.getTimestamp();
		double min = tick.getPrice();
		double max = tick.getPrice();
		ResponseEntity postRespEntiry = tradeService.addOrUpdateStatistics(tick);
        assertEquals(postRespEntiry.getStatusCode(), HttpStatus.CREATED);
        
        //Verify same instrument tick new price post later 500Millis before timestamp
        tick = TestUtils.getTickMinRequest();
        tick.setInstrument("common-ins-1");
        tick.setPrice(tick.getPrice()*3);
        max = tick.getPrice();
        tick.setTimestamp(System.currentTimeMillis());
        postRespEntiry = tradeService.addOrUpdateStatistics(tick);
        assertEquals(postRespEntiry.getStatusCode(), HttpStatus.CREATED);
        
        ResponseEntity<TradeStats> getStatsRespEntity = tradeService.getInstrumentStatistics(tick.getInstrument());
        assertEquals(getStatsRespEntity.getStatusCode(), HttpStatus.OK);
        System.out.println("resp entity "+getStatsRespEntity.getBody() );
        assertEquals(getStatsRespEntity.getBody().getMin(),Double.valueOf(min));
        assertEquals(getStatsRespEntity.getBody().getMax(),Double.valueOf(max));
        assertEquals(getStatsRespEntity.getBody().getAvg(),Double.valueOf((min+max)/2));
        double avg = getStatsRespEntity.getBody().getAvg();
        
        tick = TestUtils.getTickMaxRequest();
        tick.setInstrument("common-ins-2");
        tick.setPrice(tick.getPrice()*4);
        double min2 = tick.getPrice();
        max = tick.getPrice();
        avg = ((avg*2)+max)/3;
        tick.setTimestamp(System.currentTimeMillis());
        postRespEntiry = tradeService.addOrUpdateStatistics(tick);
        assertEquals(postRespEntiry.getStatusCode(), HttpStatus.CREATED);
        
        //Specific instrument 2 get stats
        getStatsRespEntity = tradeService.getInstrumentStatistics(tick.getInstrument());
        assertEquals(getStatsRespEntity.getStatusCode(), HttpStatus.OK);
        System.out.println("resp entity "+getStatsRespEntity.getBody() );
        assertEquals(getStatsRespEntity.getBody().getMin(),Double.valueOf(min2));
        assertEquals(getStatsRespEntity.getBody().getMax(),Double.valueOf(max));
        assertEquals(getStatsRespEntity.getBody().getAvg(),Double.valueOf(tick.getPrice()));
        
        //get all stats
        getStatsRespEntity = tradeService.getInstrumentStatistics(null);
        assertEquals(getStatsRespEntity.getStatusCode(), HttpStatus.OK);
        System.out.println("resp entity "+getStatsRespEntity.getBody() );
        assertEquals(getStatsRespEntity.getBody().getMin(),Double.valueOf(min));
        assertEquals(getStatsRespEntity.getBody().getMax(),Double.valueOf(max));
        assertEquals(getStatsRespEntity.getBody().getAvg(),Double.valueOf(avg));
        
    }
	
}