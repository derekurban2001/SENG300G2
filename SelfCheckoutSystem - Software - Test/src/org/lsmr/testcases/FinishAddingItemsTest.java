package org.lsmr.testcases;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.usecases.BagItem;
import org.lsmr.usecases.FinishAddingItems;
import org.lsmr.usecases.ScanItem;
import org.lsmr.selfcheckout.external.ProductDatabases;

public class FinishAddingItemsTest {
	
	FinishAddingItems useCase;
	BarcodedItem cheerios;
	BarcodedItem pickles;
	BarcodedItem rice;
	BigDecimal ItemPrice1;
	BigDecimal ItemPrice2;
	BigDecimal ItemPrice3;
	double weight;
	BigDecimal price;
	Barcode barcode;
	BarcodedProduct barcodedProduct;
	public SelfCheckoutStation station;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		useCase = new FinishAddingItems();
		
		// Creating 'cheerios' barcoded item.
		barcode = new Barcode("47361");
		weight = 550;
		price = new BigDecimal(5.99);
		cheerios = new BarcodedItem(barcode, weight);
		
		// Adding 'cheerios' to barcoded product database.
		barcodedProduct = new BarcodedProduct(barcode, "Cheerios", price);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, barcodedProduct);
		
		// Creating 'pickles' barcoded item.
		barcode = new Barcode("73718");
		weight = 1200;
		price = new BigDecimal(5.99);
		pickles = new BarcodedItem(barcode, weight);
		
		// Adding 'pickles' to barcoded product database.
		barcodedProduct = new BarcodedProduct(barcode, "Pickles", price);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, barcodedProduct);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testEnableDisable()
	{
		useCase = new FinishAddingItems();
		
		useCase.station.mainScanner.enable();
		assertEquals(useCase.station.baggingArea.isDisabled(), false);
		
		useCase.station.mainScanner.disable();
		assertEquals(useCase.station.mainScanner.isDisabled(), true);
		
	
	}
	@Test
	public void testScannerDisabledAfterScan () {
		// Scanning item with main scanner, which should cause it to be disabled.
		useCase.station.handheldScanner.scan(pickles);
		
		// Test will fail if either the main scanner or the handheld scanner is enabled.
		assertTrue("Both scanners should be disabled.", useCase.station.mainScanner.isDisabled() && useCase.station.handheldScanner.isDisabled());
	}
	
	@Test
    public final void TestCorrectTotalWeight1Item() throws OverloadException {
		useCase = new FinishAddingItems();
		useCase.doneAddingItems(barcode);
		useCase.station.mainScanner.scan(pickles);
		useCase.station.mainScanner.scan(cheerios);
		ItemPrice1 = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(pickles.getBarcode()).getPrice();
		useCase.updateCartTotal(ItemPrice1);

		assertEquals(useCase.getCartTotal(), ItemPrice1);
		
    }
	@Test
    public final void TestCorrectTotalWeight2Items() throws OverloadException {
		useCase = new FinishAddingItems();
		useCase.station.mainScanner.scan(pickles);
		useCase.station.mainScanner.scan(cheerios);
		
	
		ItemPrice1 = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(pickles.getBarcode()).getPrice();
		ItemPrice2 = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(cheerios.getBarcode()).getPrice();
		ItemPrice3 = ItemPrice1.add(ItemPrice2);
		useCase.updateCartTotal(ItemPrice3);
		
		assertEquals(useCase.getCartTotal(), ItemPrice3);
		
    }
	@Test
    public final void TestIncorrectTotalWeight() throws OverloadException {
		useCase = new FinishAddingItems();
		useCase.doneAddingItems(barcode);
		useCase.station.mainScanner.scan(pickles);
		ItemPrice1 = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(pickles.getBarcode()).getPrice();
		
	
		assertNotEquals(useCase.getBaggingAreaWeight(), ItemPrice1);
		
    }


}
