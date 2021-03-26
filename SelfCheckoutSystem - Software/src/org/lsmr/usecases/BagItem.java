package org.lsmr.usecases;

import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BarcodeScannerListener;
import org.lsmr.selfcheckout.devices.listeners.ElectronicScaleListener;


public class BagItem extends UseCases {
	Item item;
	double currentWeightInGrams;
	int weightLimitInGrams;
	public int sensitivity;
	
	public ElectronicScaleListener scaleElectronicListener;
	public BarcodeScannerListener barcodeScannerListener;

	// Register the listeners in this constructor
	// Constructor
	public BagItem() {
		scaleElectronicListener =  new ElectronicScaleListener() {
			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
				// TODO Auto-generated method stub
			}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
				// TODO Auto-generated method stub
			}

			@Override
			public void weightChanged(ElectronicScale scale, double weightInGrams) {	
				double currentWeight = getCurrentWeight();
				double newWeight = getBaggingAreaWeight(); 
				
				// The main and handheld scanners will only be enabled if the item was properly bagged (if the weight on the scale increases above the current weight).
				if (newWeight > currentWeight) {
		        	station.mainScanner.enable();
					station.handheldScanner.enable();
					setItemBagged(true);
		        }
			}

			@Override
			public void overload(ElectronicScale scale)  {										
			}

			@Override
			public void outOfOverload(ElectronicScale scale) {
			}
			
		};
		 station.baggingArea.register(scaleElectronicListener);
		 station.mainScanner.register(barcodeScannerListener);
	     station.handheldScanner.register(barcodeScannerListener);
	}
	 /*
     * Placing all the item in the bagging area
     * Pre-Condition: still in  the customer process session not paying yet
     * Post-Condition: completed updating the bagging area scale for all items
     * @param item: Item to be placed in bagging area
     */
	public void bagItem (Item item) {
        setCurrentItem(item);			// not needed atm, but is here for use in future iterations
        station.baggingArea.add(item);
    }
	
	 /*
     * Removing items from the bagging if needed
     * Pre-Condition: still in  the customer process session not paying yet
     * Post-Condition: completed updating the bagging area scale for all items
     * @param item: Item to be removed from the bagging
     */
	public void removingBaggedItem (Item item) {       
        setCurrentItem(item);			// not needed atm, but is here for use in future iterations
        station.baggingArea.remove(item);
    }
	
	/*
     * Checking the weight in the Baggage 
     * Pre-Condition: the item has been scanned and bagged 
     * Post-Condition: checked and completed updating the weight of the baggage for all items
     */
	public boolean correctBaggageWeight() {
		//declaring variables 	
        double weight = getCurrentWeight();
        double baggingWeight = getBaggingAreaWeight();
        
        //find the difference between actual and expected weight
		double differenceInValue = weight - baggingWeight;
		// return false if they are not almost the same 
		if (differenceInValue < station.baggingArea.getSensitivity()) 
		{
			if(differenceInValue > -station.baggingArea.getSensitivity()) 
			{
		        return true;
			}
			
		}
        //return false if OverloadException is called
        return false;

    }
}
