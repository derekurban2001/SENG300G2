package org.lsmr.usecases;

import java.io.IOException;
import java.math.BigDecimal;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.TapFailureException;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;
import org.lsmr.selfcheckout.external.CardIssuer;

public class DebitCardPayment extends UseCases{
	CardIssuer bank;
	CardReaderListener cardReaderListener;
	boolean debug = false;
	
	public DebitCardPayment() {
		
		cardReaderListener = new CardReaderListener() {

			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
				if(debug) System.out.println("Card reader is disabled!");
			}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
				if(debug) System.out.println("Card reader is disabled!");
			}

			@Override
			public void cardInserted(CardReader reader) {
				if(debug) System.out.println("Card has been inserted!");
				
			}

			@Override
			public void cardRemoved(CardReader reader) {
				if(debug) System.out.println("Card has been removed!");
			}

			@Override
			public void cardTapped(CardReader reader) {
				if(debug) System.out.println("Card has been tapped!");
			}

			@Override
			public void cardSwiped(CardReader reader) {
				if(debug) System.out.println("Card has been swiped!");
			}

			@Override
			public void cardDataRead(CardReader reader, CardData data) {
				if(debug) System.out.println("Card's data has been read!");
				
				// if not debit card then ignore
				if (data.getType() != "debit") { 
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
	
	public void removeCard(){
		station.cardReader.remove(); 
	}
	
	public void tapCard(Card card, CardIssuer bank) throws IOException, TapFailureException{
		if (amountOwed.compareTo(new BigDecimal(100)) > 0) {
			if (debug) System.out.println("Tap payment limit is 100");
			throw new TapFailureException();
		}
		this.bank = bank;
		station.cardReader.tap(card);
	}
	
	public void swipeCard(Card card, CardIssuer bank) throws IOException {
		this.bank = bank;
		station.cardReader.swipe(card, null); 
	}
}
