package org.lsmr.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.junit.*;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.usecases.ReturnChange;

public class ReturnChangeTest {
	
	SelfCheckoutStation station;
	Currency currency;
	int[] banknoteDenominations;
	BigDecimal[] coinDenominations;
	
	/*
	 * Sets up the SelfCheckoutStation used by the test class
	 */
	@Before
	public final void setup() {
		currency = Currency.getInstance(Locale.CANADA);
		banknoteDenominations = new int[]{100, 50, 20, 10, 5};
		coinDenominations = new BigDecimal[]{cBigDecimal(2.0), cBigDecimal(1.0), cBigDecimal(0.25), cBigDecimal(0.10), cBigDecimal(0.05)};
		int scaleMaximumWeight = 10000;
		int scaleSensitivity = 1;
		
		this.station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, scaleMaximumWeight, scaleSensitivity);
	}
	
	/*
	 * Tests valid parameters of ReturnChange.java
	 */
	@Test
	public final void testParameters() {
		printHeader("testParameters");
		try {
			ReturnChange change = new ReturnChange(null);
			fail("Expected SimulationException when constructor parameter is null");
		}
		catch(SimulationException ex){/*Do nothing*/}
		
		try {
			ReturnChange change = new ReturnChange(station);
			change.calculateChange(-0.00001);
			fail("Expected SimulationException when constructor parameter is null");
		}
		catch(SimulationException ex){/*Do nothing*/}
		catch(OverloadException ex) {/*Do nothing*/}
	}
	
	/*
	 * Tests if there isn't enough coin change to return to customer
	 */
	@Test
	public final void testCantReturnChange1() {
		printHeader("testCantReturnChange1");
		ReturnChange change = new ReturnChange(station);
		loadDispenser(10);
		loadDispenser(5);
		try {
			change.calculateChange(15.05);
			fail("Expected SimulationException if change cannot be returned.");
		}
		catch(Exception ex) {};
	}
	
	/*
	 * Tests if there isn't enough banknote change to return to customer
	 */
	@Test
	public final void testCantReturnChange2() {
		printHeader("testCantReturnChange2");
		ReturnChange change = new ReturnChange(station);
		loadDispenser(BigDecimal.valueOf(0.25));
		try {
			change.calculateChange(5.10);
			fail("Expected SimulationException if change cannot be returned.");
		}
		catch(Exception ex) {};
	}
	
	/*
	 * Tests if there no change is returned when change amount is 0
	 */
	@Test
	public final void testChangeOfZero() {
		printHeader("testChangeOfZero");
		ReturnChange change = new ReturnChange(station);
		loadAllDispensers();
		try {
			change.calculateChange(0.0);
		}
		catch(Exception ex) {ex.printStackTrace(); fail(ex.toString());};
		assertEquals(0, change.pendingChange());
	}
	
	/*
	 * Tests if the proper amount of bank notes and coins is returned
	 */
	@Test
	public final void testProperChange1() {
		printHeader("testProperChange1");
		ReturnChange change = new ReturnChange(station);
		loadAllDispensers();
		double sum = 0;
		try {
			change.calculateChange(17.48);
			change.releaseCoinChange();
			while(change.pendingChange() > 0) {
				change.releaseBanknoteChange();
				Banknote banknote = change.takeBanknote();
				sum += banknote.getValue();
			}
			for(Coin coin : change.takeCoins())
				sum += coin.getValue().doubleValue();
		}
		catch(Exception ex) {ex.printStackTrace(); fail(ex.toString());};
		assertEquals(17.45, sum, 0.0001);
	}
	
	/*
	 * Tests if the proper amount of bank notes and coins is returned
	 */
	@Test
	public final void testProperChange2() {
		printHeader("testProperChange2");
		ReturnChange change = new ReturnChange(station);
		loadAllDispensers();
		double sum = 0;
		try {
			change.calculateChange(878.69);
			change.releaseCoinChange();
			while(change.pendingChange() > 0) {
				change.releaseBanknoteChange();
				Banknote banknote = change.takeBanknote();
				sum += banknote.getValue();
			}
			for(Coin coin : change.takeCoins())
				sum += coin.getValue().doubleValue();
		}
		catch(Exception ex) {ex.printStackTrace(); fail(ex.toString());};
		assertEquals(878.65, sum, 0.0001);
	}
	
	/*
	 * Tests if the proper amount of bank note change is returned
	 */
	@Test
	public final void testBanknoteChange1() {
		printHeader("testBanknoteChange1");
		ReturnChange change = new ReturnChange(station);
		loadBanknoteDispensers();
		int sum = 0;
		try {
			change.calculateChange(385.00);
			while(change.pendingChange() > 0) {
				change.releaseBanknoteChange();
				Banknote banknote = change.takeBanknote();
				sum += banknote.getValue();
			}
			assertEquals(385, sum);
		}
		catch(Exception ex) {ex.printStackTrace(); fail(ex.toString());}
	}
	
	/*
	 * Tests if the proper amount of bank note change is returned
	 */
	@Test
	public final void testBanknoteChange2() {
		printHeader("testBanknoteChange2");
		ReturnChange change = new ReturnChange(station);
		loadDispenser(5);
		loadDispenser(100);
		int sum = 0;
		try {
			change.calculateChange(305.00);
			while(change.pendingChange() > 0) {
				change.releaseBanknoteChange();
				Banknote banknote = change.takeBanknote();
				sum += banknote.getValue();
			}
			assertEquals(305, sum);
		}
		catch(Exception ex) {ex.printStackTrace(); fail(ex.toString());}
	}
	
	/*
	 * Tests if the proper amount of bank note change is returned
	 */
	@Test
	public final void testBanknoteChange3() {
		printHeader("testBanknoteChange3");
		ReturnChange change = new ReturnChange(station);
		loadDispenser(5);
		int sum = 0;
		try {
			change.calculateChange(15.00);
			while(change.pendingChange() > 0) {
				change.releaseBanknoteChange();
				Banknote banknote = change.takeBanknote();
				sum += banknote.getValue();
			}
			assertEquals(15, sum);
		}
		catch(Exception ex) {ex.printStackTrace(); fail(ex.toString());}
	}
	
	/*
	 * Tests if the proper amount of coin change is returned
	 */
	@Test
	public final void testCoinChange1() {
		printHeader("testCoinChange1");
		ReturnChange change = new ReturnChange(station);
		loadDispenser(BigDecimal.valueOf(0.10));
		double sum = 0;
		try {
			change.calculateChange(0.70);
			change.releaseCoinChange();
			
			for(Coin coin : change.takeCoins())
				sum+= coin.getValue().doubleValue();
			
			assertEquals(0.70, sum, 0.00001);
		}
		catch(Exception ex) {ex.printStackTrace(); fail(ex.toString());}
	}
	
	/*
	 * Tests if the proper amount of coin change is returned
	 */
	@Test
	public final void testCoinChange2() {
		printHeader("testCoinChange2");
		ReturnChange change = new ReturnChange(station);
		loadDispenser(BigDecimal.valueOf(0.05));
		loadDispenser(BigDecimal.valueOf(0.25));
		double sum = 0;
		try {
			change.calculateChange(0.90);
			change.releaseCoinChange();
			
			for(Coin coin : change.takeCoins())
				sum+= coin.getValue().doubleValue();
			
			assertEquals(0.90, sum, 0.00001);
		}
		catch(Exception ex) {ex.printStackTrace(); fail(ex.toString());}
	}
	
	/*
	 * Tests if the proper amount of coin change is returned
	 */
	@Test
	public final void testCoinChange3() {
		printHeader("testCoinChange3");
		ReturnChange change = new ReturnChange(station);
		loadDispenser(BigDecimal.valueOf(0.10));
		double sum = 0;
		try {
			change.calculateChange(0.70);
			change.releaseCoinChange();
			
			for(Coin coin : change.takeCoins())
				sum+= coin.getValue().doubleValue();
			
			assertEquals(0.70, sum, 0.00001);
		}
		catch(Exception ex) {ex.printStackTrace(); fail(ex.toString());}
	}
	
	/*
	 * Tests if the proper bank notes are returned
	 */
	@Test
	public final void testCorrectBanknotes() {
		printHeader("testCorrectBanknotes");
		ReturnChange change = new ReturnChange(station);
		ArrayList<Banknote> banknoteList = new ArrayList<Banknote>();
		ArrayList<Integer> checkList = new ArrayList<Integer>(Arrays.asList(
		50, 50, 50, 20, 5, 5, 5));
		loadDispenser(5);
		loadDispenser(20);
		loadDispenser(50);
		
		try {
			change.calculateChange(185.00);
			while(change.pendingChange() > 0) {
				change.releaseBanknoteChange();
				banknoteList.add(change.takeBanknote());
			}
			
			for(Banknote banknote : banknoteList)
				if(checkList.contains(Integer.valueOf(banknote.getValue()))){
					checkList.remove(Integer.valueOf(banknote.getValue()));
				}
				else if(checkList.size() == 0)
					fail("Did not recieve 3 * $50, 1 * $20, & 3 * $5");
			
			if(checkList.size() != 0)
				fail("Did not recieve 3 * $50, 1 * $20, & 3 * $5");
		}
		catch(Exception ex) {ex.printStackTrace(); fail(ex.toString());}
	}
	
	/*
	 * Tests if the proper coins are returned
	 */
	@Test
	public final void testCorrectCoins() {
		printHeader("testCorrectCoins");
		ReturnChange change = new ReturnChange(station);
		ArrayList<Double> checkList = new ArrayList<Double>(Arrays.asList(
		1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 0.10, 0.10, 0.10, 0.10, 0.10, 0.10, 0.10, 0.05));
		loadDispenser(BigDecimal.valueOf(0.10));
		loadDispenser(BigDecimal.valueOf(0.05));
		loadDispenser(BigDecimal.valueOf(1.00));
		
		try {
			change.calculateChange(6.75);
			change.releaseCoinChange();
			
			for(Coin coin : change.takeCoins())
				if(checkList.contains(coin.getValue().doubleValue())){
					checkList.remove(coin.getValue().doubleValue());
				}
				else if(checkList.size() == 0)
					fail("Did not recieve 6 * $1.00, 7 * $0.10, & 1 * $0.05");
			
			if(checkList.size() != 0)
				fail("Did not recieve 6 * $1.00, 7 * $0.10, & 1 * $0.05");
		}
		catch(Exception ex) {ex.printStackTrace(); fail(ex.toString());}
	}
	
	/*
	 * Tests if the bank note return slot is full
	 */
	@Test
	public final void testBanknoteSlotFull() {
		printHeader("testBanknoteSlotFull");
		ReturnChange change = new ReturnChange(station);
		loadAllDispensers();
		try {
			change.calculateChange(15.00);
			
			change.releaseBanknoteChange();
			change.releaseBanknoteChange();
			
			assertEquals(1, change.pendingChange());
		}
		catch(Exception ex) {ex.printStackTrace(); fail(ex.toString());}
	}

	/*
	 * Tests if the coin return tray is full
	 */
	@Test
	public final void testCoinTrayFull() {
		printHeader("testCoinTrayFull");
		ReturnChange change = new ReturnChange(station);
		loadCoinDispensers();
		try {
			change.calculateChange(50.00);
			
			change.releaseCoinChange();
			
			assertEquals(5, change.pendingChange());
		}
		catch(Exception ex) {ex.printStackTrace(); fail(ex.toString());}
	}

	/*
	 * Tests if you try and take two banknotes when one is returned
	 */
	@Test
	public final void testDoubleTakeBanknote() {
		printHeader("testBanknoteSlotFull");
		ReturnChange change = new ReturnChange(station);
		loadAllDispensers();
		try {
			change.calculateChange(15.00);
			
			change.releaseBanknoteChange();
			change.takeBanknote();
			change.takeBanknote();
			
			assertEquals(1, change.pendingChange());
		}
		catch(Exception ex) {ex.printStackTrace(); fail(ex.toString());}
	}
	
	/*
	 * Print header helper method
	 */
	private final void printHeader(String testName) {
		System.out.println("\n\n******************************");
		System.out.println("Running Test: "+testName);
		System.out.println("******************************");
	}
	
	/*
	 * Loads the appropriate coin dispenser based of a valid denomination
	 */
	private final void loadDispenser(BigDecimal denomination){
		int size = 25;
		Coin[] coins = new Coin[size];
		for(int i = 0; i < size; i++)
			coins[i] = new Coin(denomination, currency);
		try {
			station.coinDispensers.get(denomination).load(coins);
		}
		catch(OverloadException ex) {/*Do nothing*/}
	}
	
	/*
	 * Loads the appropriate bank note dispenser based of a valid denomination
	 */
	private final void loadDispenser(int denomination){
		int size = 25;
		Banknote[] banknotes = new Banknote[size];
		for(int i = 0; i < size; i++)
			banknotes[i] = new Banknote(denomination, currency);
		
		try {
			station.banknoteDispensers.get(denomination).load(banknotes);
		}
		catch(OverloadException ex) {/*Do nothing*/}
	}
	
	/*
	 * Loads all the valid coin dispensers
	 */
	private final void loadCoinDispensers() {
		for(BigDecimal denomination : coinDenominations)
			loadDispenser(denomination);
	}
	
	/*
	 * Loads all the valid bank note dispensers
	 */
	private final void loadBanknoteDispensers() {
		for(int denomination : banknoteDenominations)
			loadDispenser(denomination);
	}
	
	/*
	 * Loads all the dispensers
	 */
	private final void loadAllDispensers(){
		loadCoinDispensers();
		loadBanknoteDispensers();
	}
	
	/*
	 * Create Big Decimal helper class
	 */
	private final BigDecimal cBigDecimal(double value) {
		return BigDecimal.valueOf(value);
	}

}
