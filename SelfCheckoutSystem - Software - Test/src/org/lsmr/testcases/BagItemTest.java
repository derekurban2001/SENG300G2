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
	
	
	@Before
	public void setUpItems() {
		useCase = new BagItem();
		
	}

	// @Test to check that incorrect weight has been added to the bag 
    @Test 
    public final void testBagItemIncorrect() throws OverloadException {
        useCase = new BagItem();
        BarcodedItem item1 = new BarcodedItem(new Barcode("123"), 50.9);
        BarcodedItem item2 = new BarcodedItem(new Barcode("345"), 20.0);
        BarcodedItem item3 = new BarcodedItem(new Barcode("675"), 83.2);

        useCase.bagItem(item1);
		useCase.bagItem(item2);
		useCase.bagItem(item3);
		
        assertNotEquals(144.1, useCase.getBaggingAreaWeight(), 0);
        assertTrue(useCase.correctBaggageWeight());

    }
    
    
	// @Test to check that correct weight has been added to the bag 
	@Test
	public final void testBagItemCorrect() throws OverloadException {
		useCase = new BagItem();
		
		BarcodedItem item1 = new BarcodedItem(new Barcode("123"), 50.9);		
		BarcodedItem item2 = new BarcodedItem(new Barcode("345"), 20.0);	
		BarcodedItem item3 = new BarcodedItem(new Barcode("675"), 83.2);	
		
		
		useCase.bagItem(item1);
		double a = useCase.getCurrentWeight();
		useCase.bagItem(item2);
		double b = useCase.getCurrentWeight();
		useCase.bagItem(item3);
		double c = useCase.getCurrentWeight();
		double sum = a + b + c;
		
		
		assertEquals(sum, useCase.getBaggingAreaWeight(), 0);
		assertTrue(useCase.correctBaggageWeight());
		
	}
	
	// @Test to check if 1 item has been removed from the bag 
	@Test
	public final void testRemoveBaggedItem() 
	{
		useCase = new BagItem();
		BarcodedItem item1 = new BarcodedItem(new Barcode("123"), 50.9);		
		BarcodedItem item2 = new BarcodedItem(new Barcode("345"), 20.0);	
	
		useCase.bagItem(item1);
		double a = useCase.getCurrentWeight();
		useCase.bagItem(item2);
		double b = useCase.getCurrentWeight();
		double sum = a + b;
		
		assertEquals(useCase.getBaggingAreaWeight(),sum, 0);
		useCase.removingBaggedItem(item2);
		double removedSum = sum - a;
		assertEquals(useCase.getBaggingAreaWeight(),removedSum, 0);
		

	}
	
	// @Test to check if more than 1 item has been removed from the bag 
	@Test
	public final void testRemoveMoreBaggedItem() 
	{
		useCase = new BagItem();
		BarcodedItem item1 = new BarcodedItem(new Barcode("123"), 50.9);		
		BarcodedItem item2 = new BarcodedItem(new Barcode("345"), 20.0);	
		BarcodedItem item3 = new BarcodedItem(new Barcode("334"), 60.2);
		BarcodedItem item4 = new BarcodedItem(new Barcode("259"), 24.5);
		BarcodedItem item5 = new BarcodedItem(new Barcode("871"), 10.3);
		
		useCase.bagItem(item1);
		double a = useCase.getCurrentWeight();
		useCase.bagItem(item2);
		double b = useCase.getCurrentWeight();
		useCase.bagItem(item3);
		double c = useCase.getCurrentWeight();
		useCase.bagItem(item4);
		double d = useCase.getCurrentWeight();
		useCase.bagItem(item5);
		double e = useCase.getCurrentWeight();
		
		double sum = a + b + c + d + e;
		
		assertEquals(useCase.getBaggingAreaWeight(),sum, 0);
		useCase.removingBaggedItem(item1);
		useCase.removingBaggedItem(item2);
		useCase.removingBaggedItem(item4);
		double removedSum = sum - a - b - d;
		assertEquals(useCase.getBaggingAreaWeight(),removedSum, 0);
		

	}
	@Test
	public final void testEnableDisable()
	{
		useCase = new BagItem();
		useCase.station.baggingArea.enable();
		assertEquals(useCase.station.baggingArea.isDisabled(), false);
		
		useCase.station.baggingArea.disable();
		assertEquals(useCase.station.baggingArea.isDisabled(), true);
		
		
	}
	
	
	// @ test to check the weighting of the baggage to see if it is correct
	@Test
	public final void testCorrectBaggageWeight() throws OverloadException 
	{
		useCase = new BagItem();
		BarcodedItem item1 = new BarcodedItem(new Barcode("123"), 50.9);		
		BarcodedItem item2 = new BarcodedItem(new Barcode("345"), 20.0);	
		BarcodedItem item3 = new BarcodedItem(new Barcode("675"), 83.2);	
		BarcodedItem item4 = new BarcodedItem(new Barcode("675"), 10.4);
		BarcodedItem item5 = new BarcodedItem(new Barcode("675"), 70.5);
		BarcodedItem item6 = new BarcodedItem(new Barcode("675"), 61.5);
		
	
		useCase.bagItem(item1);
		double a = useCase.getCurrentWeight();
		useCase.updateBaggingAreaWeight(a);
		
		useCase.bagItem(item2);
		double b = useCase.getCurrentWeight();
		useCase.bagItem(item3);
		double c = useCase.getCurrentWeight();
		useCase.bagItem(item4);
		double d = useCase.getCurrentWeight();
		useCase.bagItem(item5);
		double e = useCase.getCurrentWeight();
		useCase.bagItem(item6);
		double f = useCase.getCurrentWeight();
		
		double sum = a + b + c + d + e + f;
		assertEquals(useCase.getBaggingAreaWeight(),sum, 0);
		assertTrue(useCase.correctBaggageWeight());
		
	}
	
	// test for overloadException 
	@Test
	public void testItemOverloadWeightException() {
		useCase = new BagItem();
		BarcodedItem item1 = new BarcodedItem(new Barcode("1234"), 12000000);
		
		useCase.bagItem(item1);
		double a = useCase.getCurrentWeight();
		assertEquals(a, useCase.getBaggingAreaWeight(), 0);
		assertNotEquals(useCase.getOverloading(), true);

	}
	
	
	// @ test to check the weighting of the baggage to see if it is incorrect
	@Test
	public final void testInCorrectBaggageWeight() throws OverloadException 
	{
		useCase = new BagItem();
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
		
		assertNotEquals(useCase.getBaggingAreaWeight(),5.0, 0);
		assertTrue(useCase.correctBaggageWeight());
		
	}


}
