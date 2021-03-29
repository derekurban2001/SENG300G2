package org.lsmr.usecases;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.ElectronicScaleListener;

public class AddOwnBags extends UseCases {
	public ElectronicScaleListener electronicScaleListener;
	
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
				// TODO Auto-generated method stub
				double currentWeight = getCurrentWeight();
				double newWeight = getBaggingAreaWeight(); 
				
				// The main and handheld scanners will only be enabled if the customer has added their bag(s) to the scale (if the weight on the scale increases above the current weight).
				if (newWeight > currentWeight) {
		        	station.mainScanner.enable();
					station.handheldScanner.enable();
					station.baggingArea.deregister(electronicScaleListener);
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
		
		station.baggingArea.register(electronicScaleListener);
	}
}
