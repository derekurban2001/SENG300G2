package org.lsmr.testcases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.usecases.AddOwnBags;

public class AddOwnBagsTest {
	AddOwnBags useCase;
	Item bag;
	
	// Setting up use cases.
	@Before
	public void setUp() {
		useCase = new AddOwnBags();
	}
	
	// Test ensures that both scanners are disabled after the customer chooses to add their own bags.
	// Note: The creation of the AddOwnBags useCase represents the customer pressing the button the screen indicating they would like to add their own bags.
	@Test
	public void testBothScannersDisabled() {
		assertTrue("Both scanners should be disabled.", useCase.station.mainScanner.isDisabled() && useCase.station.handheldScanner.isDisabled());
	}
	
	// Test ensures that both scanners are enabled when proceeding to scanning.
	@Test
	public void testBothScannersEnabled() {
		useCase.proceedToScanning();
		assertTrue("Both scanners should be enabled.", !useCase.station.mainScanner.isDisabled() && !useCase.station.handheldScanner.isDisabled());
	}
	
	// Test ensures that the listener is deregistered when proceeding to scanning.
	// It is important that the listener is deregistered because we don't want it to conflict with other listeners in other use cases.
	@Test
	public void testListenerDeregistered() {
		// Proceeding to the scanning area so that the lister used to detect if the customer has added their bags to the bagging area gets deregistered.
		useCase.proceedToScanning();
		
		// Adding a bag to the bagging area. Then bagsAdded in useCase should remain false, since the listener has been deregistered.
		bag = new Bag(50.0);
		useCase.station.baggingArea.add(bag);
		
		assertFalse("The listener should have been deregistered.", useCase.bagsAdded);
	}
	
	// Test checks if the bag was added to the bagging area.
	@Test
	public void testBagsAdded() {
		bag = new Bag(150.0);
		useCase.station.baggingArea.add(bag);
		assertTrue("Bag should have been added to the bagging area.", useCase.bagsAdded);
	}
	
	// Test checks if bag was not added to the bagging area.
	@Test
	public void testBagsNotAdded() {
		assertFalse("Bag should have been added to the bagging area.", useCase.bagsAdded);
	}
	
	// Test for OverloadException (if over 30kg of bags are placed on scale; however, this scenario is very unlikely).
	@Test (expected = OverloadException.class)
	public void testWeightOverloadtException() throws OverloadException {
		bag = new Bag(30000.1);
		useCase.station.baggingArea.add(bag);
		useCase.station.baggingArea.getCurrentWeight();
	}
	
	// Class to create a bag that can be added to the bagging area.
	public class Bag extends Item {
		protected Bag(double weightInGrams) {
			super(weightInGrams);
			// TODO Auto-generated constructor stub
		}
	}
}
