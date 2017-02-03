package main;


import java.awt.geom.Point2D;

public final class Vector2D {
	public double X,Y;
	
	public Vector2D(){
		this(0,0);
	}
	
	public Vector2D(double a){
		this(a,a);
	}
	
	public Vector2D(double x,double y){
		X=x;
		Y=y;
	}
	
	public final Vector2D add(Vector2D b){
		return new Vector2D(X+b.X,Y+b.Y);
	}
	
	public final Vector2D sub(Vector2D b){
		return new Vector2D(X-b.X,Y-b.Y);
	}
	
	public final Vector2D mul(Vector2D b){
		return new Vector2D(X*b.X,Y*b.Y);
	}
	
	public final Vector2D div(Vector2D b){
		return new Vector2D(X/b.X,Y/b.Y);
	}
	
	public final Vector2D mul(double b){
		return new Vector2D(X*b,Y*b);
	}
	
	public final Vector2D div(double b){
		return mul(1/b);
	}
	
	public final double len(){
		return Math.sqrt(lenSqr());
	}
	
	public final double lenSqr(){
		return dot(this);
	}
	
	public final double distance(Vector2D b){
		return Math.sqrt(distanceSqr(b));
	}
	
	public final double distanceSqr(Vector2D b){
		return this.sub(b).lenSqr();
	}
	
	public final static Vector2D interpolate(Vector2D p1, Vector2D p2, double a){
		return new Vector2D((1-a)*p1.X + a*p2.X,(1-a)*p1.Y + a*p2.Y);
	}
	
	public final double angle(){
		return Math.atan2(Y, X);
	}
	
	public final double angleBetween(Vector2D b){
		return Math.atan2(cross(b),dot(b));
	}
	
	public final Vector2D rotate(double angle){
		angle=-angle;
		double Sin=Math.sin(angle);
		double Cos=Math.cos(angle);
		return new Vector2D(X*Cos-Y*Sin,X*Sin+Y*Cos);
	}
	
	public final double dot(Vector2D b){
		return X*b.X+Y*b.Y;
	}
	
	public final double cross(Vector2D b){
		return X*b.Y-Y*b.X;
	}
	
	public final boolean isLessThan(Vector2D b){
		return X<b.X&&Y<b.Y;
	}
	
	public final boolean isMoreThan(Vector2D b){
		return X>b.X&&Y>b.Y;
	}
	
	public final String toString(){
		return toString("%1$.0f|%2$.0f");
	}
	
	public final String toString(String format) {
		return String.format(format,X,Y);
	}

	public static Vector2D fromPoint2D(Point2D p1) {
		return new Vector2D(p1.getX(),p1.getY());
	}
}
