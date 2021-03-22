package org.lsmr.testcases;
import static org.junit.Assert.assertTrue;


import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.usecases.BanknotePayment;

public class BanknotePaymentTest {
	// Because we only test the functionality, we assume that the hardwares are working correctly.
	
	/**
     * Test bank note slot device is disabled.
     */
    @Test (expected = DisabledException.class)
    public void testSlotDisable() throws DisabledException {
        Banknote testBanknote = null;
        BanknotePayment testUseCase = new BanknotePayment();
        testUseCase.station.banknoteInput.disable();// disable the device
        testUseCase.setAmountOwed(new BigDecimal(100));
        try {
            testUseCase.insertBanknote(testBanknote);
        } catch (SimulationException | OverloadException e) {
            fail("Should not thrown " + e);
        }
    }
    
	/**
	 *  Test bank note slot dangling ejected object.
	 */
	@Test (expected = OverloadException.class)
	public void testSlotDangling() throws OverloadException {
		Banknote testBanknote = new Banknote(2, Currency.getInstance("CAD"));
		BanknotePayment testUseCase = new BanknotePayment();
		testUseCase.setAmountOwed(new BigDecimal(100));
		try {
			testUseCase.insertBanknote(testBanknote);
			testUseCase.insertBanknote(testBanknote);
		} catch (SimulationException | DisabledException e) {
			fail("Should not thrown " + e);
		}
	}
	
	/**
	 *  Test bank note slot dangling ejected object = null, no object to remove.
	 */
	@Test 
	public void testSlotRemoveDangling1() {
		BanknotePayment testUseCase = new BanknotePayment();
		testUseCase.setAmountOwed(new BigDecimal(100));
		try {
			testUseCase.removeBanknote();
		} catch (SimulationException e) {
			fail("Should not thrown " + e);
		}
		assertTrue("Bank note slot should have space.", testUseCase.station.banknoteInput.hasSpace());
	}
	
	/**
	 *  Test bank note slot dangling ejected object, remove bank note.
	 */
	@Test 
	public void testSlotRemoveDangling2() {
		Banknote testBanknote = new Banknote(2, Currency.getInstance("CAD"));
		BanknotePayment testUseCase = new BanknotePayment();
		testUseCase.setAmountOwed(new BigDecimal(100));
		try {
			testUseCase.insertBanknote(testBanknote);
			testUseCase.removeBanknote();
		} catch (SimulationException | DisabledException | OverloadException e) {
			fail("Should not thrown " + e);
		}
		assertTrue("Bank note slot should have space.", testUseCase.station.banknoteInput.hasSpace());
	}
	
	/**
	 * insert with invalid currency
	 */
	@Test
	public void testInvalidCurr() {
		Banknote testBanknote = new Banknote(5, Currency.getInstance("USD"));
		BanknotePayment testUseCase = new BanknotePayment();
		
		testUseCase.setAmountOwed(new BigDecimal(100));
		try {
			testUseCase.insertBanknote(testBanknote);
		} catch (SimulationException | DisabledException | OverloadException e) {
			fail("Should not thrown " + e);
		}
		assertTrue("The bank note should be ejected.", !testUseCase.station.banknoteInput.hasSpace());
	}
	
	/**
	 * insert with invalid denomination. Eg: 3 CAD bill.
	 */
	@Test
	public void testInvalidDenom() {
		Banknote testBanknote = new Banknote(3, Currency.getInstance("CAD"));
		BanknotePayment testUseCase = new BanknotePayment();
		
		testUseCase.setAmountOwed(new BigDecimal(100));
		try {
			testUseCase.insertBanknote(testBanknote);
		} catch (SimulationException | DisabledException | OverloadException e) {
			fail("Should not thrown " + e);
		}
		assertTrue("The bank note should be ejected.", !testUseCase.station.banknoteInput.hasSpace());
	}
	
	/**
	 *  insert with null bank note.
	 */
	@Test (expected = SimulationException.class)
	public void testNull() {
		Banknote testBanknote = null;
		BanknotePayment testUseCase = new BanknotePayment();
		
		testUseCase.setAmountOwed(new BigDecimal(100));
		try {
			testUseCase.insertBanknote(testBanknote);
		} catch (DisabledException | OverloadException e) {
			fail("Should not thrown " + e);
		}
	}
	
	/**
	 * insert with full storage.
	 */
	@Test
	public void testStorageFull() {
		Banknote testBanknote = new Banknote(5, Currency.getInstance("CAD"));
		BanknotePayment testUseCase = new BanknotePayment();
		
		testUseCase.setAmountOwed(new BigDecimal(100));
		try {
			Banknote[] banknotes = new Banknote[1000];
			for (int i = 0; i < banknotes.length; i++) {
				banknotes[i] = new Banknote(5, Currency.getInstance("CAD"));
			}
			testUseCase.station.banknoteStorage.load(banknotes);
			testUseCase.insertBanknote(testBanknote);
		} catch (SimulationException | DisabledException | OverloadException e) {
			fail("Should not thrown " + e);
		}
		assertTrue("Storage full, the bank note should be ejected.", !testUseCase.station.banknoteInput.hasSpace());
	}
	
