/*
 * Copyright 2012 FuseSource
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fusesource.examples.flights;

import java.util.Set;
import com.fusesource.examples.booking.spi.BookingProcessor;

public class BookingProcessorRegistry {
	private Set<BookingProcessor> bookingProcessors;

	public void setBookingProcessors(Set<BookingProcessor> bookingProcessors) {
		this.bookingProcessors = bookingProcessors;
	}

	public boolean isAirlineSupported(String flightNumber) {
		boolean airlineSupported = false;
		if (bookingProcessors != null) {
			for (BookingProcessor bookingProcessor : bookingProcessors) {
				String airlineCode = null;
				try {
					airlineCode = bookingProcessor.getAirlineCode();
				} catch (Exception ex) {
					// the service has gone away
					continue;
				}
				if (flightNumber.startsWith(airlineCode)) {
					airlineSupported = true;
					break;
				}
			}
		}
		return airlineSupported;
	}

	public String getBookingProcessorUri(String flightNumber) {
		String routeUri = null;
		if (bookingProcessors != null) {
			for (BookingProcessor bookingProcessor : bookingProcessors) {
				try {
                    // check if flight is from airline
                    if (flightNumber.startsWith(bookingProcessor.getAirlineCode())) {
                        routeUri = bookingProcessor.getRouteUri();
                    }
				} catch (Exception ex) {
					// the service has gone away
					continue;					
				}
			}
		}
		return routeUri;
	}
}