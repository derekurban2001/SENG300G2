package org.lsmr.usecases;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;

public class CustomerMembership {

	private Card memberCard;
	private SelfCheckoutStation station;
	
	//constructor
	CustomerMembership(SelfCheckoutStation station, Card memberCard){
		if(station == null) {
			throw new SimulationException("Station is null");
		}
		if(memberCard == null) {
			throw new SimulationException("Card is null");
		}
		this.station = station;
		this.memberCard = memberCard;
	}
	
	
	private void initListener() {
		station.cardReader.register(new CardReaderListener() {
			public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			}
			public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			}

			@Override
			public void cardInserted(CardReader reader) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void cardRemoved(CardReader reader) {
				// TODO Auto-generated method stub
				
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
				
			}
			
		});
	}
	
	public void scanCard(Card memberCard) {
		
	}
}
