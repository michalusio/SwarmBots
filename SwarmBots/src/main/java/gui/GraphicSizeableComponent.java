package gui;

import main.Vector2D;

public abstract class GraphicSizeableComponent extends GraphicComponent {

	public Vector2D Size;
	
	public GraphicSizeableComponent(){
		super();
		Size=new Vector2D(100,100);
	}
	
	public GraphicSizeableComponent(Vector2D size){
		super();
		Size=size;
	}

}
