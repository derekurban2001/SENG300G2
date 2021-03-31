package org.lsmr.usecases;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;

public class CustomerMembership extends UseCases{
	private String membershipID;
	private Scanner sc = new Scanner(System.in);
	public boolean cardInserted;
	private CardData data;
	public ArrayList<String> memberDatabase;
	private boolean debug = false;
	
	/**
	 * Constructor for the class
	 * 
	 * @param station SelfCheckoutStation that is currently being used
	 * @param memberCard Card for the membership of the user
	 */
	public CustomerMembership(){
		memberDatabase = new ArrayList<String>();
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
				if(debug) System.out.println("Card has been inserted");
			}

			@Override
			public void cardRemoved(CardReader reader) {
				// TODO Auto-generated method stub
				if(debug) System.out.println("Card has been removed");
			}

			@Override
			public void cardTapped(CardReader reader) {
				// TODO Auto-generated method stub
				if(debug) System.out.println("Card has been tapped");
			}

			@Override
			public void cardSwiped(CardReader reader) {
				// TODO Auto-generated method stub
				if(debug) System.out.println("Card has been swiped");
			}

			// how do I access this if they manually input it in?
			@Override
			public void cardDataRead(CardReader reader, CardData data) {
				// TODO Auto-generated method stub
				if(data.getType().toLowerCase().indexOf("membership") == -1) {
					if(debug) System.out.println("Invalid type of card");
				}
				else {
					// check if the member is in the database
					if(verifyMember(data.getNumber())) {
						if(debug) System.out.println("Member found!");
					}
					else {
						if(debug) System.out.println("Member not found!");
					}
				}
			}
		});
	}
	
	
	/**
	 * Method to insert the membership card
	 * 
	 * @param memberCard Card for the membership of the user
	 * @param pin String for the pin of the membership card
	 * @throws IOException 
	 */
	public void insertMemberCard(Card memberCard, String pin) throws IOException{
		setData(station.cardReader.insert(memberCard, pin));
	}
	
	/**
	 * Method to tap membership card
	 * 
	 * @param memberCard Card for the membership of the user
	 * @throws IOException
	 */
	public void tapMemberCard(Card memberCard) throws IOException{
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
		setData(station.cardReader.swipe(memberCard, signature));
	}
	
	/**
	 * Method that prompts the user to input their membership number by hand
	 * Saves the membership number regardless if it is null or not
	 */
	public void enterMemberNum() {
		System.out.print("Please input membership number:");
		setMembershipID(sc.nextLine());
//		if(sc.nextLine() == null) {
//			System.out.println("That is not a valid membership number.");
//		}
//		else {
//			setMembershipID(sc.nextLine());
//		}
	}
	
	/**
	 * Method to add a membership number into the membership database
	 * @param newMember String of the new membership number
	 */
	public void addMembership(String newMember){
		memberDatabase.add(newMember);
	}
	
//	public void removeMembership
	
	
	
	/**
	 * Method to verify if the member is in the database
	 * @param member String of the member we are looking for
	 * @return boolean which evaluates to true if the member is in the database
	 */
	public boolean verifyMember(String member) {
		if(this.memberDatabase.contains(member)) {
			return true;
		}
		else {
			return false;
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
