package org.lsmr.testcases;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Item;
import org.lsmr.usecases.AddOwnBags;

public class AddOwnBagsTest {
	AddOwnBags useCase;
	Item bag;
	
	// Setting up use cases.
	@Before
	public void setUp() {
		useCase = new AddOwnBags();
		
		// Adding bags to scale, but keeping the scale zeroed.
		bag = new Bag(25.0);
	}
	
	// Test ensures that both scanners are enabled after the bags are added to the bagging area.
	@Test
	public void testMainScannerDisabled() {
		assertTrue("Both scanners should be enabled.", !useCase.station.mainScanner.isDisabled() && !useCase.station.handheldScanner.isDisabled());
	}
	
	// disable the scanners after a bag has been added to the bagging area
		// at this point the listener should be deregistered and 
	
	public class Bag extends Item {
		// ...
		protected Bag(double weightInGrams) {
			super(weightInGrams);
			// TODO Auto-generated constructor stub
		}
	}
}
