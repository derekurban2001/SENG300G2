package org.lsmr.usecases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.ChipFailureException;
import org.lsmr.selfcheckout.TapFailureException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;

public class CustomerMembershipTest {

	private SelfCheckoutStation station;
	
	@Before
	public void setup() {
		
		Currency currency = Currency.getInstance(Locale.CANADA);
		int[] banknoteDenominations = {5, 10, 20, 50, 100};
		BigDecimal[] coinDenominations = {	BigDecimal.valueOf(0.05), 
											BigDecimal.valueOf(0.10),
											BigDecimal.valueOf(0.25),
											BigDecimal.valueOf(0.50), 
											BigDecimal.valueOf(1.00), 
											BigDecimal.valueOf(2.00)};
		int scaleMaximumWeight = (25*1000);	//Scale maximum in grams
		int scaleSensitivity = (15);		//Scale sensitivity in grams
		
		station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, scaleMaximumWeight, scaleSensitivity);
	}
	
	
	@Test
	public void testConstructorOne() {
		Card memberCard = null;
		try{
			CustomerMembership membership = new CustomerMembership(station, memberCard);
		}
		catch(SimulationException e) {return;}
		fail("Card is null and should throw a SimulationException");
	}
	
	@Test
	public void testConstructorTwo() {
		station = null;
		Card memberCard = new Card("Membership", "1234", "John Doe", "123", "1234", false, false);
		try{
			CustomerMembership membership = new CustomerMembership(station, memberCard);
		}
		catch(SimulationException e) {return;}
		fail("Station is null and should throw a SimulationException");
	}
	
	@Test
	public void testConstructorThree() {
		Card memberCard = new Card("Membership", "1234", "John Doe", "123", "1234", false, false);
		String pin = null;
		try{
			CustomerMembership membership = new CustomerMembership(station, memberCard, pin);
		}
		catch(SimulationException e) {return;}
		fail("Pin is null and should throw a SimulationException");
	}
	
	@Test
	public void testConstructorFour() {
		Card memberCard = null;
		String pin = "1234";
		try{
			CustomerMembership membership = new CustomerMembership(station, memberCard, pin);
		}
		catch(SimulationException e) {return;}
		fail("Card is null and should throw a SimulationException");
	}
	
	@Test
	public void testConstructorFive() {
		station = null;
		Card memberCard = new Card("Membership", "1234", "John Doe", "123", "1234", false, false);
		String pin = "1234";
		try{
			CustomerMembership membership = new CustomerMembership(station, memberCard, pin);
		}
		catch(SimulationException e) {return;}
		fail("Station is null and should throw a SimulationException");
	}
	
	@Test
	public void cardReaderTest(){
		Card memberCard = new Card("Membership", "1234", "John Doe", "123", "1234", false, false);
		CustomerMembership membership = new CustomerMembership(station, memberCard);
		station.cardReader.disable();
		try{
			membership.checkCardReader();
		} catch(SimulationException e) {return;}
		fail("Expected SimulationException");
	}
	
	
	@Test
	public void insertCardTestNoChip(){
		Card memberCard = new Card("Membership", "1234", "John Doe", "123", "1234", false, false);
		String pin = "1234";
		CustomerMembership membership = new CustomerMembership(station, memberCard, pin);
		try{
			membership.insertMemberCard(memberCard, pin);
		} catch(IOException e) {return;}
		fail("ChipFailureException expected");
	}
	
	@Test
	public void insertCardTest(){
		Card memberCard = new Card("Membership", "1234", "John Doe", "123", "1234", false, true);
		String pin = "1234";
		try{
			membership.insertMemberCard(memberCard, pin);
		} catch(IOException e) {return;}

	}
	
	@Test
	public void swipeCardTest(){
		Card memberCard = new Card("Membership", "1234", "John Doe", "123", "1234", false, false);
		BufferedImage signature = null;
		CustomerMembership membership = new CustomerMembership(station, memberCard);
		try{
			membership.swipeMemberCard(memberCard, signature);
		} catch(IOException e) {return;}
		fail("BlockedCardException expected");
	}
	
	@Test
	public void tapDisabledCardTest() {
		Card memberCard = new Card("Membership", "1234", "John Doe", "123", "1234", false, false);
		CustomerMembership membership = new CustomerMembership(station, memberCard);
		try{
			membership.tapMemberCard(memberCard);
		} catch(IOException e) {return;}
		fail("TapFailureException expected");
	}
	
}
