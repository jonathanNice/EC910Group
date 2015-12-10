package org.com.essex.ec910.artificialstockmarket.datasource;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Jonathan
 *Object for wrapping a historical price
 *inlcuding volume and date
 *price gets casted to an integer
 */
public class HistoricalPrice {

	private Date date;
	private int price;
	private int volume;
		
	
	/**
	 * @param date
	 * @param price
	 * @param volume
	 */
	public HistoricalPrice(String date, double price, double volume) {
		
		try {
				this.date = (Date) new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
		this.price = (int) Math.round(price*100);
		this.volume = (int) volume;
	}


	public Date getDate() {
		return date;
	}


	public int getPrice() {
		return price;
	}


	public int getVolume() {
		return volume;
	}


	@Override
	public String toString() {
		return "HistoricalPrice [date=" + date + ", price=" + price + ", volume=" + volume + "]";
	}
	

}