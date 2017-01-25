package gui;


import main.Vector2D;

public class Camera {
	private Vector2D Position, Size, HSize;
	private double Zoom;
	
	public Camera(Vector2D Pos, Vector2D Siz, double Z){
		Position=Pos;
		Size=Siz;
		HSize=Siz.mul(0.5d);
		Zoom=Z;
	}
	
	public Vector2D getPosition(){
		return Position;
	}
	
	public Vector2D getHalfSize(){
		return HSize;
	}
	
	public Vector2D getSize(){
		return Size;
	}
	
	public double getZoom(){
		return Zoom;
	}
	
	public void setPosition(Vector2D newPos){
		Position=newPos;
	}
	
	public void setSize(Vector2D newSize){
		Size=newSize;
		HSize=newSize.mul(0.5d);
	}
	
	public void setZoom(double newZoom){
		Zoom=newZoom;
	}
}
