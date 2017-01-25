package gui;

import java.awt.Graphics2D;

import main.Vector2D;

public abstract class GraphicComponent {
	public boolean Visible;
	public Vector2D Position;
	
	public GraphicComponent(){
		Position=new Vector2D(0,0);
		Visible=true;
	}

	public void drawComponent(Graphics2D gr){
		drawComponent(gr,new Vector2D(0,0));
	}
	public abstract void drawComponent(Graphics2D gr, Vector2D offset);
	public abstract boolean onMouseClick(Vector2D mousePos);
}
