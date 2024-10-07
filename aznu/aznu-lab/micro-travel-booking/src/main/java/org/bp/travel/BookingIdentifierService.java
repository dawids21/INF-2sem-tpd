package org.bp.travel;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class BookingIdentifierService {

	public String getBookingIdentifier() {
		return UUID.randomUUID().toString();
	}

}