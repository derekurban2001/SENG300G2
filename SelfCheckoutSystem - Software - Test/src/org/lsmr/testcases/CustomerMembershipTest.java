package org.lsmr.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card;
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
	public void insertCardTest(){
		Card memberCard = new Card("Membership", "1234", "John Doe", "123", "1234", false, true);
		String pin = "1234";
		CustomerMembership membership = new CustomerMembership();
		try{
			membership.insertMemberCard(memberCard, pin);
		} catch(IOException e) {return;}
		
	}
	
	@Test
	public void swipeCardTest(){
		Card memberCard = new Card("Membership", "1234", "John Doe", "123", "1234", false, false);
		BufferedImage signature = null;
		CustomerMembership membership = new CustomerMembership();
		try{
			membership.swipeMemberCard(memberCard, signature);
		} catch(IOException e) {return;}
	}
	
	
	@Test
	public void tapCardTest() {
		Card memberCard = new Card("Membership", "1234", "John Doe", "123", "1234", true, false);
		CustomerMembership membership = new CustomerMembership();
		try {
			membership.tapMemberCard(memberCard);
		} catch (IOException e) {return;}
	}
	
	@Test
	public void removeTest() {
		Card memberCard = new Card("Membership", "1234", "John Doe", "123", "1234", false, false);
		CustomerMembership membership = new CustomerMembership();
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
		CustomerMembership membership = new CustomerMembership();
		membership.enterMemberNum();
		assertEquals("Please input membership number:", outStream.toString());
		streamTeardown();
	}
	
	@Test
	public void memberInDatabaseTest() {
		Card memberCard = new Card("Membership", "1234", "John Doe", "567", "8999", true, false);
		CustomerMembership membership = new CustomerMembership();
		membership.addMembership("1234");
		try {
			membership.tapMemberCard(memberCard);
		} catch (IOException e) {return;}
		assertEquals(true, membership.verifyMember(membership.getData().getNumber()));
	}
	
	@Test
	public void addMembertoDatabase() {
		CustomerMembership membership = new CustomerMembership();
		membership.addMembership("1234");
		assertEquals("1234", membership.memberDatabase.get(0));
	}

	@Test
	public void removeMemberFromDatabase() {
		CustomerMembership membership = new CustomerMembership();
		membership.addMembership("12345");
		membership.addMembership("56789");
		membership.addMembership("89999");
		membership.removeMembership("56789");
		assertEquals(false, membership.memberDatabase.contains("56789"));
	}
	
	@Test
    public final void testEnableDisable()
    {
        CustomerMembership membership = new CustomerMembership();
        membership.station.cardReader.enable();
        assertEquals(membership.station.baggingArea.isDisabled(), false);
               
    }
	
	@Test
    public void testScannerEnable() {
		CustomerMembership membership = new CustomerMembership();
		membership.finishedMembershipScan();
        // Test will fail if either the main scanner or the handheld scanner is enabled.
        assertFalse("Both scanners should be enabled.", membership.station.mainScanner.isDisabled() && membership.station.handheldScanner.isDisabled());
    }
	
	
	/**
	 * Method to set up the streams for the tests that rely on messages input/output
	 * 
	 * @param outStream ByteArrayOutputStream of the stream that is going out
 	 * @param inStream InputStream of the stream that is receiving 
	 */
	public void streamSetup(ByteArrayOutputStream outStream, InputStream inStream) {
		System.setOut(new PrintStream(outStream));
		System.setIn(inStream);
	}
	
	/**
	 * Method to restore the streams to the standard in/out streams
	 */
	@After
	public void streamTeardown() {
		System.setOut(standardOut);
		System.setIn(standardIn);
	}
}
