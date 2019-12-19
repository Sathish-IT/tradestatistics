package com.solactive.service;


import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import com.solactive.Launcher;
import com.solactive.model.Tick;
import com.solactive.util.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Launcher.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Ignore
public class TradeAsyncAPITest{

	@Autowired
	private TradeService tradeService;
	
	@Autowired
	private TaskExecutor taskExecutor;
	
	private final int MAX_CONCURRENT_REQUEST = 20000;
    
	public void executeAsynchronously1() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				calculatePostTime();
				
			}
		});
	}
	
	public void executeAsynchronously2() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				calculateGetTime();
			}
		});
	}
	
	private void calculatePostTime() {
		try {
			System.out.println("Async POST execution ");
			int i = 1;
			Tick tick = new Tick();
			
			long startTime = System.currentTimeMillis();
			long tickTime = -1;
			while( i++ <= MAX_CONCURRENT_REQUEST) {
				tick = new Tick();
				if (i%4 ==0)
					tick.setInstrument("ins_"+(i-1));
				else
					tick.setInstrument("ins_"+i);
				tick.setPrice(i*10.0);
				
				if (i%8 ==0)
					tickTime = tickTime-10000;
				else if (i%7 == 0)
					tickTime = tickTime-80000;
				else if (i%6 == 0)
					tickTime = tickTime-40000;
				else
					tickTime = System.currentTimeMillis();
				
				tick.setTimestamp(tickTime);
				
				tradeService.addOrUpdateStatistics(TestUtils.getTickRequest());
			}
			long totalTime = System.currentTimeMillis() - startTime;
			System.out.println("Total time to process "+i+" POST request is "+totalTime+" ms");
		}catch(Exception e) {
			System.out.println("Error ");
			e.printStackTrace();
		}
	}
	
	private void calculateGetTime() {
		try {
			System.out.println("Async GET execution ");
			int i = 1;
			long startTime = System.currentTimeMillis();
			String inst_id = null;
			while( i++ <= MAX_CONCURRENT_REQUEST) {
				if (i%4 ==0)
					inst_id = "ins_"+(i-1);
				else
					inst_id = null;
				tradeService.getInstrumentStatistics(inst_id);
			}
			long totalTime = System.currentTimeMillis() - startTime;
			System.out.println("Total time to process "+i+" GET request is "+totalTime+" ms");
		}catch(Exception e) {
			System.out.println("Error ");
			e.printStackTrace();
		}
	}
	

}