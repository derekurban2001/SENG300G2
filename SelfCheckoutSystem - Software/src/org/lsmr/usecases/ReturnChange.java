package org.lsmr.usecases;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
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
	/* Global variables */
	private boolean banknotePause = false;
	private boolean debug = false;
	private boolean coinPause = false;
	private SelfCheckoutStation station;
	private ArrayList<BigDecimal> coinsToReturn = new ArrayList<BigDecimal>();
	private ArrayList<Integer> banknotesToReturn = new ArrayList<Integer>();
	private Map<BigDecimal, CoinDispenser> coinDispensers;
	private Map<Integer, BanknoteDispenser> banknoteDispensers;
	private int[] banknoteDenominations;
	private List<BigDecimal> coinDenominations;
	
	/*
	 * Constructor that takes in an initialized SelfCheckoutStation and registers
	 * listeners for ReturnChange.java. Also stores denominations in reverse order.
	 */
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
	
	/*
	 * Calculates the total change to be dispensed based off what bank notes/coins are available.
	 * If change cannot be returned, throws a simulation exception. Rounds to nearest $0.05
	 * (since pennies are discontinued). Then prints the change to be dispensed.
	 */
	public void calculateChange(double changeTotal) throws OverloadException{
		if(changeTotal < 0)
			throw new SimulationException("Can't calculate change with a negative number");
		
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
				
				total = round(total - denomination*banknoteCount, 2);
			}
		}
		
		for(BigDecimal denomination : coinDenominations) {
			double value = round(denomination.doubleValue(), 2);
			
			if(round(total - value, 2) >= 0 && coinDispensers.get(denomination).size() > 0) {
				double coinCount = 1;
				while(round(total - value*coinCount, 2) >= 0 && coinDispensers.get(denomination).size() >= coinCount) {
					coinsToReturn.add(denomination);
					coinCount++;
				}
				coinCount--;
				total = round(total - value*coinCount, 2);
			}
		}
		
		if(total >= lowestDenomination())
			throw new SimulationException("Cannot return change, please re-stock change dispensers!");
		
		if(debug)
			printChange(banknotesToReturn, coinsToReturn);
	}
	
	/*
	 * Releases one of the bank notes to be dispensed, will not release more until
	 * dispensed bank note is taken
	 */
	public void releaseBanknoteChange() throws DisabledException{
		ArrayList<Integer> toRemove = new ArrayList<Integer>();
		for(Integer denomination : banknotesToReturn) {
			if(!banknotePause) {
				try {
					banknoteDispensers.get(denomination).emit();
					toRemove.add(denomination);
				}
				catch(OverloadException ex) {
					if(debug)
						System.out.println("Please remove banknote to collect rest of change.");
					banknotePause = true;
				}
				catch(EmptyException ex) {/*Shouldn't happen*/}
			}
		}
		
		for(Integer banknote : toRemove)
			banknotesToReturn.remove(banknote);
	}
	
	/*
	 * Releases the coins to be dispensed, stops releasing coins if the coin
	 * tray is full.
	 */
	public void releaseCoinChange() throws DisabledException{
		ArrayList<BigDecimal> toRemove = new ArrayList<BigDecimal>();
		for(BigDecimal denomination : coinsToReturn) {
			if(!coinPause) {
				try {
					coinDispensers.get(denomination).emit();
					toRemove.add(denomination);
				}
				catch(OverloadException ex) {
					if(debug)
						System.out.println("Please empty coin tray to collect rest of change.");
					coinPause = true;
				}
				catch(EmptyException ex) {/*Shouldn't happen*/}
			}
		}
		for(BigDecimal coin : toRemove)
			coinsToReturn.remove(coin);
	}
	
	/*
	 * This function takes the bank note from the slot (user action)
	 */
	public Banknote takeBanknote() {
		Banknote banknote = station.banknoteOutput.removeDanglingBanknote();
		if(banknote != null)
			if(debug)
				System.out.println("Taking $"+banknote.getValue()+" Ejected Banknote...\n");
		return banknote;
	}
	
	/*
	 * This function takes the coins from the coin tray (user action)
	 */
	public List<Coin> takeCoins() {
		List<Coin> coins = station.coinTray.collectCoins();
		
		ArrayList<Coin> fillList = new ArrayList<Coin>();
		
		for(Coin coin : coins) {
			if(coin != null) {
				fillList.add(coin);
				if(debug)
					System.out.println("Taking $"+coin.getValue()+" Coin from coin tray...");
				coinPause = false;
			}
		}
			
		return fillList;
	}
	
	/*
	 * Returns the number of bank notes or coins waiting to be dispensed
	 */
	public int pendingChange() {
		return coinsToReturn.size() + banknotesToReturn.size();
	}
	
	/*
	 * Rounds a given decimal to "places" number of digits
	 */
	private double round(double value, int places) {
		return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
	}
	
	/*
	 * Prints the total amount of change to be dispensed
	 */
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
	
	/*
	 * Creates the BanknoteSlotListener listener used by this software.
	 */
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
				if(debug)
					System.out.println("Ejecting Banknote...");
				banknotePause = true;
			}

			@Override
			public void banknoteRemoved(BanknoteSlot slot) {
				if(debug)
					System.out.println("Ejected Banknote Removed...");
				if(banknotePause) {
					banknotePause = false;
					try {
						releaseBanknoteChange();
					}
					catch(DisabledException ex) {
						throw new SimulationException("Can't release change, BanknoteSlot is disabled!");
					}
				}
			}
		};
	}
	
	/*
	 * Creates the CoinTrayListener listener used by this software.
	 */
	private CoinTrayListener createCoinTrayListener() {
		return new CoinTrayListener() {

			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

			@Override
			public void coinAdded(CoinTray tray) {
				if(debug)
					System.out.println("Ejecting Coin...");
			}
		};
	}
	
	/*
	 * Takes the given int[] array and returns a new array in reverse order
	 */
	private int[] reverseOrder(int[] array) {
		int[] tempArray = new int[array.length];
		int index = 0;
		for(int i = array.length-1; i >= 0; i--) {
			tempArray[index] = array[i];
			index++;
		}
		return tempArray;
	}
	
	/*
	 * Takes the given BigDecimal List and returns a List in reverse order
	 */
	private List<BigDecimal> reverseOrder(List<BigDecimal> array) {
		List<BigDecimal> tempArray = new ArrayList<BigDecimal>(array);
		tempArray.sort(Collections.reverseOrder());
		return tempArray;
	}
	
	/*
	 * Returns the lowest denomination of the bank notes and the coins
	 */
	private double lowestDenomination() {
		double lowestDenom = 0;
		
		if(banknoteDenominations.length != 0)
			lowestDenom = banknoteDenominations[0];
		
		if(coinDenominations.size() != 0)
			lowestDenom = coinDenominations.get(0).doubleValue();
		
		for(int denomination : banknoteDenominations)
			if((double)denomination < lowestDenom)
				lowestDenom = (double)denomination;
		
		for(BigDecimal denomination : coinDenominations)
			if(denomination.doubleValue() < lowestDenom)
				lowestDenom = denomination.doubleValue();
		
		return lowestDenom;
	}
	
	/*
	 * Enables print statements for debugging
	 */
	public void enableDebugMode() {
		debug = true;
	}
	
	/*
	 * Disables print statements for debugging
	 */
	public void disableDebugMode() {
		debug = false;
	}
}