	/**
	 * Test bank note validator device is disabled.
	 */
	@Test (expected = DisabledException.class)
	public void testValidatorDisable() throws DisabledException {
		Banknote testBanknote = null;
		BanknotePayment testUseCase = new BanknotePayment();
		testUseCase.station.banknoteValidator.disable();// disable the device
		testUseCase.setAmountOwed(new BigDecimal(100));
		try {
			testUseCase.insertBanknote(testBanknote);
		} catch (SimulationException | OverloadException e) {
			fail("Should not thrown " + e);
		}
	}
	
	/**
	 * Test if the customer over pay the amount needed.
	 */
	@Test (expected = DisabledException.class)
	public void testOverPay() throws DisabledException{
		Banknote testBanknote = new Banknote(100, Currency.getInstance("CAD"));
		BanknotePayment testUseCase = new BanknotePayment();
		testUseCase.setAmountOwed(new BigDecimal(100));
		try {
			testUseCase.insertBanknote(testBanknote);
			testUseCase.insertBanknote(testBanknote);
		} catch (SimulationException | OverloadException e) {
			fail("Should not thrown " + e);
		}
	}
	
	/**
	 * Insert valid bank note.
	 */
	@Test
	public void testValid1() {
		Banknote testBanknote = new Banknote(5, Currency.getInstance("CAD"));
		BanknotePayment testUseCase = new BanknotePayment();
		testUseCase.setAmountOwed(new BigDecimal(100));
		try {
			testUseCase.insertBanknote(testBanknote);
		} catch (SimulationException | OverloadException | DisabledException e) {
			fail("Should not thrown " + e);
		}
		assertTrue("The amount owed should be: 95", testUseCase.getAmountOwed().compareTo(new BigDecimal(95)) == 0);
	}
	
	/**
	 * Insert valid bank note, max capacity.
	 */
	@Test
	public void testValid2() {
		Banknote testBanknote = new Banknote(5, Currency.getInstance("CAD"));
		BanknotePayment testUseCase = new BanknotePayment();
		testUseCase.setAmountOwed(new BigDecimal(100));
		try {
			Banknote[] banknotes = new Banknote[999];
			for (int i = 0; i < banknotes.length; i++) {
				banknotes[i] = new Banknote(5, Currency.getInstance("CAD"));
			}
			testUseCase.station.banknoteStorage.load(banknotes);
			testUseCase.insertBanknote(testBanknote);
		} catch (SimulationException | OverloadException | DisabledException e) {
			fail("Should not thrown " + e);
		}
		assertTrue("The amount owed should be: 95", testUseCase.getAmountOwed().compareTo(new BigDecimal(95)) == 0);
	}
	
	/**
	 * Insert invalid bank note, remove then insert valid bank note.
	 */
	@Test
	public void testValid3() {
		Banknote testBanknote1 = new Banknote(5, Currency.getInstance("USD"));
		Banknote testBanknote2 = new Banknote(5, Currency.getInstance("CAD"));
		BanknotePayment testUseCase = new BanknotePayment();
		testUseCase.setAmountOwed(new BigDecimal(100));
		try {
			testUseCase.insertBanknote(testBanknote1);
			testUseCase.removeBanknote();
			testUseCase.insertBanknote(testBanknote2);
		} catch (SimulationException | OverloadException | DisabledException e) {
			fail("Should not thrown " + e);
		}
		assertTrue("The amount owed should be: 95", testUseCase.getAmountOwed().compareTo(new BigDecimal(95)) == 0);
	}
	
	/**
	 * Insert to max capacity, unload then insert valid bank note.
	 */
	@Test
	public void testValid4() {
		Banknote testBanknote = new Banknote(5, Currency.getInstance("CAD"));
		BanknotePayment testUseCase = new BanknotePayment();
		testUseCase.setAmountOwed(new BigDecimal(100));
		try {
			Banknote[] banknotes = new Banknote[999];
			for (int i = 0; i < banknotes.length; i++) {
				banknotes[i] = new Banknote(5, Currency.getInstance("CAD"));
			}
			testUseCase.station.banknoteStorage.load(banknotes);
			testUseCase.insertBanknote(testBanknote);
			testUseCase.station.banknoteStorage.unload();
			testUseCase.insertBanknote(testBanknote);
		} catch (SimulationException | OverloadException | DisabledException e) {
			fail("Should not thrown " + e);
		}
		assertTrue("The amount owed should be: 90", testUseCase.getAmountOwed().compareTo(new BigDecimal(90)) == 0);
	}
	
	/**
	 * insert valid with full storage.
	 */
	@Test
	public void testValid5() {
		Banknote testBanknote = new Banknote(5, Currency.getInstance("CAD"));
		BanknotePayment testUseCase = new BanknotePayment();
		
		testUseCase.setAmountOwed(new BigDecimal(100));
		try {
			Banknote[] banknotes = new Banknote[999];
			for (int i = 0; i < banknotes.length; i++) {
				banknotes[i] = new Banknote(5, Currency.getInstance("CAD"));
			}
			testUseCase.station.banknoteStorage.load(banknotes);
			testUseCase.insertBanknote(testBanknote);
		} catch (SimulationException | DisabledException | OverloadException e) {
			fail("Should not thrown " + e);
		}
		assertTrue("The amount owed should be: 95", testUseCase.getAmountOwed().compareTo(new BigDecimal(95)) == 0);
	}
}
