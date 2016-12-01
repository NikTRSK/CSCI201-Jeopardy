package client;

import java.awt.Rectangle;

import libraries.ImageLibrary;

public class FactoryWall2 extends FactoryObject{

	protected FactoryWall2(Rectangle inDimensions) {
		super(inDimensions);
			mImage = ImageLibrary.getImage("resources/img/Wall2.png");
	}

}
