package org.lsmr.usecases;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.ElectronicScaleListener;

// The creation of this use case outlines what would happen if the customer presses the button on the screen indicating they would like to add their own bags.
public class AddOwnBags extends UseCases {
	private ElectronicScaleListener electronicScaleListener;
	public boolean bagsAdded = false;
	
	public AddOwnBags () {
		electronicScaleListener = new ElectronicScaleListener () {
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
				// The main and handheld scanners will only be enabled if the customer has added their bag(s) to the scale (if the weight on the scale increases above the current weight).
				if (weightInGrams > getPreviousWeight()) {
		        	bagsAdded = true;
		        }
			}

			@Override
			public void overload(ElectronicScale scale) {
				// TODO Auto-generated method stub
			}

			@Override
			public void outOfOverload(ElectronicScale scale) {
				// TODO Auto-generated method stub
			}
		};
		
		// Registering listener and disabling scanners.
		station.baggingArea.register(electronicScaleListener);
		station.mainScanner.disable();
		station.handheldScanner.disable();
	}
	
	// Proceeding to scanning after bags have been added to the bagging area.
	// Scanners should be enabled and listener should be disabled.
	public void proceedToScanning() {
		station.mainScanner.enable();
		station.handheldScanner.enable();
		station.baggingArea.deregister(electronicScaleListener);
	}
}
