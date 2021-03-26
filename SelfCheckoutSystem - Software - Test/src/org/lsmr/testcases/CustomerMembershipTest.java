package org.lsmr.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.ChipFailureException;
import org.lsmr.selfcheckout.TapFailureException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.usecases.CustomerMembership;

public class CustomerMembershipTest {

	private SelfCheckoutStation station;
	private static PrintStream standardOut = System.out;
	private static InputStream standardIn = System.in;
	
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
		CustomerMembership membership = new CustomerMembership(station, memberCard, pin);
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
	}
	
	
	@Test
	public void tapCardTest() {
		Card memberCard = new Card("Membership", "1234", "John Doe", "123", "1234", true, false);
		CustomerMembership membership = new CustomerMembership(station, memberCard);
		try {
			membership.tapMemberCard(memberCard);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void removeTest() {
		Card memberCard = new Card("Membership", "1234", "John Doe", "123", "1234", false, false);
		CustomerMembership membership = new CustomerMembership(station, memberCard);
		membership.removeCard();
		assertEquals(false, membership.isCardInserted());
	}
	
	@Test
	public void enterMemberNumTest() {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ByteArrayInputStream inStream = new ByteArrayInputStream("1234".getBytes());
		streamSetup(outStream, inStream);
		System.setOut(new PrintStream(outStream));
		System.setIn(inStream);
		
		
		Card memberCard = new Card("Membership", "1234", "John Doe", "123", "1234", false, false);
		CustomerMembership membership = new CustomerMembership(station, memberCard);
		membership.enterMemberNum();
		assertEquals("Please input membership number:", outStream.toString());
		streamTeardown();
	}
	
	@Test
	public void enterMemberNumTestTwo() {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ByteArrayInputStream inStream = new ByteArrayInputStream("".getBytes());
		streamSetup(outStream, inStream);
		
		Card memberCard = new Card("Membership", "1234", "John Doe", "123", "1234", false, false);
		CustomerMembership membership = new CustomerMembership(station, memberCard);
		membership.enterMemberNum();
		assertEquals("That is not a valid membership number.", outStream.toString());
		streamTeardown();
	}
	
	
	public void streamSetup(ByteArrayOutputStream outStream, InputStream inStream) {
		System.setOut(new PrintStream(outStream));
		System.setIn(inStream);
	}
	
	@After
	public void streamTeardown() {
		System.setOut(standardOut);
		System.setIn(standardIn);
	}
}
