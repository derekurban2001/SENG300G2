package org.lsmr.usecases;

import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.ElectronicScaleListener;

public class BagItem extends UseCases {
	public ElectronicScaleListener scaleElectronicListener;

	// Registering the listener for the electronic scale in this constructor.
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
				// The main and handheld scanners will only be enabled if the item was properly bagged.
				// The weight must increase by the weight of the current item.
				if (weightInGrams > getCurrentWeight() && weightInGrams == getCurrentWeight() + getCurrentItem().getWeight()) {
		        	station.mainScanner.enable();
					station.handheldScanner.enable();
					// setItemBagged(true);					// not used for this iteration, but may be used for iteration 3
					setPreviousWeight(weightInGrams - getCurrentItem().getWeight());
					setCurrentWeight(getCurrentWeight() + getCurrentItem().getWeight());
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
	}
	
	 /*
     * Placing all the item in the bagging area
     * Pre-Condition: still in  the customer process session not paying yet
     * Post-Condition: completed updating the bagging area scale for all items
     * @param item: Item to be placed in bagging area
     */
	public void bagItem (Item item) {
        setCurrentItem(item);
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
	public boolean correctBaggageWeight() throws OverloadException {
		// Declaring variables.
        double actualWeight = station.baggingArea.getCurrentWeight();
        double expectedWeight = getCurrentWeight();
        
        // Find the difference between actual and expected weight.
		double differenceInValue = actualWeight - expectedWeight;
		
		// Return true if the expected weight and the actual weight are approximately the same.
		if (differenceInValue < station.baggingArea.getSensitivity() && differenceInValue > -station.baggingArea.getSensitivity()) {
			return true;
		}
		
        // Return false if the expected weight is not the same as the actual weight (shouldn't happen).
        return false;
    }
}
