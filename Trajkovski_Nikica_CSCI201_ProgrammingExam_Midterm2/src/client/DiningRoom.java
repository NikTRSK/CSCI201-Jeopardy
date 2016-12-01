package client;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.Semaphore;

import resource.Resource;

public class DiningRoom extends FactoryResource {
  
  Semaphore numberOfSpots;
  
  DiningRoom(Resource inResource) {
    super(inResource);
    numberOfSpots = new Semaphore(3);
  }

  @Override
  public void draw(Graphics g, Point mouseLocation) {
    super.draw(g, mouseLocation);
  }
  
  public void takeOne() throws InterruptedException {
    if (numberOfSpots.tryAcquire();
    super.takeResource(1);
//    Thread.sleep(5000);
//    super.takeResource(-1);
//    numberOfSpots.release();
  }
  
  public void leaveOne() {
    super.takeResource(-1);
    numberOfSpots.release();
  }
}
