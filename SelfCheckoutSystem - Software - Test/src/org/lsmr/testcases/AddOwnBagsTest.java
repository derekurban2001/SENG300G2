package org.lsmr.testcases;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.usecases.AddOwnBags;

public class AddOwnBagsTest {
	AddOwnBags useCase;
	
	// Setting up use cases.
	@Before
	public void setUp() {
		useCase = new AddOwnBags();
		
		// Adding bags to scale, but keeping the scale zeroed.
		useCase.station.baggingArea.notifyWeightChanged();
	}
	
	// Test ensures that both scanners are enabled after the bags are added to the bagging area.
	@Test
	public void testMainScannerDisabled() {
		assertTrue("Both scanners should be enabled.", !useCase.station.mainScanner.isDisabled() && !useCase.station.handheldScanner.isDisabled());
	}
}
