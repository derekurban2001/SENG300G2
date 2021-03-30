package org.lsmr.testcases;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Test;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.ChipFailureException;
import org.lsmr.selfcheckout.MagneticStripeFailureException;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.usecases.CreditCardPayment;




public class CreditCardPaymentTest {


	//addCardDate: String number, String cardholder, Calendar expiry, String ccv, BigDecimal amount
	//Card: String type, String number, String cardholder, String cvv, String pin, boolean isTapEnabled,boolean hasChip
	@Test
	public void testTapValid() throws DisabledException, IOException{
		// Arrange
		Calendar testCalendar = Calendar.getInstance();
		testCalendar.set(Calendar.YEAR, 2030);
		testCalendar.set(Calendar.MONTH, 3);
		Card testCard = new Card("credit", "123321", "joy wang", "123", "321", true, true);
		CardIssuer testBank = new CardIssuer("CIBC");
		testBank.addCardData("123321", "joy wang", testCalendar, "123", new BigDecimal(100));
		CreditCardPayment TestCreditCardPayment = new CreditCardPayment();
		
		// Act
		TestCreditCardPayment.setAmountOwed(new BigDecimal(50));
		TestCreditCardPayment.tapCard(testCard, testBank);
		
		assertTrue(TestCreditCardPayment.getAmountOwed().compareTo(new BigDecimal(0)) == 0);			
	}
	
	@Test
	public void testInsertValid() throws DisabledException, IOException{
		// Arrange
		Calendar testCalendar = Calendar.getInstance();
		testCalendar.set(Calendar.YEAR, 2030);
		testCalendar.set(Calendar.MONTH, 3);
		Card testCard = new Card("credit", "123321", "joy wang", "123", "321", true, true);
		CardIssuer testBank = new CardIssuer("CIBC");
		testBank.addCardData("123321", "joy wang", testCalendar, "123", new BigDecimal(100));
		CreditCardPayment TestCreditCardPayment = new CreditCardPayment();
		
		// Act
		TestCreditCardPayment.setAmountOwed(new BigDecimal(50));
		TestCreditCardPayment.insertCard(testCard, "321", testBank);
		
		assertTrue(TestCreditCardPayment.getAmountOwed().compareTo(new BigDecimal(0)) == 0);			
	}
	
	@Test
	public void testSwipeValid() throws DisabledException, IOException{
		// Arrange
		Calendar testCalendar = Calendar.getInstance();
		testCalendar.set(Calendar.YEAR, 2030);
		testCalendar.set(Calendar.MONTH, 3);
		Card testCard = new Card("credit", "123321", "joy wang", "123", "321", true, true);
		CardIssuer testBank = new CardIssuer("CIBC");
		testBank.addCardData("123321", "joy wang", testCalendar, "123", new BigDecimal(100));
		CreditCardPayment TestCreditCardPayment = new CreditCardPayment();
		
		// Act
		TestCreditCardPayment.setAmountOwed(new BigDecimal(50));
		try{
		TestCreditCardPayment.swipeCard(testCard, testBank);
		}catch(MagneticStripeFailureException e)  {
			System.out.println("MagneticStripeFailureException");
		}
		assertTrue(TestCreditCardPayment.getAmountOwed().compareTo(new BigDecimal(0)) == 0);			
	}

	@Test 
	public void testTapUnable() throws IOException{
		// Arrange
		Calendar testCalendar = Calendar.getInstance();
		testCalendar.set(Calendar.YEAR, 2030);
		testCalendar.set(Calendar.MONTH, 3);
		Card testCard = new Card("credit", "123321", "joy wang", "123", "321", false, true);
		CardIssuer testBank = new CardIssuer("CIBC");
		testBank.addCardData("123321", "joy wang", testCalendar, "123", new BigDecimal(100));
		CreditCardPayment TestCreditCardPayment = new CreditCardPayment();
		
		// Act
		TestCreditCardPayment.setAmountOwed(new BigDecimal(50));
		TestCreditCardPayment.tapCard(testCard, testBank);
		//amount should remain the same
		assertTrue(TestCreditCardPayment.getAmountOwed().compareTo(new BigDecimal(50)) == 0);			
	}
	
	@Test (expected = ChipFailureException.class)
	public void testInsertUnable() throws IOException, DisabledException{
		// Arrange
		Calendar testCalendar = Calendar.getInstance();
		testCalendar.set(Calendar.YEAR, 2030);
		testCalendar.set(Calendar.MONTH, 3);
		Card testCard = new Card("credit", "123321", "joy wang", "123", "321", false, false);
		CardIssuer testBank = new CardIssuer("CIBC");
		testBank.addCardData("123321", "joy wang", testCalendar, "123", new BigDecimal(100));
		CreditCardPayment TestCreditCardPayment = new CreditCardPayment();
		
		// Act
		TestCreditCardPayment.setAmountOwed(new BigDecimal(50));
		TestCreditCardPayment.insertCard(testCard, "321", testBank);
		//amount should remain the same
		assertTrue(TestCreditCardPayment.getAmountOwed().compareTo(new BigDecimal(50)) == 0);			
	}
	

	
	
	
	
	@Test
	public void testBankAmountNotEnough() throws IOException, DisabledException{
		// Arrange
		Calendar testCalendar = Calendar.getInstance();
		testCalendar.set(Calendar.YEAR, 2030);
		testCalendar.set(Calendar.MONTH, 3);
		Card testCard = new Card("credit", "123321", "joy wang", "123", "321", false, true);
		CardIssuer testBank = new CardIssuer("CIBC");
		testBank.addCardData("123321", "joy wang", testCalendar, "123", new BigDecimal(10));
		CreditCardPayment TestCreditCardPayment = new CreditCardPayment();
		
		// Act
		TestCreditCardPayment.setAmountOwed(new BigDecimal(50));
		TestCreditCardPayment.tapCard(testCard, testBank);
		TestCreditCardPayment.insertCard(testCard, "321", testBank);
		try{
			TestCreditCardPayment.swipeCard(testCard, testBank);
		}catch(MagneticStripeFailureException e)  {
			System.out.println("MagneticStripeFailureException");
		}
		//amount should remain the same
		assertTrue(TestCreditCardPayment.getAmountOwed().compareTo(new BigDecimal(50)) == 0);			
	}
	
