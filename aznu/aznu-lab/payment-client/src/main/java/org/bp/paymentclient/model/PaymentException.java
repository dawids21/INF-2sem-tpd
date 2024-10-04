package org.bp.paymentclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentException extends RuntimeException{

	
	public PaymentException(String msg) {
		super(msg);
	}

}
