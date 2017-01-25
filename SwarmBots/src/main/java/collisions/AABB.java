package collisions;

import main.Vector2D;

public final class AABB {
	
	public final Vector2D Location,Size;
	
	public AABB(Vector2D Loc,Vector2D S){
		Location=Loc;
		Size=S;
	}
	
	public final double getX(){
		return Location.X;
	}
	
	public final double getY(){
		return Location.Y;
	}
	
	public final double getWidth(){
		return Size.X;
	}
	
	public final double getHeight(){
		return Size.Y;
	}
	
	public final Vector2D getCenter(){
		return Location.add(Size.mul(0.5d));
	}
	
	public final boolean getInside(Vector2D Point){
		return (Point.X>=Location.X&&Point.Y>=Location.Y&&Point.X<=Location.X+Size.X&&Point.Y<=Location.Y+Size.Y);
	}
	
	public final String toString(){
		return '{'+Location.toString()+'|'+Size.toString()+'}';
	}
}
