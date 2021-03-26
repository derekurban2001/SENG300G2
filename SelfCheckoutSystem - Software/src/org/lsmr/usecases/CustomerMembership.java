package org.lsmr.usecases;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Scanner;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;

public class CustomerMembership{

	private Card memberCard;
	private String pin;
	private SelfCheckoutStation station;
	private String membershipID;
	private Scanner sc = new Scanner(System.in);
	private CardData data;
	
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
		initListener();
	}
	
	CustomerMembership(SelfCheckoutStation station, Card memberCard, String pin){
		if(station == null) {
			throw new SimulationException("Station is null");
		}
		if(memberCard == null) {
			throw new SimulationException("Card is null");
		}
		if (pin == null){
			throw new SimulationException("Pin is null");
		}
		this.station = station;
		this.memberCard = memberCard;
		initListener();
	}
	
	public CardData getData() {
		return data;
	}

	public void setData(CardData data) {
		this.data = data;
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
				System.out.println("Card has been inserted");
			}

			@Override
			public void cardRemoved(CardReader reader) {
				// TODO Auto-generated method stub
				System.out.println("Card has been removed");
			}

			@Override
			public void cardTapped(CardReader reader) {
				// TODO Auto-generated method stub
				System.out.println("Card has been tapped");
			}

			@Override
			public void cardSwiped(CardReader reader) {
				// TODO Auto-generated method stub
				System.out.println("Card has been swiped");
			}

			@Override
			public void cardDataRead(CardReader reader, CardData data) {
				// TODO Auto-generated method stub
				System.out.println("Card data has been read");
			}
			
		});
	}
	
	public void checkCardReader() {
		if(this.station.cardReader.isDisabled()) {
			throw new SimulationException("The card reader is disabled");
		}
	}
	
	public void insertMemberCard(Card memberCard, String pin) throws IOException{
		checkCardReader();
		setData(station.cardReader.insert(memberCard, pin));
	}
	
	public void tapMemberCard(Card memberCard) throws IOException{
		checkCardReader();
		setData(station.cardReader.tap(memberCard));
	}
	
	public void swipeMemberCard(Card memberCard, BufferedImage signature) throws IOException{
		checkCardReader();
		setData(station.cardReader.swipe(memberCard, signature));
	}
	
	public void enterMemberNum() {
		if(data.getNumber() == null) {
			enterMemberNum();
		}
		System.out.println("Please input membership number:");
		if(sc.nextLine() == null) {
			System.out.println("That is not a valid membership number");
		}
	}
	
	public void removeCard() {
		station.cardReader.remove();
	}
	
	
}
