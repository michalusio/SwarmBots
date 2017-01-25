package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;

import main.Vector2D;

public class GraphicButton extends GraphicSizeableComponent {
	public IGraphicalAction Action;
	public String text="Button";
	public Color FontColor;
	public Font Font;
	public Color BgColor=Color.LIGHT_GRAY;
	public boolean clipping;
	
	public GraphicButton(){
		Font=new Font("Arial",java.awt.Font.PLAIN,10);
		FontColor=Color.BLACK;
	}
	
	public GraphicButton(Vector2D pos, Vector2D size,String title, IGraphicalAction action){
		Size=size;
		Position=pos;
		Action=action;
		text=title;
		Font=new Font("Arial",java.awt.Font.PLAIN,10);
		FontColor=Color.BLACK;
	}
	
	public GraphicButton(IGraphicalAction action){
		super();
		Action=action;
		Font=new Font("Arial",java.awt.Font.PLAIN,10);
		FontColor=Color.BLACK;
	}
	
	@Override
	public void drawComponent(Graphics2D gr, Vector2D offset) {
		if (Visible){
			Shape cl=gr.getClip();
			if (clipping) gr.clipRect((int)(Position.X+offset.X), (int)(Position.Y+offset.Y), (int)Size.X, (int)Size.Y);
			gr.setColor(BgColor);
			gr.fill3DRect((int)(Position.X+offset.X), (int)(Position.Y+offset.Y), (int)Size.X, (int)Size.Y, true);
			if (text!=null){
				gr.setColor(FontColor);
				gr.setFont(Font);
				gr.drawString(text, (int)(Position.X+offset.X+(Size.X-gr.getFontMetrics().stringWidth(text))*0.5), (int)(Position.Y+offset.Y+(Size.Y+Font.getSize())*0.5));
			}
			gr.setClip(cl);
		}
	}

	@Override
	public boolean onMouseClick(Vector2D mousePos) {
		if (Visible&&mousePos.isMoreThan(Position)&&mousePos.isLessThan(Position.add(Size))){
			if (Action!=null) Action.process();
			return true;
		}
		return false;
	}

}
