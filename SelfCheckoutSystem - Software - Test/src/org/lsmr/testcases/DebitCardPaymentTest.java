package org.lsmr.testcases;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.ChipFailureException;
import org.lsmr.selfcheckout.InvalidPINException;
import org.lsmr.selfcheckout.TapFailureException;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.usecases.DebitCardPayment;
import org.lsmr.selfcheckout.devices.SimulationException;

public class DebitCardPaymentTest {
	static CardIssuer testBank;
	static Calendar calendar;
	static DebitCardPayment testUseCase;
	static Card testCard;
	
	@Before
	public void setUp() {
		// initialize card issuer
		testBank = new CardIssuer("TD Bank"); 
		// initialize expire date
		calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2077);
		calendar.set(Calendar.MONTH, 9);
		// initialize card
		testCard = new Card("debit", "123", "Mike Wazowski", "333", "123", true, true);
		// add card info to card issuer
		testBank.addCardData("123", "Mike Wazowski", calendar, "333", new BigDecimal(50));
		// initialize system
		testUseCase = new DebitCardPayment();
	}
	
	/**
	 * Read invalid card type
	 * @throws IOException
	 * @throws DisabledException
	 */
	@Test
	public void testInValidCard1() throws IOException, DisabledException {
		testCard = new Card("credit", "123", "Mike Wazowski", "333", "123", true, true);
		testUseCase.setAmountOwed(new BigDecimal(100));
		
		testUseCase.tapCard(testCard, testBank);
		testUseCase.swipeCard(testCard, testBank);
		testUseCase.insertCard(testCard, "123", testBank);
		// invalid card no change
		assertTrue(testUseCase.getAmountOwed().compareTo(new BigDecimal(100)) == 0);
	}
	
	/**
	 * Read invalid card number not in database
	 * @throws IOException
	 * @throws DisabledException
	 */
	@Test
	public void testInValidCard2() throws IOException, DisabledException {
		testCard = new Card("debit", "1235", "Mike Wazowski", "333", "123", true, true);
		testUseCase.setAmountOwed(new BigDecimal(100));

		testUseCase.tapCard(testCard, testBank);
		testUseCase.swipeCard(testCard, testBank);
		testUseCase.insertCard(testCard, "123", testBank);
		// invalid card no change
		assertTrue(testUseCase.getAmountOwed().compareTo(new BigDecimal(100)) == 0);
	}
	
	/**
	 * Tap disable tap card
	 * @throws IOException
	 */
	@Test
	public void testInValidCard3() throws IOException {
		testCard = new Card("debit", "123", "Mike Wazowski", "333", "123", false, true);
		testUseCase.setAmountOwed(new BigDecimal(100));

		testUseCase.tapCard(testCard, testBank);
		// invalid card no change
		assertTrue(testUseCase.getAmountOwed().compareTo(new BigDecimal(100)) == 0);
	}
	
	/**
	 * Insert card with no chip
	 * @throws IOException
	 * @throws DisabledException 
	 */
	@Test (expected = ChipFailureException.class)
	public void testInValidCard4() throws IOException, DisabledException {
		testCard = new Card("debit", "123", "Mike Wazowski", "333", "123", true, false);
		testUseCase.setAmountOwed(new BigDecimal(100));

		testUseCase.insertCard(testCard, "123", testBank);
		// invalid card no change
		assertTrue(testUseCase.getAmountOwed().compareTo(new BigDecimal(100)) == 0);
	}
	
	/**
	 * Try to insert non-empty card reader
	 * @throws IOException
	 * @throws DisabledException 
	 */
	@Test (expected = SimulationException.class)
	public void testInValidCard5() throws IOException, DisabledException {
		testCard = new Card("debit", "123", "Mike Wazowski", "333", "123", true, true);
		testUseCase.setAmountOwed(new BigDecimal(100));

		testUseCase.insertCard(testCard, "123", testBank);
		testUseCase.insertCard(testCard, "123", testBank);
		// invalid card no change
		assertTrue(testUseCase.getAmountOwed().compareTo(new BigDecimal(100)) == 0);
	}
	
	/**
	 * Insert card with incorrect pin
	 * @throws IOException
	 * @throws DisabledException 
	 */
	@Test (expected = InvalidPINException.class)
	public void testFailed1() throws IOException, DisabledException {
		testUseCase.setAmountOwed(new BigDecimal(100));

		testUseCase.insertCard(testCard, "1234", testBank);
		// invalid card no change
		assertTrue(testUseCase.getAmountOwed().compareTo(new BigDecimal(100)) == 0);
	}
	
	/**
	 * Pay with blocked card.
	 * @throws IOException
	 * @throws DisabledException 
	 */
	@Test
	public void testFailed2() throws IOException, DisabledException {
		testUseCase.setAmountOwed(new BigDecimal(100));
		testBank.block("123");
		
		testUseCase.insertCard(testCard, "123", testBank);
		testUseCase.tapCard(testCard, testBank);
		testUseCase.swipeCard(testCard, testBank);
		// invalid card no change
		assertTrue(testUseCase.getAmountOwed().compareTo(new BigDecimal(100)) == 0);
	}
	
	/**
	 * Insufficient amount.
	 * @throws IOException
	 * @throws DisabledException 
	 */
	@Test
	public void testFailed3() throws IOException, DisabledException {
		testUseCase.setAmountOwed(new BigDecimal(60));
		
		testUseCase.insertCard(testCard, "123", testBank);
		testUseCase.tapCard(testCard, testBank);
		testUseCase.swipeCard(testCard, testBank);
		// invalid card no change
		assertTrue(testUseCase.getAmountOwed().compareTo(new BigDecimal(60)) == 0);
	}
	
	/**
	 * Tap over 100 payment
	 * @throws IOException
	 * @throws DisabledException 
	 */
	@Test (expected = TapFailureException.class)
	public void testFailed4() throws IOException {
		testUseCase.setAmountOwed(new BigDecimal(200));
		
		testUseCase.tapCard(testCard, testBank);
		// invalid card no change
		assertTrue(testUseCase.getAmountOwed().compareTo(new BigDecimal(200)) == 0);
	}
	
	/**
	 * Valid tap payment
	 * @throws IOException
	 */
	@Test 
	public void testSuccesful1() throws IOException {
		testUseCase.setAmountOwed(new BigDecimal(50));
		
		testUseCase.tapCard(testCard, testBank);
		// valid card, amountOwed = 0;
		assertTrue(testUseCase.getAmountOwed().compareTo(new BigDecimal(0)) == 0);
	}
	
	/**
	 * Valid swipe payment
	 * @throws IOException
	 */
	@Test 
	public void testSuccesful2() throws IOException {
		testUseCase.setAmountOwed(new BigDecimal(50));
		
		testUseCase.swipeCard(testCard, testBank);
		// valid card, amountOwed = 0;
		assertTrue(testUseCase.getAmountOwed().compareTo(new BigDecimal(0)) == 0);
	}
	
	/**
	 * Valid insert payment
	 * @throws IOException
	 * @throws DisabledException 
	 */
	@Test 
	public void testSuccesful3() throws IOException, DisabledException {
		testUseCase.setAmountOwed(new BigDecimal(50));
		
		testUseCase.insertCard(testCard, "123", testBank);
		// valid card, amountOwed = 0;
		assertTrue(testUseCase.getAmountOwed().compareTo(new BigDecimal(0)) == 0);
	}
	
	/**
	 * Insert invalid card, remove then insert valid card
	 * @throws IOException
	 * @throws DisabledException 
	 */
	@Test 
	public void testSuccesful4() throws IOException, DisabledException {
		testUseCase.setAmountOwed(new BigDecimal(50));
		Card testCard2 = new Card("credit", "123", "Mike Wazowski", "333", "123", true, true);
		
		testUseCase.insertCard(testCard2, "123", testBank);
		testUseCase.removeCard();
		testUseCase.insertCard(testCard, "123", testBank);
		// invalid card no change
		assertTrue(testUseCase.getAmountOwed().compareTo(new BigDecimal(0)) == 0);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}