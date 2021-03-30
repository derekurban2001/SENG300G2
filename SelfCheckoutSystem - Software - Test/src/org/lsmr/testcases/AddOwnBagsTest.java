package org.lsmr.testcases;

import static org.junit.Assert.assertEquals;
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
	
	// Test ensures that both scanners are enabled after the bags are added to the bagging area.
	@Test
	public void testBothScannersEnabled() {
		bag = new Bag(50.0);
		useCase.station.baggingArea.add(bag);
		assertTrue("Both scanners should be enabled.", !useCase.station.mainScanner.isDisabled() && !useCase.station.handheldScanner.isDisabled());
	}
	
	// Test covers enable and disable.
	@Test
    public final void testEnableDisable() {
        useCase.station.baggingArea.enable();
        assertEquals(useCase.station.baggingArea.isDisabled(), false);
        
        useCase.station.baggingArea.disable();
        assertEquals(useCase.station.baggingArea.isDisabled(), true);
    }
	
	// Test for OverloadException (if over 30kg of bags are placed on scale; however, this scenario is very unlikely).
	@Test (expected = OverloadException.class)
	public void testWeightOverloadtException() throws OverloadException {
		bag = new Bag(30000.1);
		useCase.station.baggingArea.add(bag);
		useCase.station.baggingArea.getCurrentWeight();
	}
	
	// disable the scanners after a bag has been added to the bagging area
		// at this point the listener should be deregistered and the scanners should be disabled
	
	public class Bag extends Item {
		// ...
		protected Bag(double weightInGrams) {
			super(weightInGrams);
			// TODO Auto-generated constructor stub
		}
	}
}
