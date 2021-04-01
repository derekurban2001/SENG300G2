package org.lsmr.usecases;

import java.math.BigDecimal;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BarcodeScannerListener;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class ScanItem extends UseCases {
	private BigDecimal itemPrice;
	private BarcodedProduct barcodedProduct;
	private BarcodeScannerListener listener;
	
	public ScanItem() {
		listener = new BarcodeScannerListener() {
			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
				// TODO Auto-generated method stub
			}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
				// TODO Auto-generated method stub
			}
			
			// This event occurs when an item is scanned. It retrieves the barcoded product and the price of the product, then updates the cart.
			@Override
			public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
				// Checking if the barcode corresponds to a BarcodedProduct in the database.
				if (ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(barcode)) {
					barcodedProduct = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
					itemPrice = barcodedProduct.getPrice();
					
					// Updating cart.
					addToCart(barcodedProduct);
					updateCartTotal(itemPrice);
					updateAmountOwed(itemPrice);
					updateNumberOfItems(1);
					
					// Disabling scanners. They will be enabled when the item has been bagged.
					station.mainScanner.disable();
					station.handheldScanner.disable();
				}
			}
			
		};
		
		station.mainScanner.register(listener);
		station.handheldScanner.register(listener);
	}
}