	@Test (expected =  SimulationException .class)
	public void testInvalidCCV() throws IOException, DisabledException{
		// Arrange
		Calendar testCalendar = Calendar.getInstance();
		testCalendar.set(Calendar.YEAR, 2030);
		testCalendar.set(Calendar.MONTH, 3);
		Card testCard = new Card("credit", "123321", "joy wang", "123", "321", true, true);
		CardIssuer testBank = new CardIssuer("CIBC");
		testBank.addCardData("123321", "joy wang", testCalendar, "123456", new BigDecimal(100));
		CreditCardPayment TestCreditCardPayment = new CreditCardPayment();
		
		// Act
		TestCreditCardPayment.setAmountOwed(new BigDecimal(50));
		TestCreditCardPayment.tapCard(testCard, testBank);
		TestCreditCardPayment.insertCard(testCard, "321", testBank);
		TestCreditCardPayment.swipeCard(testCard, testBank);
		//amount should remain the same
		assertTrue(TestCreditCardPayment.getAmountOwed().compareTo(new BigDecimal(50)) == 0);			
	}
	
	@Test (expected =  SimulationException .class)
	public void testCardExpired() throws IOException, DisabledException{
		// Arrange
		Calendar testCalendar = Calendar.getInstance();
		testCalendar.set(Calendar.YEAR, 2001);
		testCalendar.set(Calendar.MONTH, 3);
		Card testCard = new Card("credit", "123321", "joy wang", "123", "321", true, true);
		CardIssuer testBank = new CardIssuer("CIBC");
		testBank.addCardData("123321", "joy wang", testCalendar, "123", new BigDecimal(100));
		CreditCardPayment TestCreditCardPayment = new CreditCardPayment();
		
		// Act
		TestCreditCardPayment.setAmountOwed(new BigDecimal(50));
		TestCreditCardPayment.tapCard(testCard, testBank);
		TestCreditCardPayment.insertCard(testCard, "321", testBank);
		TestCreditCardPayment.swipeCard(testCard, testBank);
		//amount should remain the same
		assertTrue(TestCreditCardPayment.getAmountOwed().compareTo(new BigDecimal(50)) == 0);			
	}
	
	@Test (expected =  SimulationException .class)
	public void testCardholderNameInvalid() throws IOException, DisabledException{
		// Arrange
		Calendar testCalendar = Calendar.getInstance();
		testCalendar.set(Calendar.YEAR, 2001);
		testCalendar.set(Calendar.MONTH, 3);
		Card testCard = new Card("credit", "123321", "joy wang", "123", "321", true, true);
		CardIssuer testBank = new CardIssuer("CIBC");
		testBank.addCardData("123321", "jay wong", testCalendar, "123", new BigDecimal(100));
		CreditCardPayment TestCreditCardPayment = new CreditCardPayment();
		
		// Act
		TestCreditCardPayment.setAmountOwed(new BigDecimal(50));
		TestCreditCardPayment.tapCard(testCard, testBank);
		TestCreditCardPayment.insertCard(testCard, "321", testBank);
		TestCreditCardPayment.swipeCard(testCard, testBank);
		//amount should remain the same
		assertTrue(TestCreditCardPayment.getAmountOwed().compareTo(new BigDecimal(50)) == 0);			
	}
	
	@Test (expected =  SimulationException .class)
	public void testCardReaderHasCardInsert() throws IOException, DisabledException{
		// Arrange
		Calendar testCalendar = Calendar.getInstance();
		testCalendar.set(Calendar.YEAR, 2030);
		testCalendar.set(Calendar.MONTH, 3);
		Card testCard = new Card("credit", "123321", "joy wang", "123", "321", true, true);
		CardIssuer testBank = new CardIssuer("CIBC");
		testBank.addCardData("123321", "joy wang", testCalendar, "123", new BigDecimal(100));
		CreditCardPayment TestCreditCardPayment = new CreditCardPayment();
		
		TestCreditCardPayment.insertCard(testCard, "321", testBank);
		
		// Act
		TestCreditCardPayment.insertCard(testCard, "321", testBank);		
	}
	
	@Test 
	public void testCardRemoveAndInsertAgain() throws IOException, DisabledException{
		// Arrange
		Calendar testCalendar = Calendar.getInstance();
		testCalendar.set(Calendar.YEAR, 2030);
		testCalendar.set(Calendar.MONTH, 3);
		Card testCard = new Card("credit", "123321", "joy wang", "123", "321", true, true);
		CardIssuer testBank = new CardIssuer("CIBC");
		testBank.addCardData("123321", "joy wang", testCalendar, "123", new BigDecimal(1000));
		CreditCardPayment TestCreditCardPayment = new CreditCardPayment();
		
		//TestCreditCardPayment.setAmountOwed(new BigDecimal(0));
		TestCreditCardPayment.insertCard(testCard, "321", testBank);
		
		TestCreditCardPayment.removeCard();
		
		// Act
		TestCreditCardPayment.setAmountOwed(new BigDecimal(50));
		
		TestCreditCardPayment.insertCard(testCard, "321", testBank);
		
		assertTrue(TestCreditCardPayment.getAmountOwed().compareTo(new BigDecimal(0)) == 0);			
	}
	
	
	
	
	
	
	
}
