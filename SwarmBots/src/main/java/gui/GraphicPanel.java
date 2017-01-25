package gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import main.Vector2D;

public class GraphicPanel extends GraphicSizeableComponent{
	
	private final List<GraphicComponent> Components;
	
	public Vector2D MaxSize;
	public Color BgColor;
	public boolean Raised;
	
	public GraphicPanel(){
		this(new Vector2D(100,100));
	}
	
	public GraphicPanel(Vector2D size) {
		super(size);
		MaxSize=Size;
		Components=new ArrayList<GraphicComponent>();
		BgColor=Color.GRAY.brighter();
	}

	public void addComponent(GraphicComponent gc){
		Components.add(gc);
	}
	
	@Override
	public void drawComponent(Graphics2D gr, Vector2D offset) {
		if (Visible){
			if (Size.distanceSqr(MaxSize)>0.1){
				Vector2D dir=MaxSize.sub(Size);
				Size=Size.add(new Vector2D(Math.min(Math.abs(dir.X),40)*Math.signum(dir.X),Math.min(Math.abs(dir.Y),40)*Math.signum(dir.Y)));
			}
			Shape cl=gr.getClip();
			gr.clipRect((int)(Position.X+offset.X-2+(MaxSize.X-Size.X)*0.5), (int)(Position.Y+offset.Y-2+(MaxSize.Y-Size.Y)*0.5), (int)(Size.X+4-(MaxSize.X-Size.X)*0.5), (int)(Size.Y+4-(MaxSize.Y-Size.Y)*0.5));
			gr.setColor(BgColor.darker());
			if (Raised) gr.fillRect((int)(Position.X+offset.X-2+(MaxSize.X-Size.X)*0.5), (int)(Position.Y+offset.Y-2+(MaxSize.Y-Size.Y)*0.5), (int)(Size.X+4-(MaxSize.X-Size.X)*0.5), (int)(Size.Y+4-(MaxSize.Y-Size.Y)*0.5));
			gr.setColor(BgColor);
			gr.fillRect((int)(Position.X+offset.X+(MaxSize.X-Size.X)*0.5), (int)(Position.Y+offset.Y+(MaxSize.Y-Size.Y)*0.5), (int)(Size.X-(MaxSize.X-Size.X)*0.5), (int)(Size.Y-(MaxSize.Y-Size.Y)*0.5));
			for(GraphicComponent gc : Components){
				gc.drawComponent(gr, offset.add(Position));
			}
			gr.setClip(cl);
		}
	}

	@Override
	public boolean onMouseClick(Vector2D mousePos) {
		if (Visible&&mousePos.isMoreThan(Position)&&mousePos.isLessThan(Position.add(Size))){
			for(GraphicComponent gc : Components){
				if (gc.onMouseClick(mousePos.sub(Position))) return true;
			}
			return true;
		}
		return false;
	}
	
	
}
