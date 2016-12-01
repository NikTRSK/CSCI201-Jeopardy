package client;

import resource.Resource;

public final class FactoryResourceFactory {
  
  static FactoryResourceFactory _instance;
  
  static {
    _instance = new FactoryResourceFactory();
  }
  
  public static FactoryResourceFactory get() {
    return _instance;
  }
  
  public FactoryResource makeFactoryResource(Resource inResource) {
    if(inResource.getName().equals("Dining Room")) return new DiningRoom(inResource);
    else if (inResource.getName().equals("Coffee Shop")) return new CoffeeShop(inResource);
    else return new FactoryResource(inResource);
  }
}
