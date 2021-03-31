package org.lsmr.usecases;


import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.TapFailureException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;
import org.lsmr.selfcheckout.external.CardIssuer;

public class CreditCardPayment extends UseCases{
	
	CardIssuer bank;
	String pin;
	Card card;
	BufferedImage signature = null;
	
	CardReaderListener cardReaderListener;
	boolean debug = false;
	
	public CreditCardPayment() {
		
		cardReaderListener = new CardReaderListener() {

			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
				if(debug) System.out.println("card reader is enabled");
			}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
				if(debug) System.out.println("card reader is disabled!");
			}

			@Override
			public void cardInserted(CardReader reader) {
				if(debug) System.out.println("card has bee inserted!");
				
			}

			@Override
			public void cardRemoved(CardReader reader) {
				if(debug) System.out.println("card has bee removed!");
				
			}

			@Override
			public void cardTapped(CardReader reader) {
				if (debug) System.out.println("card tapped");
				
			}

			@Override
			public void cardSwiped(CardReader reader) {
				if (debug) System.out.println("card swiped");
				
			}

			@Override
			public void cardDataRead(CardReader reader, CardData data) {
				if (data.getType().toLowerCase().indexOf("credit") == -1) { 
					if (debug) System.out.println("Invalid card!");
				}
				else {
					// check account balance
					if (bank.authorizeHold(data.getNumber(), amountOwed) != -1) {
						// if hold not fail
						amountOwed = BigDecimal.ZERO;
						if (debug) System.out.println("Transaction successful!");
					}
					else {
						if (debug) System.out.println("Transaction failed!");
					}
				}
				
			}};
			
			station.cardReader.register(cardReaderListener);
		
	}
	
	public void insertCard(Card card, String pin, CardIssuer bank) throws DisabledException, IOException{
		this.bank = bank;
		station.cardReader.insert(card, pin); 
	}
	public void tapCard(Card card,CardIssuer bank) throws IOException,TapFailureException	 {
		if (amountOwed.compareTo(new BigDecimal(100)) > 0) {
			if (debug) System.out.println("Tap payment limit is 100");
			throw new TapFailureException();
		}
		this.bank = bank;
		station.cardReader.tap(card); 
	}
	public void swipeCard(Card card,CardIssuer bank) throws IOException {
		this.bank = bank;
		station.cardReader.swipe(card, signature); 
	}
	
	public void removeCard() {
		station.cardReader.remove(); 
	}

}
