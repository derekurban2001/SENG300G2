package org.lsmr.usecases;
import java.math.BigDecimal;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.listeners.BarcodeScannerListener;
import org.lsmr.selfcheckout.devices.listeners.ElectronicScaleListener;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class FinishAddingItems extends UseCases{
	// useful variables 
	Item item;
	BarcodedProduct barcodedProduct;
	BigDecimal itemPrice;
	public ElectronicScaleListener scaleElectronicListener;
	public BarcodeScannerListener barcodeScannerListener;
	
	
	// getting a final amount owned to be payed by the customer
	public BigDecimal doneAddingItems(Barcode barcode) {
		return getAmountOwed();
	}
	
	// proceeding to payment after scanning is done
	// scanners/ listeners have to be disabled
	public void proceedToPayment() {
		// Disabling scanners. 
		station.mainScanner.disable();
		station.handheldScanner.disable();
	}
	
	// upon answering "YES" to go back to scanning, the scanners will be enabled
	// and the customer can now go back to scanning
	public void backToScanning() {
		// Disabling scanners. 
		station.mainScanner.enable();
		station.handheldScanner.enable();
	}
}
