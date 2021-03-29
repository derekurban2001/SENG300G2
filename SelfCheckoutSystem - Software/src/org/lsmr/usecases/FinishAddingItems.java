package org.lsmr.usecases;

import java.math.BigDecimal;

import java.util.ArrayList;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BarcodeScannerListener;
import org.lsmr.selfcheckout.devices.listeners.ElectronicScaleListener;
import org.lsmr.selfcheckout.devices.listeners.TouchScreenListener;
import org.lsmr.selfcheckout.external.ProductDatabases;
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
	
	public void backToScanning() {
		// Disabling scanners. 
		station.mainScanner.enable();
		station.handheldScanner.enable();
	}
}
