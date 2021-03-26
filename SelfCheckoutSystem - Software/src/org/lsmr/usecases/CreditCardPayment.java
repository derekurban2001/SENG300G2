package org.lsmr.usecases;

import org.lsmr.selfcheckout.Coin;

import java.io.IOException;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;

public class CreditCardPayment extends UseCases{
	
	CardReaderListener cardReaderListener;
	boolean debug = false;
	
	public CreditCardPayment() {
		
		cardReaderListener = new CardReaderListener() {

			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

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
				// TODO Auto-generated method stub
				
			}

			@Override
			public void cardSwiped(CardReader reader) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void cardDataRead(CardReader reader, CardData data) {
				// TODO Auto-generated method stub
				
			}};
			
			station.cardReader.register(cardReaderListener);
		
	}
	
	public void insertCard(Card card) throws DisabledException{
		
		station.cardReader.insert(card, pin); 
	}
	public void tapCard(Card card) throws IOException{
		
		station.cardReader.tap(card); 
	}
	public void swipeCard(Card card) throws IOException {
		
		station.cardReader.swipe(card, signature); 
	}

}
