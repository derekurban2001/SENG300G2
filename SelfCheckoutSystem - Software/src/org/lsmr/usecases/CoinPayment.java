package org.lsmr.usecases;


import java.math.BigDecimal;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinSlot;
import org.lsmr.selfcheckout.devices.CoinStorageUnit;
import org.lsmr.selfcheckout.devices.CoinTray;
import org.lsmr.selfcheckout.devices.CoinValidator;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;

import org.lsmr.selfcheckout.devices.listeners.CoinSlotListener;
import org.lsmr.selfcheckout.devices.listeners.CoinStorageUnitListener;
import org.lsmr.selfcheckout.devices.listeners.CoinTrayListener;
import org.lsmr.selfcheckout.devices.listeners.CoinValidatorListener;

public class CoinPayment extends UseCases {
	CoinSlotListener coinSlotListener;
	CoinStorageUnitListener coinStorageUnitListener;
	CoinTrayListener coinTrayListener;
	CoinValidatorListener coinValidatorListener;
	boolean debug = false;
	BigDecimal coinDenom;
	
	//to initialize all the device and listener
	public CoinPayment() {
		coinSlotListener = new CoinSlotListener() {
		
		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

		@Override
		public void coinInserted(CoinSlot slot) {
			// TODO Auto-generated method stub
			if(debug) System.out.println("coin is inserted!");
		}

	}; 
	
	station.coinSlot.register(coinSlotListener);
	
	coinValidatorListener = new CoinValidatorListener() {

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

		@Override
		public void validCoinDetected(CoinValidator validator, BigDecimal value) {
			// TODO Auto-generated method stub
			coinDenom = value;
		}

		@Override
		public void invalidCoinDetected(CoinValidator validator) {
			// TODO Auto-generated method stub
			if(debug) System.out.println("invalid coin detected!");
		}};
		
	station.coinValidator.register(coinValidatorListener);
		
	coinStorageUnitListener = new CoinStorageUnitListener() {

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

		@Override
		public void coinsFull(CoinStorageUnit unit) {

			if(amountOwed.compareTo(new BigDecimal(0)) > 0) {amountOwed = amountOwed.subtract(coinDenom);}
			station.coinStorage.disable();
			if(debug) {System.out.println("coin storage is full!");}
		}

		@Override
		public void coinAdded(CoinStorageUnit unit) {
			// TODO Auto-generated method stub
			if(amountOwed.compareTo(new BigDecimal(0)) <= 0) {
				station.coinSlot.disable();
				}else{
				amountOwed = amountOwed.subtract(coinDenom);
				}
	}

		@Override
		public void coinsLoaded(CoinStorageUnit unit) {
			// TODO Auto-generated method stub
			if(debug) System.out.println("coin is loaded!");
		}

		@Override
		public void coinsUnloaded(CoinStorageUnit unit) {
			// TODO Auto-generated method stub
			if(debug) System.out.println("coin is unloaded!");
			
			if (station.coinStorage.isDisabled()) {
				station.coinStorage.enable();
			}
		}
	};
			
	station.coinStorage.register(coinStorageUnitListener);
	
	coinTrayListener = new CoinTrayListener() {

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

		@Override
		public void coinAdded(CoinTray tray) {
			// TODO Auto-generated method stub
			if(debug) System.out.println("coin is in the tray!");
		}};
		
	station.coinTray.register(coinTrayListener);
	}
	
	
	public void insertCoin(Coin coin) throws DisabledException{
		
		station.coinSlot.accept(coin);
	}
	
	public void removeCoins() {
		station.coinTray.collectCoins();
	}
}
