package org.lsmr.usecases;

import java.math.BigDecimal;

import java.util.ArrayList;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BarcodeScannerListener;
import org.lsmr.selfcheckout.devices.listeners.ElectronicScaleListener;
import org.lsmr.selfcheckout.devices.listeners.TouchScreenListener;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class FinishAddingItems extends UseCases{
	Item item;
	
	public ElectronicScaleListener scaleElectronicListener;
	public BarcodeScannerListener barcodeScannerListener;
	public TouchScreenListener touchScreenListener;
	// Register the listeners in this constructor
	//constructor
		
	public FinishAddingItems() {	      
		 barcodeScannerListener =  new BarcodeScannerListener() {

			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
				
				// TODO Auto-generated method stub
				
			}

			@Override
			public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
				
				// Disabling scanners. 
				station.mainScanner.disable();
				station.handheldScanner.disable();
			}
	    	 
	     };
	     station.mainScanner.register(barcodeScannerListener);
		 station.handheldScanner.register(barcodeScannerListener);
		 
		 
	}
	
	
}
