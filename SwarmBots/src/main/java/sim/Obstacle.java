package sim;

import java.awt.Image;

import collisions.CollisionShape;

public abstract class Obstacle {
	protected CollisionShape cs;
	protected boolean Movable=true;
	protected Image ObstacleImage=null;
	
	public Image getImage(){
		return ObstacleImage;
	}
	
	public boolean getMovable(){
		return Movable;
	}
	
	public CollisionShape getShape(){
		return cs;
	}
}
