package gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import main.Vector2D;

public class GraphicList extends GraphicSizeableComponent{
	
	private final List<String> textLines;
	private int scroll;
	
	public Color BgColor,FontColor;
	public Font Font;
	
	public GraphicList(){
		super();
		textLines=new ArrayList<String>();
		Font=new Font("Arial",java.awt.Font.PLAIN,10);
		BgColor=Color.WHITE;
		FontColor=Color.BLACK;
		scroll=0;
	}

	public void removeAll() {
		textLines.clear();
		scroll=0;
	}

	public void add(String s) {
		textLines.add(s);
	}

	@Override
	public void drawComponent(Graphics2D gr, Vector2D offset) {
		if (Visible){
			Font f=gr.getFont();
			gr.setFont(Font);
			Shape cl=gr.getClip();
			gr.clipRect((int)(Position.X+offset.X), (int)(Position.Y+offset.Y), (int)Size.X, (int)Size.Y);
			gr.setColor(BgColor);
			gr.fillRect((int)(Position.X+offset.X), (int)(Position.Y+offset.Y), (int)Size.X, (int)Size.Y);
			gr.setColor(Color.GRAY);
			gr.fillRect((int)(Position.X+offset.X+Size.X*0.9), (int)(Position.Y+offset.Y), (int)(Size.X*0.1), (int)(Size.X*0.1));
			gr.fillRect((int)(Position.X+offset.X+Size.X*0.9), (int)(Position.Y+offset.Y+Size.Y-Size.X*0.1), (int)(Size.X*0.1), (int)(Size.X*0.1));
			gr.setColor(FontColor);
			for(int i=0;i<textLines.size();++i){
				gr.drawString(textLines.get(i), (int)(Position.X+offset.X),(int)(Position.Y+offset.Y)+i*16+Font.getSize()-scroll);
			}
			gr.setClip(cl);
			gr.setFont(f);
		}
	}

	@Override
	public boolean onMouseClick(Vector2D mousePos) {
		if (Visible&&mousePos.isMoreThan(Position)&&mousePos.isLessThan(Position.add(Size))){
			if (mousePos.isMoreThan(Position.add(new Vector2D(Size.X*0.9,0)))){
				if (mousePos.isLessThan(Position.add(new Vector2D(Size.X,Size.X*0.1)))){
					if ((textLines.size()-2)*16+Font.getSize()-scroll>0) scroll+=16;
				}
				if (mousePos.isMoreThan(Position.add(new Vector2D(Size.X*0.9,Size.Y-Size.X*0.1)))){
					if (Font.getSize()-scroll+16<0)	scroll-=16;
				}
			}
			return true;
		}
		return false;
	}
	
}
