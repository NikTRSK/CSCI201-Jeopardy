package client;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.Semaphore;

import resource.Resource;

public class DiningRoom extends FactoryResource {
  
  Semaphore numberOfSpots;
  
  boolean available = true;
  
  DiningRoom(Resource inResource) {
    super(inResource);
    numberOfSpots = new Semaphore(3);
  }

  @Override
  public void draw(Graphics g, Point mouseLocation) {
    super.draw(g, mouseLocation);
  }
  
  public boolean takeOne() throws InterruptedException {
//    numberOfSpots.tryAcquire();
    if (numberOfSpots.tryAcquire()) {
      super.takeResource(1);
      available = true;
      return true;
    }
    else {
      available = false;
      return false;
    }
//    Thread.sleep(5000);
//    super.takeResource(-1);
//    numberOfSpots.release();
  }
  
  public void leaveOne() {
    super.takeResource(-1);
    numberOfSpots.release();
  }
}
