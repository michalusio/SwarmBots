package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;

import main.Vector2D;

public class GraphicLabel extends GraphicSizeableComponent {

	public String text;
	public Color FontColor;
	public Font Font;
	public boolean clipping;
	
	public GraphicLabel(){
		this(new Vector2D(0,0));
	}
	
	public GraphicLabel(Vector2D size){
		super(size);
		clipping=true;
		Font=new Font("Arial",java.awt.Font.PLAIN,10);
		FontColor=Color.BLACK;
	}
	
	public GraphicLabel(Vector2D pos, Vector2D size, String caption, Color c){
		super(size);
		FontColor=c;
		clipping=true;
		text=caption;
		Position=pos;
		Font=new Font("Arial",java.awt.Font.PLAIN,10);
	}
	
	@Override
	public void drawComponent(Graphics2D gr, Vector2D offset) {
		if (Visible){
			Shape cl=gr.getClip();
			if (clipping) gr.clipRect((int)(Position.X+offset.X), (int)(Position.Y+offset.Y), (int)Size.X, (int)Size.Y);
			if (text!=null){
				gr.setFont(Font);
				gr.setColor(FontColor);
				gr.drawString(text, (int)(offset.X+Position.X),(int)(offset.Y+Position.Y+Font.getSize()));
			}
			gr.setClip(cl);
		}
	}

	@Override
	public boolean onMouseClick(Vector2D mousePos) {
		return false;
	}

}
