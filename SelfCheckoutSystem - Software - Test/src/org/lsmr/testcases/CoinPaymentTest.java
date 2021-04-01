package org.lsmr.testcases;


import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Test;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.usecases.CoinPayment;

public class CoinPaymentTest {
	@Test
	public void testValid() {
		// Arrange
		Coin TestCoin = new Coin(new BigDecimal(1.00), Currency.getInstance("CAD"));
		CoinPayment TestCoinPayment = new CoinPayment();
		TestCoinPayment.setAmountOwed(new BigDecimal(2));
		
		try {
			TestCoinPayment.insertCoin(TestCoin);
		} catch (DisabledException e) {
			fail("Should not throw " + e);
		}
		// Assert
		assertEquals(0,TestCoinPayment.getAmountOwed().compareTo(new BigDecimal(1.00)));	
	}
	
	@Test
	public void testAmountOwedCalculation() {
		// Arrange
		Coin TestCoin = new Coin(new BigDecimal(2.00), Currency.getInstance("CAD"));
		CoinPayment TestCoinPayment = new CoinPayment();
		TestCoinPayment.setAmountOwed(new BigDecimal(10 ));
		
		try {
			TestCoinPayment.insertCoin(TestCoin);
			TestCoinPayment.insertCoin(TestCoin);
			TestCoinPayment.insertCoin(TestCoin);
		} catch (DisabledException e) {
			fail("Should not throw " + e);
		}
		// Assert
		assertEquals(0, TestCoinPayment.getAmountOwed().compareTo(new BigDecimal(4.00)));	
	}
	
	@Test (expected = DisabledException.class)
	public void testAmountOwedLowerThanZero() throws DisabledException  {
		// Arrange
		Coin TestCoin = new Coin(new BigDecimal(1.00), Currency.getInstance("CAD"));
		CoinPayment TestCoinPayment = new CoinPayment();
		TestCoinPayment.setAmountOwed(new BigDecimal(0 ));
		
		TestCoinPayment.insertCoin(TestCoin);
		TestCoinPayment.insertCoin(TestCoin);
		TestCoinPayment.insertCoin(TestCoin);
	}

	@Test (expected = DisabledException.class)
	public void testCoinSlotDisable() throws DisabledException {
		// Arrange
		Coin TestCoin = new Coin(new BigDecimal(0.25), Currency.getInstance("CAD"));
		CoinPayment TestCoinPayment = new CoinPayment();
		
		// Act
		TestCoinPayment.station.coinSlot.disable();
		TestCoinPayment.setAmountOwed(new BigDecimal(100));
		
		// Inserting coin
		TestCoinPayment.insertCoin(TestCoin);
		
	}
	@Test (expected = DisabledException.class)
	public void testCoinValidatorDisable() throws DisabledException {
		// Arrange
		Coin TestCoin = new Coin(new BigDecimal(1), Currency.getInstance("CAD"));
		CoinPayment TestCoinPayment = new CoinPayment();
		
		// Act
		TestCoinPayment.station.coinValidator.disable();
		TestCoinPayment.setAmountOwed(new BigDecimal(100));
		
		// Inserting coin
		TestCoinPayment.insertCoin(TestCoin);
	}
	
	
	@Test (expected = DisabledException.class)
	public void testCoinStorageUnitDisable() throws DisabledException {
		// Arrange
		Coin TestCoin = new Coin(new BigDecimal(1), Currency.getInstance("CAD"));
		CoinPayment TestCoinPayment = new CoinPayment();
		
		// Act
		TestCoinPayment.station.coinStorage.disable();
		TestCoinPayment.setAmountOwed(new BigDecimal(100));
		
		for(BigDecimal denomination: TestCoinPayment.station.coinDenominations) {
            TestCoinPayment.station.coinDispensers.get(denomination).disable();
		}
		
		// Inserting coin
		TestCoinPayment.insertCoin(TestCoin);
	}

	@Test
	public void testCoinStorageFullAndAmountOwed() {
		// Arrange
		Coin TestCoin = new Coin(new BigDecimal(0.25), Currency.getInstance("CAD"));
		CoinPayment TestCoinPayment = new CoinPayment();
		
		// Act
		TestCoinPayment.setAmountOwed(new BigDecimal(1));
		Coin[] TestCoinStorageList = new Coin[999];
	    
		for (int i = 0; i < TestCoinStorageList.length; i++ ) {
	    	TestCoinStorageList[i] = new Coin(new BigDecimal(1), Currency.getInstance("CAD"));
	    }
		
		// Test
	    try {
			TestCoinPayment.station.coinStorage.load(TestCoinStorageList);
			TestCoinPayment.insertCoin(TestCoin);
			assertEquals(0,TestCoinPayment.getAmountOwed().compareTo(new BigDecimal(0.75)));	
		} catch (DisabledException | SimulationException |OverloadException e) {
			// TODO Auto-generated catch block
			fail("Should not throw " + e);
		}
	}
	
	@Test
	public void testUnloadAndRemovedCoin() {
		// Arrange
		Coin TestCoin = new Coin(new BigDecimal(0.25), Currency.getInstance("CAD"));
		CoinPayment TestCoinPayment = new CoinPayment();
		
		// Act
		TestCoinPayment.setAmountOwed(new BigDecimal(1000));
		Coin[] TestCoinStorageList = new Coin[999];
	    
		for (int i = 0; i < TestCoinStorageList.length; i++ ) {
	    	TestCoinStorageList[i] = new Coin(new BigDecimal(0.01), Currency.getInstance("CAD"));
	    }
		
		// Test
	    try {
			TestCoinPayment.station.coinStorage.load(TestCoinStorageList);
			TestCoinPayment.insertCoin(TestCoin);
			TestCoinPayment.station.coinStorage.unload();
			TestCoinPayment.insertCoin(TestCoin);
			TestCoinPayment.insertCoin(TestCoin);
			TestCoinPayment.removeCoins();
		} catch (DisabledException | SimulationException |OverloadException e) {
			fail("Should not throw " + e);
		}
	}
	



	@Test
	public void testInvalidCoinInvalidCurrency() {
		// Arrange
		Coin TestCoin = new Coin(new BigDecimal(1), Currency.getInstance("USD"));
		CoinPayment TestCoinPayment = new CoinPayment();
		
		// Act
		TestCoinPayment.setAmountOwed(new BigDecimal(100));
		
		// Test
		try {
			// Inserting invalid coin 20 times.
			for (int i = 0; i < 20; i++) {
				TestCoinPayment.insertCoin(TestCoin);
			}
			
		} catch (DisabledException e) {
		    fail("Should not throw " + e);
		}
		
		assertTrue("Coin tray should be full.", !TestCoinPayment.station.coinTray.hasSpace());
	}
	
	@Test
	public void testInvalidCoinInvalidDenomination() {
		// Arrange
		Coin TestCoin = new Coin(new BigDecimal(3), Currency.getInstance("CAD"));
		CoinPayment TestCoinPayment = new CoinPayment();
		
		// Act
		TestCoinPayment.setAmountOwed(new BigDecimal(100));
		
		// Test
		try {
			// Inserting invalid coin 20 times.
			for (int i = 0; i < 20; i++) {
				TestCoinPayment.insertCoin(TestCoin);
			}
		} catch (DisabledException e) {
			fail("Should not throw " + e);
		}
		
		assertTrue("Coin tray should be full.", !TestCoinPayment.station.coinTray.hasSpace());
	}
}
