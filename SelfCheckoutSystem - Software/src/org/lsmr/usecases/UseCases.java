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
	double baggingAreaWeight = 0.0;
	public double weightInGrams;
	Item currentItem;
	BigDecimal totalPrice;
	boolean PuttingItemInBag;
	boolean overloading;
	public SelfCheckoutStation station;
	
	public UseCases() {
		Currency currency = Currency.getInstance("CAD");
		int[] banknoteDenominations = {5, 10, 20, 50, 100};
		BigDecimal[] coinDenominations = {new BigDecimal(0.05), new BigDecimal(0.10), new BigDecimal(0.25), new BigDecimal(1.0), new BigDecimal(2.0)};
		int scaleMax = 75;
		int scaleSensitivity = 1;
		station = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, scaleMax, scaleSensitivity);
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
	
	// **move currentItem to BagItem class if it isn't used by any other classes
	public void setCurrentItem(Item i) {
        this.currentItem = i;
    }
	
	// move currentItem to BagItem class if it isn't used by any other classes
	public void setPuttingItemInBag (boolean p) {
       this.PuttingItemInBag = p;
    }
  
	public void updateNumberOfItems (int numItems) {
		this.numberOfItems += numItems;
	}
	
	public void setBaggingAreaWeight (double weight) {
		this.baggingAreaWeight = weight;
	}
	
	public void updateBaggingAreaWeight (double weight) {
		this.baggingAreaWeight += weight;
	}
	
	public void setOverloading(boolean p) { 
		overloading = p; 
	}
	
	// if the bagging are is overload; no more weight can be added
    public boolean getOverloading() { 
    	return overloading; 

	}
	// Getter methods.
	public ArrayList<BarcodedProduct> getCart() {
		return cart;
	}
	
	public BigDecimal getTotalPrice() {
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
	
	public double getBaggingAreaWeight() {
        return baggingAreaWeight;
    }
	
	
	// **move currentItem to BagItem class if it isn't used by any other classes
	public double getCurrentWeight() {
		return weightInGrams;
	}
	
	// **move currentItem to BagItem class if it isn't used by any other classes
    public boolean getPuttingItemInBag() {
    	return PuttingItemInBag;
    }
}
