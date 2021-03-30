package org.lsmr.testcases;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.usecases.BagItem;

public class BagItemTest {
	Item item;
	double currentWeightInGrams;
	int weightLimitInGrams;
	public int sensitivity;
	BagItem useCase;
	
	// Setup for test cases.
	@Before
	public void setUp() {
		useCase = new BagItem();
	}

	// Test to check that incorrect weight has been added to the bag 
    @Test 
    public final void testBagItemIncorrect() throws OverloadException {
        BarcodedItem item1 = new BarcodedItem(new Barcode("123"), 50.9);
        BarcodedItem item2 = new BarcodedItem(new Barcode("345"), 20.0);
        BarcodedItem item3 = new BarcodedItem(new Barcode("675"), 83.2);

        useCase.bagItem(item1);
		useCase.bagItem(item2);
		useCase.bagItem(item3);
		
        assertNotEquals(144.1, useCase.station.baggingArea.getCurrentWeight(), 0);
        assertTrue(useCase.correctBaggageWeight());

    }
  
    
	// Test to check that correct weight has been added to the bag.
	@Test
	public final void testBagItemCorrect() throws OverloadException {
		BarcodedItem item1 = new BarcodedItem(new Barcode("123"), 50.9);
		useCase.bagItem(item1);
		
		assertEquals(item1.getWeight(), useCase.getCurrentItem().getWeight(), 0);
	}
	
	// Test to check if 1 item has been removed from the bag.
	@Test
	public final void testRemoveBaggedItem() throws OverloadException {
		BarcodedItem item1 = new BarcodedItem(new Barcode("123"), 50.9);		
		BarcodedItem item2 = new BarcodedItem(new Barcode("345"), 20.0);	
	
		useCase.bagItem(item1);
		useCase.bagItem(item2);
		useCase.removingBaggedItem(item2);
		
		assertEquals(useCase.station.baggingArea.getCurrentWeight(), round(useCase.getPreviousWeight(), 1), 0);
	}
	
	// Test to check if more than 1 item has been removed from the bag. 
	@Test
	public final void testRemoveMoreBaggedItem() throws OverloadException {
		useCase = new BagItem();
		BarcodedItem item1 = new BarcodedItem(new Barcode("123"), 50.9);		
		BarcodedItem item2 = new BarcodedItem(new Barcode("345"), 20.0);	
		BarcodedItem item3 = new BarcodedItem(new Barcode("334"), 60.2);
		BarcodedItem item4 = new BarcodedItem(new Barcode("259"), 24.5);
		BarcodedItem item5 = new BarcodedItem(new Barcode("871"), 10.3);
		
		useCase.bagItem(item1);
		useCase.bagItem(item2);
		useCase.bagItem(item3);
		useCase.bagItem(item4);
		useCase.bagItem(item5);
		double originalWeight = useCase.station.baggingArea.getCurrentWeight();
		
		useCase.removingBaggedItem(item1);
		useCase.removingBaggedItem(item2);
		useCase.removingBaggedItem(item3);
		double removedWeight = item1.getWeight() + item2.getWeight() + item3.getWeight();
		
		assertEquals(useCase.station.baggingArea.getCurrentWeight(), round(originalWeight - removedWeight, 1), 0);
	}
	
	// Test to check the correctness of the weight of the bagging area.
	@Test
	public void testCorrectBaggageWeight() throws OverloadException {
		useCase = new BagItem();
		BarcodedItem item1 = new BarcodedItem(new Barcode("123"), 50.9);		
		BarcodedItem item2 = new BarcodedItem(new Barcode("345"), 20.0);	
		BarcodedItem item3 = new BarcodedItem(new Barcode("675"), 83.2);	
		BarcodedItem item4 = new BarcodedItem(new Barcode("675"), 10.4);
		BarcodedItem item5 = new BarcodedItem(new Barcode("675"), 70.5);
		BarcodedItem item6 = new BarcodedItem(new Barcode("675"), 61.5);
	
		useCase.bagItem(item1);
		useCase.bagItem(item2);
		useCase.bagItem(item3);
		useCase.bagItem(item4);
		useCase.bagItem(item5);
		useCase.bagItem(item6);
		
		assertTrue(useCase.correctBaggageWeight());
	}
	
	// Test for OverloadException.
	@Test (expected = OverloadException.class)
	public void testItemOverloadWeightException() throws OverloadException {
		BarcodedItem item1 = new BarcodedItem(new Barcode("1234"), 31000);
		useCase.bagItem(item1);
		useCase.station.baggingArea.getCurrentWeight();
	}
	
	// Test to check the weighting of the baggage to see if it is incorrect.
	@Test
	public final void testIncorrectBaggageWeight() {
		BarcodedItem item1 = new BarcodedItem(new Barcode("123"), 50.9);		
		BarcodedItem item2 = new BarcodedItem(new Barcode("345"), 20.0);	
		BarcodedItem item3 = new BarcodedItem(new Barcode("675"), 83.2);	
		BarcodedItem item4 = new BarcodedItem(new Barcode("675"), 10.4);
		BarcodedItem item5 = new BarcodedItem(new Barcode("675"), 70.5);
		BarcodedItem item6 = new BarcodedItem(new Barcode("675"), 10.3);
		
		useCase.bagItem(item1);
		useCase.bagItem(item2);
		useCase.bagItem(item3);
		useCase.bagItem(item4);
		useCase.bagItem(item5);
		useCase.bagItem(item6);
		
		assertNotEquals(useCase.getCurrentWeight(), 5.0, 0);
	}
	
	// Test covers enable and disable.
	@Test
    public final void testEnableDisable() {
        useCase.station.baggingArea.enable();
        assertEquals(useCase.station.baggingArea.isDisabled(), false);
        
        useCase.station.baggingArea.disable();
        assertEquals(useCase.station.baggingArea.isDisabled(), true);
    }
	
	// Rounding method.
	public static double round (double value, int precision) {
	    int scale = (int) Math.pow(10, precision);
	    return (double) Math.round(value * scale) / scale;
	}
}
