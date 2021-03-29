package org.lsmr.testcases;

import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.products.*;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.usecases.ScanItem;
import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;

public class ScanItemTest {
	ScanItem useCase;
	BarcodedItem cheerios;
	BarcodedItem pickles;
	BarcodedItem rice;
	
	double weight;
	BigDecimal price;
	Barcode barcode;
	BarcodedProduct barcodedProduct;
	
	// Setting up use cases and items.
	@Before
	public void setUp() {
		useCase = new ScanItem();
		
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
		price = new BigDecimal(4.99);
		pickles = new BarcodedItem(barcode, weight);
		
		// Adding 'pickles' to barcoded product database.
		barcodedProduct = new BarcodedProduct(barcode, "Pickles", price);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, barcodedProduct);
	}
	
	// Testing scanning one item.
	// Note: This test may fail on occasion due to the scanner simulating failed barcode scans.
	@Test
	public void testScan() {
		// Scanning 'cheerios'.
		useCase.station.mainScanner.scan(pickles);
		
		// Boolean to keep track of whether the item was scanned and added to cart.
		boolean picklesInCart = false;
		
		// Checking to see if 'rice' was added to the cart.
		for (BarcodedProduct bp: useCase.cart) {
			if (bp.getBarcode() == pickles.getBarcode()) {
				picklesInCart = true;
			}
		}
		
		// Test fails if 'pickles' doesn't appear in the cart.
		assertTrue("Pickles should appear in the cart.", picklesInCart);
	}
	
	// Test ensures that both scanners are disabled after the main scanner scans an item.
	// Note: This test may fail on occasion due to the scanner simulating failed barcode scans.
	@Test
	public void testScannersDisabledAfterMainScan () {
		// Scanning item with main scanner, which should cause both scanners to be disabled.
		useCase.station.mainScanner.scan(pickles);
		
		// Test fails if either the main scanner or the handheld scanner is enabled.
		assertTrue("Both scanners should be disabled.", useCase.station.mainScanner.isDisabled() && useCase.station.handheldScanner.isDisabled());
	}

	// Test ensures that both scanners are disabled after the handheld scanner scans an item.
	// Note: This test may fail on occasion due to the scanner simulating failed barcode scans.
	@Test
	public void testScannerDisabledAfterScan () {
		// Scanning item with main scanner, which should cause it to be disabled.
		useCase.station.handheldScanner.scan(pickles);
		
		// Test fails if either the main scanner or the handheld scanner is enabled.
		assertTrue("Both scanners should be disabled.", useCase.station.mainScanner.isDisabled() && useCase.station.handheldScanner.isDisabled());
	}
	
	// Testing scanning multiple items.
	// Note: This test may fail on occasion due to the scanner simulating failed barcode scans.
	@Test
	public void testMultipleScans() {
		// Scanning items, and enabling scanner between scans (because it gets disabled after each scan until it is bagged).
		useCase.station.mainScanner.scan(cheerios);
		useCase.station.mainScanner.enable();
		useCase.station.mainScanner.scan(pickles);
		
		// Booleans to to keep track of whether the items were scanned and added to cart.
		boolean cheeriosInCart = false;
		boolean picklesInCart = false;
		
		// Checking to see if 'items' were added to the cart.
		for (BarcodedProduct bp: useCase.cart) {
			if (bp.getBarcode() == cheerios.getBarcode()) {
				cheeriosInCart = true;
			}
			else if (bp.getBarcode() == pickles.getBarcode()) {
				picklesInCart = true;
			}
		}
		
		// Test fails if one of 'cheerios', 'pickles', or 'rice' is not in the cart.
		assertTrue("Cheerios, pickles, and rice should appear in the cart.", cheeriosInCart && picklesInCart);
	}
	
	// Testing disabled main scanner. The item should not scan when the main scanner is disabled.
	@Test
	public void testMainScanWhenDisabled() {
		// Disabling the main scanner and attempting to scan 'cheerios'.
		useCase.station.mainScanner.disable();
		useCase.station.mainScanner.scan(cheerios);
		
		// Checking to see if 'cheerios' was added to the cart.
		for (BarcodedProduct bp: useCase.cart) {
			// Test fails if 'cheerios' was scanned and put into the cart.
			assertFalse("Scanned item shouldn't be added to cart when scanner is disabled.", bp.getBarcode() == cheerios.getBarcode());
		}
	}
	
	// Testing disabled handheld scanner. The item should not scan when the handheld scanner is disabled.
	@Test
	public void testHandheldScanWhenDisabled() {
		// Disabling the main scanner and attempting to scan 'cheerios'.
		useCase.station.handheldScanner.disable();
		useCase.station.handheldScanner.scan(cheerios);
		
		// Checking to see if 'cheerios' was added to the cart.
		for (BarcodedProduct bp: useCase.cart) {
			// Test fails if 'cheerios' was scanned and put into the cart.
			assertFalse("Scanned item shouldn't be added to cart when scanner is disabled.", bp.getBarcode() == cheerios.getBarcode());
		}
	}
	
	// Test ensures that if a scanned barcode is not in the barcoded product database, then it is not added to cart.
	@Test
	public void testBarcodeNotInDatabase() {
		// Creating 'rice' item.
		BarcodedItem rice;
		barcode = new Barcode("37483");
		weight = 3000;
		price = new BigDecimal(4.29);
		rice = new BarcodedItem(barcode, weight);
		
		// Scanning rice.
		useCase.station.mainScanner.scan(rice);
		
		// Checking to see if 'rice' was added to the cart.
		for (BarcodedProduct bp: useCase.cart) {
			// Test fails if rice is put into the cart when scanned.
			assertFalse("Rice should not appear in the cart.", bp.getBarcode() == rice.getBarcode());
		}
	}
	
	// Test ensures that the cart total is incremented properly when an item is scanned.
	// Note: This test may fail on occasion due to the scanner simulating failed barcode scans.
	@Test
	public void testCartTotal() {
		// Scanning items, and enabling scanner between scans (because it gets disabled after each scan until it is bagged).
		useCase.station.mainScanner.scan(cheerios);
		useCase.station.mainScanner.enable();
		useCase.station.mainScanner.scan(pickles);
		
		// Getting prices of items scanned and expected cart total.
		BigDecimal cheeriosPrice = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(cheerios.getBarcode()).getPrice();
		BigDecimal picklesPrice = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(pickles.getBarcode()).getPrice();
		BigDecimal expectedCartTotal = cheeriosPrice.add(picklesPrice);
		
		// Test fails if the cart total differs from the expected cart total.
		assertTrue("Cart total is different from expected cart total.", expectedCartTotal.equals(useCase.getCartTotal()));
	}
	
	// Test ensures that the amount owed is incremented properly when an item is scanned.
	// Note: This test may fail on occasion due to the scanner simulating failed barcode scans.
	@Test
	public void testAmountOwed() {
		// Scanning items, and enabling scanner between scans (because it gets disabled after each scan until it is bagged).
		useCase.station.mainScanner.scan(cheerios);
		useCase.station.mainScanner.enable();
		useCase.station.mainScanner.scan(pickles);
		
		// Getting prices of items scanned and expected cart total.
		BigDecimal cheeriosPrice = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(cheerios.getBarcode()).getPrice();
		BigDecimal picklesPrice = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(pickles.getBarcode()).getPrice();
		BigDecimal expectedAmountOwed = cheeriosPrice.add(picklesPrice);
		
		// Test fails if the amount owed differs from the expected amount owed.
		assertTrue("Amount owed is different from expected amount owed.", expectedAmountOwed.equals(useCase.getAmountOwed()));
	}
	
	// Test ensures that the number of items in the cart is incremented properly when an item is scanned.
	// Note: This test may fail on occasion due to the scanner simulating failed barcode scans.
	@Test
	public void testNumberOfItems() {
		// Scanning items, and enabling scanner between scans (because it gets disabled after each scan until it is bagged).
		useCase.station.mainScanner.scan(cheerios);
		useCase.station.mainScanner.enable();
		useCase.station.mainScanner.scan(pickles);
		
		// Defining expected number of items.
		int expectedNumberOfItems = 2;
		
		// Test fails if the cart total differs from the expected cart total
		assertTrue("Cart total is different from expected cart total.", useCase.getNumberOfItems() == expectedNumberOfItems);
	}
}
