package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import resource.Resource;

public class CoffeeShop extends FactoryResource {
  
//  Semaphore numberOfSpots;
  ArrayList<String> orders;
  
  CoffeeShop(Resource inResource) {
    super(inResource);
//    numberOfSpots = new Semaphore(3);
    orders = new ArrayList<String>();
  }

  @Override
  public void draw(Graphics g, Point mouseLocation) {
    super.draw(g, mouseLocation);
    g.setColor(Color.BLACK);
//    g.drawString(mResource.getQuantity()+"", centerTextX(g,mResource.getQuantity()+""), centerTextY(g));
    g.drawString("", centerTextX(g,""), centerTextY(g));
  }
  
  public void addOrder(String order) {
    System.out.println("adding... " + order);
    orders.add(order);
  }
  
  public ArrayList<String> getAllOrder() {
    return orders;
  }
//  public void takeOne() throws InterruptedException {
////    numberOfSpots.acquire();
//    super.takeResource(1);
//  }
//  
//  public void leaveOne() {
//    super.takeResource(-1);
////    numberOfSpots.release();
//  }
}
