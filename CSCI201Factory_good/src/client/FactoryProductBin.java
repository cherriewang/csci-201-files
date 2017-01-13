package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Queue;

import libraries.ImageLibrary;
import resource.Product;

public class FactoryProductBin extends FactoryObject{
	
	private Queue<Product> products;
	private int num;
	
	public FactoryProductBin(){
		super(new Rectangle(0,3, 1, 1));
		products = new LinkedList<Product>();
		num = 0;
		mLabel = "ProductBin";
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "productbin" + Constants.png);
	}
	
	public void addProduct(Product p){
		products.add(p);
		num++;
	}
	@Override
	public void draw(Graphics g, Point mouseLocation) {
		super.draw(g, mouseLocation);
		g.setColor(Color.BLACK);
		g.drawString(Integer.toString(num), centerTextX(g, Integer.toString(num)), centerTextY(g));
	}
	
	public Product removeProduct(){
		while (products.isEmpty()){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		num--;
		return products.remove();
	}
	

}
