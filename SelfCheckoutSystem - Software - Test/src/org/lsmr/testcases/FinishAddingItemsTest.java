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
	public void testScannerDisabledAfterScan () {
		useCase.proceedToPayment();
		// Scanning item with main scanner, which should cause it to be disabled.
		useCase.station.handheldScanner.scan(pickles);
		
		// Test will fail if either the main scanner or the handheld scanner is enabled.
		assertTrue("Both scanners should be disabled.", useCase.station.mainScanner.isDisabled() && useCase.station.handheldScanner.isDisabled());
	}
	
	@Test
    public final void TestCorrectTotalWeight1Item() {
		useCase = new FinishAddingItems();
	
		ItemPrice1 = useCase.doneAddingItems(barcode);
		useCase.updateCartTotal(ItemPrice1);

		assertEquals(useCase.getCartTotal(), ItemPrice1);
		
    }
	@Test
    public final void TestCorrectTotalWeight2Items() {
		useCase = new FinishAddingItems();
	
		ItemPrice1 = useCase.doneAddingItems(barcode);
		ItemPrice2 = useCase.doneAddingItems(barcode);
		ItemPrice3 = ItemPrice1.add(ItemPrice2);
		useCase.updateCartTotal(ItemPrice3);
		
		assertEquals(useCase.getCartTotal(), ItemPrice3);
		
    }
	
}
