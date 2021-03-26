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
	
	/**
     * Converts a list of BarcodedItems into a list of BarcodedProducts based off the product database
     * 
     * @param ArrayList<BarcodedItem> list
     *             The list of items to be converted to products
     * 
     * @throws SimulationException
     *             If the list provided is null
     *             If the barcode found in an item isn't in the product database
     *
     */
    public void convertItemToProduct(ArrayList<BarcodedItem> list) {
        if(list == null)
            throw new SimulationException("Can't convert null list");

        ArrayList<BarcodedProduct> productList = new ArrayList<BarcodedProduct>();

        for(BarcodedItem item : list) {
            if(!ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(item.getBarcode()))
                throw new SimulationException("Item not in product database");

            productList.add(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(item.getBarcode()));
        }

        BigDecimal finalPrice = getFinal(productList);
        
        // call the parent payment class here
        
      
       
        
    }
	
}
