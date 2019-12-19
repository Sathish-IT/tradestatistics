package com.solactive.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.solactive.exception.TradeStatsValidationException;
import com.solactive.model.ErrorResponse;
import com.solactive.model.Tick;

public class TradeUtil {
	
	private final static String INVALID_TICK_REQUEST_ERROR = "Invalid input. Expecting valid instrument(string), price(double), timestamp(long)";
	private final static String INVALID_INSTRUMENT_ERROR = "Invalid instrument value. Expecting valid instrument(string)";
	private final static String INVALID_PRICE_ERROR = "Invalid price. Expecting valid price(double)";
	private final static String INVALID_TIMESTAMP_ERROR = "Invalid timestamp. Expecting valid time(in millis) long format";
	
	// This method validate the tick and throws TradeStatsValidationException(status=400 with error message)
	public static ErrorResponse validateTickRequest(Tick tick){
		ErrorResponse errorResp = new ErrorResponse();
		List<String> errList = new ArrayList<String>();
		if( tick != null ) {
			if( null == tick.getInstrument() || tick.getInstrument().strip().isEmpty() )
				addErrorItem(errList, INVALID_INSTRUMENT_ERROR);
			
			if( null == tick.getTimestamp() || tick.getTimestamp() <= 0 )
				addErrorItem(errList, INVALID_TIMESTAMP_ERROR);
			
			try {
				double priceVal = tick.getPrice();
				Double.parseDouble(String.valueOf(priceVal));
			}catch(Exception e) {
				addErrorItem(errList,INVALID_PRICE_ERROR);
			}
		}else{
			addErrorItem(errList, INVALID_TICK_REQUEST_ERROR);
		}
		if( errList != null ) {
			errList.forEach(err -> errorResp.addErrorsItem(err));
		}
		return errorResp;
	}
	
	// This method adds errormessage to the error list
	private static void addErrorItem(List<String> errList, String errorMessage) {
		if( errList == null )
			errList = new ArrayList();
		errList.add(errorMessage);
	}
	
}
