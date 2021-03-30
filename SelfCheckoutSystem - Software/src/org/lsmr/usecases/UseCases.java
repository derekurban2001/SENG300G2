package org.lsmr.usecases;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class UseCases {
	public ArrayList<BarcodedProduct> cart = new ArrayList<BarcodedProduct>();
	BigDecimal cartTotal = new BigDecimal(0.0);
	BigDecimal amountOwed = new BigDecimal(0.0);
	int numberOfItems = 0;
	public double previousWeight = 0.0;
	public double currentWeight = 0.0;
	Item currentItem;
	BigDecimal totalPrice;
	boolean itemBagged;
	boolean overloading;
	public SelfCheckoutStation station;
	
	public UseCases() {
		Currency currency = Currency.getInstance("CAD");
		int[] banknoteDenominations = {5, 10, 20, 50, 100};
		BigDecimal[] coinDenominations = {new BigDecimal(0.05), new BigDecimal(0.10), new BigDecimal(0.25), new BigDecimal(1.0), new BigDecimal(2.0)};
		int weightLimitInGrams = 30000;
		int scaleSensitivity = 1;
		station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, weightLimitInGrams, scaleSensitivity);
		
	}
	
	// Setter methods.
	public void addToCart (BarcodedProduct barcodedProduct) {
		this.cart.add(barcodedProduct);
	}
	
	public void setCartTotal (BigDecimal total) {
		this.cartTotal = total;
	}
	
	public void updateCartTotal (BigDecimal itemPrice) {
		this.cartTotal = this.cartTotal.add(itemPrice);
	}
	
	public void setAmountOwed (BigDecimal amount) {
		this.amountOwed = amount;
	}
	
	public void updateAmountOwed (BigDecimal amount) {
		this.amountOwed = this.amountOwed.add(amount);
	}
	
	public void setNumberOfItems (int numItems) {
		this.numberOfItems = numItems;
	}
	
	public void setCurrentItem(Item i) {
        this.currentItem = i;
    }
	
	public void setItemBagged (boolean b) {
       this.itemBagged = b;
    }
  
	public void updateNumberOfItems (int numItems) {
		this.numberOfItems += numItems;
	}
	
	public void setOverloading(boolean p) { 
		this.overloading = p; 
	}
	
	public void setPreviousWeight (double weight) {
		this.previousWeight = weight;
	}
	
	public void setCurrentWeight (double weight) {
		this.currentWeight = weight;
	}
	
	// Getter methods.
    public boolean getOverloading() { 
    	return overloading; 
	}
    
	public ArrayList<BarcodedProduct> getCart() {
		return cart;
	}
	
	public BigDecimal getTotalPrice() {
		return totalPrice;
		
	}
	public  BigDecimal getFinal(ArrayList<BarcodedProduct> productList) {
		return totalPrice;
	}	
	
	public Item getCurrentItem() {
        return currentItem;
    }
	public BigDecimal getCartTotal() {
		return cartTotal;
	}
	
	public BigDecimal getAmountOwed() {
		return amountOwed;
	}
	
	public int getNumberOfItems() {
		return numberOfItems;
	}
	
	public double getPreviousWeight() {
		return previousWeight;
	}
	
    public boolean getItemBagged() {
    	return itemBagged;
    }
    
    public double getCurrentWeight() {
    	return currentWeight;
    }
}
