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

public class CustomerMembership {
	private Card memberCard;
	private String pin;
	private SelfCheckoutStation station;
	private String membershipID;
	private Scanner sc = new Scanner(System.in);
	public boolean cardInserted;
	private CardData data;
	
	/**
	 * Constructor for the class
	 * 
	 * @param station SelfCheckoutStation that is currently being used
	 * @param memberCard Card for the membership of the user
	 */
	public CustomerMembership(SelfCheckoutStation station, Card memberCard){
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
	
	/**
	 * Constructor for the class, includes pin for the memberCard
	 * 
	 * @param station SelfCheckoutStation that is currently being used
	 * @param memberCard Card for the membership of the user
	 * @param pin String of the pin for the memberCard
	 */
	public CustomerMembership(SelfCheckoutStation station, Card memberCard, String pin){
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
	
	/**
	 * Method to initialize the listeners
	 * Changed the response for the methods to print messages
	 */
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
	
	/**
	 * Method to check the card reader if disabled
	 */
	public void checkCardReader() {
		if(this.station.cardReader.isDisabled()) {
			throw new SimulationException("The card reader is disabled");
		}
	}
	
	/**
	 * Method to insert the membership card
	 * 
	 * @param memberCard Card for the membership of the user
	 * @param pin String for the pin of the membership card
	 * @throws IOException 
	 */
	public void insertMemberCard(Card memberCard, String pin) throws IOException{
		checkCardReader();
		setData(station.cardReader.insert(memberCard, pin));
	}
	
	/**
	 * Method to tap membership card
	 * 
	 * @param memberCard Card for the membership of the user
	 * @throws IOException
	 */
	public void tapMemberCard(Card memberCard) throws IOException{
		checkCardReader();
		setData(station.cardReader.tap(memberCard));
	}
	
	/**
	 * Method to swipe membership card
	 * 
	 * @param memberCard Card for the membership of the user
	 * @param signature BufferedImage of the signature of the user
	 * @throws IOException
	 */
	public void swipeMemberCard(Card memberCard, BufferedImage signature) throws IOException{
		checkCardReader();
		setData(station.cardReader.swipe(memberCard, signature));
	}
	
	/**
	 * Method that prompts the user to input their membership number by hand
	 * Saves the membership number
	 */
	public void enterMemberNum() {
		System.out.print("Please input membership number:");
		if(sc.nextLine() == " ") {
			System.out.println("That is not a valid membership number.");
		}
		else {
			setMembershipID(sc.nextLine());
		}
	}
	
	/*
	 * Getters and Setters 
	 */
	public void removeCard() {
		station.cardReader.remove();
		setCardInserted(false);
	}
	
	public boolean isCardInserted() {
		return cardInserted;
	}

	public void setCardInserted(boolean cardInserted) {
		this.cardInserted = cardInserted;
	}

	public String getMembershipID() {
		return membershipID;
	}

	public void setMembershipID(String membershipID) {
		this.membershipID = membershipID;
	}

	public CardData getData() {
		return data;
	}

	public void setData(CardData data) {
		this.data = data;
	}
}
