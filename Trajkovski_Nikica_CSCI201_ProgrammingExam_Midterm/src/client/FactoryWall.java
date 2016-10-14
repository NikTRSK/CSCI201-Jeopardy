package client;

import java.awt.Rectangle;

import libraries.ImageLibrary;

public class FactoryWall extends FactoryObject{

	protected FactoryWall(Rectangle inDimensions) {
		super(inDimensions);
			mImage = ImageLibrary.getImage("resources/img/Wall.png");
	}
	
	public FactoryWall(Rectangle inDimensions, String file) {
		super(inDimensions);
			mImage = ImageLibrary.getImage(file);
	}

}
