package org.lsmr.usecases;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.CoinTray;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteSlotListener;
import org.lsmr.selfcheckout.devices.listeners.CoinTrayListener;

public class ReturnChange {
	
	private boolean banknotePause = false;
	private boolean coinPause = false;
	private SelfCheckoutStation station;
	private ArrayList<BigDecimal> coinsToReturn = new ArrayList<BigDecimal>();
	private ArrayList<Integer> banknotesToReturn = new ArrayList<Integer>();
	private Map<BigDecimal, CoinDispenser> coinDispensers;
	private Map<Integer, BanknoteDispenser> banknoteDispensers;
	private int[] banknoteDenominations;
	private List<BigDecimal> coinDenominations;
	
	public ReturnChange(SelfCheckoutStation station) {
		if(station == null)
			throw new SimulationException("Can't return change with null station");
		
		this.station = station;
		
		this.station.banknoteOutput.register(createBanknoteListener());
		this.station.coinTray.register(createCoinTrayListener());
		
		banknotesToReturn = new ArrayList<Integer>();
		coinsToReturn = new ArrayList<BigDecimal>();
		coinDispensers = station.coinDispensers;
		banknoteDispensers = station.banknoteDispensers;
		banknoteDenominations = reverseOrder(station.banknoteDenominations);
		coinDenominations = reverseOrder(station.coinDenominations);
	}
	
	public void calculateChange(double changeTotal) throws OverloadException{
		double total = changeTotal;
		coinsToReturn.clear();
		banknotesToReturn.clear();
		
		for(int denomination : banknoteDenominations) {
			if(total - denomination >= 0 && banknoteDispensers.get(denomination).size() > 0) {
				int banknoteCount = 1;
				while(total - denomination*banknoteCount >= 0 && banknoteDispensers.get(denomination).size() >= banknoteCount) {
					banknotesToReturn.add(denomination);
					banknoteCount++;
				}
				banknoteCount--;
				
				total = BigDecimal.valueOf(total).subtract(BigDecimal.valueOf(denomination*banknoteCount)).doubleValue();
			}
		}
		
		for(BigDecimal denomination : coinDenominations) {
			double value = denomination.doubleValue();
			
			if(total - value >= 0 && coinDispensers.get(denomination).size() > 0) {
				double coinCount = 1;
				while(total - value*coinCount >= 0 && coinDispensers.get(denomination).size() >= coinCount) {
					coinsToReturn.add(denomination);
					coinCount++;
				}
				coinCount--;
				
				
				total = BigDecimal.valueOf(total).subtract(BigDecimal.valueOf(value*coinCount)).doubleValue();
			}
		}
		
		if(total >= 0.05)
			throw new SimulationException("Cannot return change, please re-stock change dispensers!");
		
		printChange(banknotesToReturn, coinsToReturn);
	}
	
	public void releaseBanknoteChange() throws DisabledException{
		ArrayList<Integer> toRemove = new ArrayList<Integer>();
		for(Integer denomination : banknotesToReturn) {
			if(!banknotePause) {
				try {
					banknoteDispensers.get(denomination).emit();
					toRemove.add(denomination);
				}
				catch(OverloadException ex) {
					System.out.println("Please remove banknote to collect rest of change.");
					banknotePause = true;
				}
				catch(EmptyException ex) {/*Shouldn't happen*/}
			}
		}
		banknotesToReturn.removeAll(toRemove);
	}
	
	public void releaseCoinChange() throws DisabledException{
		ArrayList<BigDecimal> toRemove = new ArrayList<BigDecimal>();
		for(BigDecimal denomination : coinsToReturn) {
			if(!coinPause) {
				try {
					coinDispensers.get(denomination).emit();
					toRemove.add(denomination);
				}
				catch(OverloadException ex) {
					System.out.println("Please empty coin tray to collect rest of change.");
					coinPause = true;
				}
				catch(EmptyException ex) {/*Shouldn't happen*/}
			}
		}
		coinsToReturn.removeAll(toRemove);
	}
	
	public Banknote takeBanknote() {
		Banknote banknote = station.banknoteOutput.removeDanglingBanknote();
		if(banknote != null)
			System.out.println("Taking $"+banknote.getValue()+" Ejected Banknote...\n");
		return banknote;
	}
	
	public List<Coin> takeCoins() {
		List<Coin> coins = station.coinTray.collectCoins();
		
		for(Coin coin : coins) {
			if(coin != null) {
				System.out.println("Taking $"+coin.getValue()+" Coin from coin tray...");
				coinPause = false;
			}
		}
			
		return coins;
	}
	
	public int changePending() {
		return coinsToReturn.size() + banknotesToReturn.size();
	}
	
	private void printChange(ArrayList<Integer> banknotesToReturn, ArrayList<BigDecimal> coinsToReturn) {
		System.out.print("Banknote Change: ");
		for(Integer denomination: banknotesToReturn)
			System.out.print(denomination+", ");
		System.out.println();
		
		System.out.print("Coin Change: ");
		for(BigDecimal denomination: coinsToReturn)
			System.out.printf("%.2f ", denomination.doubleValue());
		System.out.println();
	}
	
	private BanknoteSlotListener createBanknoteListener() {
		return new BanknoteSlotListener() {

			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

			@Override
			public void banknoteInserted(BanknoteSlot slot) {}

			@Override
			public void banknoteEjected(BanknoteSlot slot) {
				System.out.println("Ejecting Banknote...");
				banknotePause = true;
			}

			@Override
			public void banknoteRemoved(BanknoteSlot slot) {
				System.out.println("Ejected Banknote Removed...");
				banknotePause = false;
			}
		};
	}
	
	private CoinTrayListener createCoinTrayListener() {
		return new CoinTrayListener() {

			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

			@Override
			public void coinAdded(CoinTray tray) {
				System.out.println("Ejecting Coin...");
			}
		};
	}
	
	private int[] reverseOrder(int[] array) {
		int[] tempArray = new int[array.length];
		int index = 0;
		for(int i = array.length-1; i >= 0; i--) {
			tempArray[index] = array[i];
			index++;
		}
		return tempArray;
	}
	
	private List<BigDecimal> reverseOrder(List<BigDecimal> array) {
		List<BigDecimal> tempArray = new ArrayList<BigDecimal>(array);
		tempArray.sort(Collections.reverseOrder());
		return tempArray;
	}
}
