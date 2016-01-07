package org.com.essex.ec910.artificialstockmarket.trader;

import java.util.ArrayList;

import org.com.essex.ec910.artificialstockmarket.market.ArtificialMarket;
import org.com.essex.ec910.artificialstockmarket.market.Order;


/**
 * @author Pouyan
 *
 */

public abstract class AbstractTrader {

	//	general parameters of trader
	public String name;                         // trader's name eg: pouyan, Jonathan, randomTrader and ...
	public ArtificialMarket artificialMarket;   // trader has access to artificial market
	public Portfolio portfolio;                 // current portfolio of trader
	public Portfolio lastPortfolio;             // last portfolio of trader before sending the final oder to market (memory of trader)
	public Order lastOrder;                     // last final order of trader that has been double checked and sent to market (memory of trader)  
	public long max_buy;                        // maximum amount of shares that trader can buy      
	public long max_sell;                       // minimum amount of shares that trader can sell

	//  metrics for measuring the performance of trading strategy
	public int profit_loss;                     // P&L of trader
	public int ROI;                             // return of investment 
	public int numTrades;                       // number of total trades
	public int numWinTrades;                    // number of winning trades
	public int winningRate;                     // = numWinTrades / numTrades
    public double profitFactor;                 // = total profit / total loss
    
	private int transactionCounter = 0;
	private double comFee;
	
	/**
	 * Constructor
	 * @param name 
	 * @param artificialMarket  
	 * @param portfolio  
	 * @param max_buy
	 * @param max_sell
	 */
	public AbstractTrader(String name, ArtificialMarket artificialMarket, Portfolio portfolio, long max_buy , long max_sell) {
		this.name = name;
		this.artificialMarket = artificialMarket;
		this.portfolio = portfolio;
		this.max_buy = max_buy;
		this.max_sell = max_sell;
	}

	//	run your trading strategy (Override this function)
	//  if you want to send order to market, you should not directly send your order, 
	//  because it should be double checked by sendFinalOrderToMarket() 
	//  if you do not want to send order to market, make sure your order volume is 0    
	public Order runStrategy(){

		Order order; 

		// your trading algorithm ...

		order = new Order(0, 0, 0, 0, this);// default order which will not be sent to the market (because volume = 0)
		return order;
	}
	
	
	//  Final check to make sure that trader sends correct order to market 
	//	also this function, updates the performance results of trader (???) 
	//	please do not change this function
	public void sendFinalOrderToMarket(){
		
		Order order = runStrategy();
		Integer[] spotPrice = this.artificialMarket.getLastNPrice(1);
		
		//check volume of order
		if (order.getVolume() > 0   		                                           // check that Volume > 0, otherwise, it means that trader does not want to send order to market
			&&((order.getType1() == Order.BUY && order.getVolume()<= this.max_buy )   // check that if trader wants to buy assets, the volume of his order <= limit to buy
			||(order.getType1() == Order.SELL && order.getVolume()<= this.max_sell))){    // check that if trader wants to buy assets, the volume of his order <= limit to sell 
			
			//check order before sending it to artificial market in order to make sure it is a valid order
//			if((order.getType1() == Order.BUY && order.getType2() == Order.LIMIT && order.getVolume() <= this.max_buy && this.portfolio.getMoney() >= order.getVolume()*order.getLimitprice()) ||     //  buy limit order --> (trader should have enough money to buy shares) trader wants to buy and volume should be < max limit for buying
//					(order.getType1() == Order.BUY && order.getType2() == Order.MARKET && order.getVolume() <= this.max_buy && this.portfolio.getMoney() >= order.getVolume()*spotPrice)  ||            // ??? put [0] in front spot price when using Jonathan market ???? buy market order --> (trader should have enough money to buy shares)
//					(order.getType1() == Order.SELL && order.getVolume() <= this.max_sell && order.getVolume() <= this.portfolio.getShares())) {//  sell order --> trader wants to sell and volume < max limit for selling // also trader should have enough shares in his portfolio in order to be able to sell the desired volume 
            
			//final verification of order
//			if((order.getType1() == Order.BUY && this.getInvestableMoney() >= order.getVolume()*this.artificialMarket.getSpotPrice())  // check that if trader wants to buy an asset, he should have enough money in his portfolio
//			    ||(order.getType1() == Order.SELL && order.getVolume() <= this.portfolio.getShares())){                                // check that if trader wants to sell assets,  he can not sell more than the number of shares in his portfolio
				
				if(!(this instanceof RandomTrader))
				{
					System.out.println(this.name + ": " + order.toString());
				}
			
				if(order.getType1() == Order.SELL)
				{
					this.transactionCounter++;
				}				
				
				this.artificialMarket.reciveOrder(order); // send a valid order to artificial market
				this.lastOrder = order;                   // save final order sent to market
				this.lastPortfolio = this.portfolio;      // save the last portfolio of trader when final order sent to market
//			}//if check
		}

		  	 
		// update the performance results of trader
		// ???   	 
	}

	//  reduce shares from portfolio of trader and increase the money 
	//  to be used by artificial market, not by trader  
	public void buyShareFromTrader(long money, long shares){
		
		//adjustment by commission fee
		double m = this.portfolio.getMoney() + money*(1-comFee);
		
		this.portfolio.setMoney(m);
		this.portfolio.setShares(this.portfolio.getShares() - shares);
		
	}

	//  reduce money from portfolio of trader and increase the shares
	//  to be used by artificial market, not by trader 
	public void sellShareToTrader(long money, long shares){
		
		//adjustment by commission fee
		double m = this.portfolio.getMoney() - money*(1+comFee);

		this.portfolio.setMoney(m);
		this.portfolio.setShares(this.portfolio.getShares() + shares);
		
	}
	
	/**
	 * calculates actual value of portfolio by money and shares value
	 * @return
	 */
	public double getPortfolioValue()
	{
		return portfolio.getMoney() + portfolio.getShares()*artificialMarket.getSpotPrice();
	}
	
	public String toString()
	{
		return name;
	}
	
	/**
	 * 
	 * @param comFee
	 */
	public void setCommissionFee(final double comFee)
	{
		this.comFee = comFee;
	}
	
	/**
	 * calculates money available for investment with respect to commission fee
	 * @return
	 */
	protected double getInvestableMoney()
	{
		return portfolio.getMoney() * (1-comFee);
	}
	
	/**
	 * A transaction is defined as buy and sell and is counted after the sell order (even if the sell order isn't executed) 
	 * @return - number of transactions done by the trader
	 */
	public int getTransactionCount()
	{
		return transactionCounter;
	}
	
}

