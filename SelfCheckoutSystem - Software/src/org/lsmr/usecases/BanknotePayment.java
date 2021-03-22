package org.lsmr.usecases;

import java.math.BigDecimal;

import java.util.Currency;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.BanknoteStorageUnit;
import org.lsmr.selfcheckout.devices.BanknoteValidator;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteSlotListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteStorageUnitListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteValidatorListener;

public class BanknotePayment extends UseCases {
	public BanknoteSlotListener slotListener;
	public BanknoteValidatorListener validatorListener;
	public BanknoteStorageUnitListener storageListener;
	private boolean debug = false;
	private BigDecimal banknoteValue;
	
	public BanknotePayment(){
		// **initialize hardware instances**
		
		// initialize bank note slot device and listener
		slotListener = new BanknoteSlotListener() {
			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
				if(debug) System.out.println("Bank note slot is disabled!");
			}
				
			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
				if(debug) System.out.println("Bank note slot is disabled!");
			}

			@Override
			public void banknoteInserted(BanknoteSlot slot) {
				if(debug) System.out.println("Bank note is inserted!");
			}

			@Override
			public void banknoteEjected(BanknoteSlot slot) {
				if(debug) System.out.println("Bank note is ejected!");
			}

			@Override
			public void banknoteRemoved(BanknoteSlot slot) {
				if(debug) System.out.println("Bank note is removed!");
			}
		};
		station.banknoteInput.register(slotListener);	// register slotListener
		
		validatorListener = new BanknoteValidatorListener() {

			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

			@Override
			public void validBanknoteDetected(BanknoteValidator validator, Currency currency, int value) {
				banknoteValue = new BigDecimal(value);
			}

			@Override
			public void invalidBanknoteDetected(BanknoteValidator validator) {
				if(debug) System.out.println("Bank note is not valid.");
			}
		};
		station.banknoteValidator.register(validatorListener); // register validatorListener
		
		// initialize bank note storage with capacity and listener
		storageListener = new BanknoteStorageUnitListener() {

			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

			@Override
			public void banknotesFull(BanknoteStorageUnit unit) {
				if (amountOwed.compareTo(BigDecimal.ZERO) > 0) 
					amountOwed = amountOwed.subtract(banknoteValue);
				station.banknoteInput.disable();
				if(debug) System.out.println("Bank note storage is full.");
			}

			@Override
			public void banknoteAdded(BanknoteStorageUnit unit) {
				// if there is still money to be paid.
				amountOwed = amountOwed.subtract(banknoteValue);
				if(amountOwed.compareTo(BigDecimal.ZERO) <= 0) {
					station.banknoteInput.disable();
				}
				if(debug) System.out.println("Bank note is added to storage.");
			}

			@Override
			public void banknotesLoaded(BanknoteStorageUnit unit) {
				if(debug) System.out.println("Bank note is loaded to storage.");
			}

			@Override
			public void banknotesUnloaded(BanknoteStorageUnit unit) {
				if (station.banknoteInput.isDisabled())
					station.banknoteInput.enable();
				if(debug) System.out.println("Bank note is unloaded from the storage.");
			}
			
		};
		
		station.banknoteStorage.register(storageListener);
	}
	
	public void insertBanknote(Banknote banknote) throws SimulationException, DisabledException, OverloadException {
		// if banknoteInserted event then bank note is delivered to banknoteValidator
		// if the bank note is valid then deliver it to the storage
		// amountOwed should be decreased.
		// if the storage is full then eject the bank note.
		// else deliver to the storage.
		station.banknoteInput.accept(banknote);
	}
	
	public void removeBanknote() {
		station.banknoteInput.removeDanglingBanknote();
	}

}
