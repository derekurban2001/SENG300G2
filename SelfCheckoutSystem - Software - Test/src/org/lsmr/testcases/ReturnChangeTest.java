package org.lsmr.testcases;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.*;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.usecases.ReturnChange;

public class ReturnChangeTest {
	
	SelfCheckoutStation station;
	Currency currency;
	int[] banknoteDenominations;
	BigDecimal[] coinDenominations;
	
	@Before
	public final void setup() {
		currency = Currency.getInstance(Locale.CANADA);
		banknoteDenominations = new int[]{100, 50, 20, 10, 5};
		coinDenominations = new BigDecimal[]{cBigDecimal(2.0), cBigDecimal(1.0), cBigDecimal(0.25), cBigDecimal(0.10), cBigDecimal(0.05)};
		int scaleMaximumWeight = 10000;
		int scaleSensitivity = 1;
		
		this.station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, scaleMaximumWeight, scaleSensitivity);
	}
	
	@Test
	public final void testCorrectChange() {
		System.out.println("\n********** Running test \"testCorrectChange\" **********");
		ReturnChange change = new ReturnChange(station);
		
		loadAllDispensers();
		try {
			change.calculateChange(260.70);
			
			while(change.changePending() > 0) {
				change.releaseBanknoteChange();
				change.takeBanknote();
				
				change.releaseCoinChange();
				change.takeCoins();
			}
		}
		catch(Exception ex) {ex.printStackTrace();};
	}
	
	@Test
	public final void testCorrectChange2() {
		System.out.println("\n********** Running test \"testCorrectChange2\" **********");
		ReturnChange change = new ReturnChange(station);
		
		loadAllDispensers();
		try {
			change.calculateChange(660.70);
			
			while(change.changePending() > 0) {
				change.releaseBanknoteChange();
				change.takeBanknote();
				
				change.releaseCoinChange();
				change.takeCoins();
			}
		}
		catch(Exception ex) {ex.printStackTrace();};
		
	}
	
	private final void loadDispenser(BigDecimal denomination){
		int size = 10;
		Coin[] coins = new Coin[size];
		for(int i = 0; i < size; i++)
			coins[i] = new Coin(denomination, currency);
		try {
			station.coinDispensers.get(denomination).load(coins);
		}
		catch(OverloadException ex) {/*Do nothing*/}
	}
	
	private final void loadDispenser(int denomination){
		int size = 10;
		Banknote[] coins = new Banknote[size];
		for(int i = 0; i < size; i++)
			coins[i] = new Banknote(denomination, currency);
		
		try {
			station.banknoteDispensers.get(denomination).load(coins);
		}
		catch(OverloadException ex) {/*Do nothing*/}
	}
	
	private final void loadCoinDispensers() {
		for(BigDecimal denomination : coinDenominations)
			loadDispenser(denomination);
	}
	
	private final void loadBanknoteDispensers() {
		for(int denomination : banknoteDenominations)
			loadDispenser(denomination);
	}
	
	private final void loadAllDispensers(){
		loadCoinDispensers();
		loadBanknoteDispensers();
	}
	
	private final BigDecimal cBigDecimal(double value) {
		return BigDecimal.valueOf(value);
	}
}
