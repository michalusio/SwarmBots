package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import botControl.Bot;
import botControl.BotFactory;
import botControl.BotType;
import botControl.SwarmCode;
import collisions.CollisionShape;
import main.Main;
import main.Vector2D;
import sim.Obstacle;
import sim.ObstacleBox;

public class MainDrawPanel extends JPanel {
	private static final long serialVersionUID = -3951099059163673209L;

	public final BotFactory Bots;
	public Bot selectedBot;
	private final Camera Camera;
	private Image BotImage,StationImage;
	public static Image BoxImage;
	private AffineTransform a;
	private Vector2D inverted;
	
	private GraphicPanel botPanel;
	private GraphicList botList;
	private GraphicLabel botLabelWork;
	private GraphicLabel botLabelID;
	private GraphicLabel botLabelPos;
	private GraphicLabel botLabelAngle;
	
	private GraphicPanel createPanel;
	
	private List<Obstacle> Obstacles;
	
	public MainDrawPanel(BotFactory bots){
		super();
		Obstacles=new ArrayList<Obstacle>();
		Camera=new Camera(new Vector2D(0,0),new Vector2D(getWidth(),getHeight()),1);
		Bots=bots;
		selectedBot=null;
	    URL url = Main.class.getClassLoader().getResource("main/bot.png");
	    System.out.println("Loading bot image: "+url.getPath());
	    try {
			BotImage = ImageIO.read(url);
		} catch (IOException e) {
			BotImage=null;
			e.printStackTrace();
		}
	    url = Main.class.getClassLoader().getResource("main/station.png");
	    System.out.println("Loading station image: "+url.getPath());
	    try {
			StationImage = ImageIO.read(url);
		} catch (IOException e) {
			StationImage=null;
			e.printStackTrace();
		}
	    
	    url = Main.class.getClassLoader().getResource("main/box.png");
	    System.out.println("Loading box image: "+url.getPath());
	    try {
			BoxImage = ImageIO.read(url);
		} catch (IOException e) {
			BoxImage=null;
			e.printStackTrace();
		}
	    
	    botPanel=new GraphicPanel(new Vector2D(300,300));
	    botPanel.Visible=false;
	    botPanel.Raised=true;
	    botList=new GraphicList();
	    botList.Position=new Vector2D(10,90);
	    botList.Size=new Vector2D(280,200);
	    botPanel.addComponent(botList);
	    botPanel.addComponent(new GraphicButton(new Vector2D(20,50),new Vector2D(50,24),"Restart",()->{
	    	selectedBot.Interrupt();
	    	selectedBot.Restart();
	    	selectedBot.Continue();
	    	updateBotDescription((Graphics2D) getGraphics(),true);
	    }));
	    botPanel.addComponent(new GraphicButton(new Vector2D(85,50),new Vector2D(50,24),"Continue",()->{
	    	selectedBot.Continue();
	    	updateBotDescription((Graphics2D) getGraphics(),true);
	    }));
	    botPanel.addComponent(new GraphicButton(new Vector2D(150,50),new Vector2D(50,24),"Pause",()->{
	    	selectedBot.Interrupt();
	    	updateBotDescription((Graphics2D) getGraphics(),true);
	    }));
	    
	    botPanel.addComponent(new GraphicLabel(new Vector2D(10,25),new Vector2D(32,24),"Status:",Color.BLACK));
	    botLabelWork=new GraphicLabel(new Vector2D(45,25),new Vector2D(48,24),"Working",Color.GREEN.darker());
	    botPanel.addComponent(botLabelWork);
	    
	    botPanel.addComponent(new GraphicLabel(new Vector2D(10,10),new Vector2D(32,24),"ID:",Color.BLACK));
	    botLabelID=new GraphicLabel(new Vector2D(25,10),new Vector2D(48,24),"0",Color.BLACK);
	    botPanel.addComponent(botLabelID);
	    
	    botPanel.addComponent(new GraphicLabel(new Vector2D(120,10),new Vector2D(32,24),"Pos:",Color.BLACK));
	    botLabelPos=new GraphicLabel(new Vector2D(145,10),new Vector2D(48,24),"<0,0>",Color.BLACK);
	    botPanel.addComponent(botLabelPos);
	    
	    botPanel.addComponent(new GraphicLabel(new Vector2D(120,25),new Vector2D(32,24),"Angle:",Color.BLACK));
	    botLabelAngle=new GraphicLabel(new Vector2D(155,25),new Vector2D(48,24),"0",Color.BLACK);
	    botPanel.addComponent(botLabelAngle);
	    
	    
	    
	    createPanel=new GraphicPanel(new Vector2D(200,200));
	    createPanel.Visible=false;
	    createPanel.Raised=true;
	    
	    createPanel.addComponent(new GraphicButton(new Vector2D(20,20),new Vector2D(64,48),"Normal Bot",()->{
	    	selectedBot=Bots.getNewBot(BotType.Basic);
			selectedBot.setPosition(inverted);
			selectedBot.Start();
			createPanel.Size=new Vector2D();
			createPanel.Visible=false;
	    }));
	    createPanel.addComponent(new GraphicButton(new Vector2D(94,20),new Vector2D(64,48),"Bot Station",()->{
	    	selectedBot=Bots.getNewBot(BotType.Station);
			selectedBot.setPosition(inverted);
			selectedBot.Start();
			createPanel.Size=new Vector2D();
			createPanel.Visible=false;
	    }));
	    createPanel.addComponent(new GraphicButton(new Vector2D(20,78),new Vector2D(64,48),"Box",()->{
	    	selectedBot=null;
			Obstacle ob=new ObstacleBox(inverted,new Vector2D(64,64));
			Obstacles.add(ob);
			SwarmCode.ColMap.addCollisionShape(ob.getShape());
			createPanel.Size=new Vector2D();
			createPanel.Visible=false;
	    }));
	}
	
    @Override
	public void paintComponent(Graphics g) {
        super.paintComponent(g); 
        Graphics2D gr = (Graphics2D) g;
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        gr.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gr.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_NORMALIZE);
        gr.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        gr.setStroke(new BasicStroke(2));
        
        gr.setColor(Color.LIGHT_GRAY);
        gr.fillRect(0, 0, getWidth(), getHeight());
        gr.setColor(Color.BLUE);
        if (Bots!=null)
        for(Bot b:Bots.getAllBots()){
        	//Macierz transformacji bot-œwiat to:
        	//1. Obrót o k¹t bota
        	//2. Przesuniêcie ujemne o kamerê
        	//3. Przesuniêcie o pozycjê bota (po³¹czone z 2.)
        	//4. Skalowanie o zoom kamery
        	//5. Przesuniêcie o pó³ ekranu (na wyœrodkowanie)
        	a=AffineTransform.getTranslateInstance(Camera.getSize().X,Camera.getSize().Y);
        	a.concatenate(AffineTransform.getScaleInstance(Camera.getZoom(), Camera.getZoom()));
        	a.concatenate(AffineTransform.getTranslateInstance(b.getPosition().X-Camera.getPosition().X, b.getPosition().Y-Camera.getPosition().Y));
        	a.concatenate(AffineTransform.getRotateInstance(-b.getAngle()));
        	if (b==selectedBot){
        		Point2D p1=a.transform(new Point2D.Double(),null);
        		botPanel.Position=new Vector2D(p1.getX(),p1.getY()-botPanel.MaxSize.Y);
        		int w1=(int)(32*Camera.getZoom());
        		int w2=w1<<1;
        		gr.drawRect((int)p1.getX() -w1, (int)p1.getY() -w1, w2, w2);
        		updateBotDescription(gr,false);
        	}
        	//6. Przesuniêcie ujemne o 32 pixele (wyœrodkowanie obrazka bota) oraz half-pixel fix
        	a.concatenate(AffineTransform.getTranslateInstance(-32.5,-32.5));
        	Image Img;
        	switch(b.Type){
			case Basic:
				Img=BotImage;
				break;
			case Station:
				Img=StationImage;
				break;
			default:
				continue;
        	}
        	gr.drawImage(Img, a, null);
        }
        for(Obstacle ob: Obstacles){
        	CollisionShape cs=ob.getShape();
        	a=AffineTransform.getTranslateInstance(Camera.getSize().X,Camera.getSize().Y);
        	a.concatenate(AffineTransform.getScaleInstance(Camera.getZoom(), Camera.getZoom()));
        	a.concatenate(AffineTransform.getTranslateInstance(cs.BoundingBox.Location.X-Camera.getPosition().X, cs.BoundingBox.Location.Y-Camera.getPosition().Y));
        	a.concatenate(AffineTransform.getTranslateInstance(-0.5,-0.5));
        	gr.drawImage(ob.getImage(), a, null);
        }
        gr.setColor(Color.BLACK);
        gr.drawString("Free memory: " + String.format("%1$.2f",Runtime.getRuntime().freeMemory()/1048576.0d) +"Mb", getWidth()-140,12);
        botPanel.drawComponent(gr);
        createPanel.drawComponent(gr);
    }

    @Override
    public void setBounds(Rectangle r){
    	this.setBounds(r.x,r.y,r.width,r.height);
    	Camera.setSize(new Vector2D(r.width>>1,r.height>>1));
    }
    
	public void MoveMouse(Vector2D diff) {
		Camera.setPosition(Camera.getPosition().add(diff.mul(12d/Camera.getZoom())));
	}

	public void MoveWheel(int wheelRotation) {
		Camera.setZoom(Math.min(5,Math.max(1,Camera.getZoom()+wheelRotation*0.5d)));
	}

	public void clickMouse(Vector2D mPos,boolean leftButton) throws NoninvertibleTransformException {
		if ((!botPanel.onMouseClick(mPos))&&(!createPanel.onMouseClick(mPos))){
			AffineTransform invert=AffineTransform.getTranslateInstance(Camera.getPosition().X, Camera.getPosition().Y);
			invert.concatenate(AffineTransform.getScaleInstance(1/Camera.getZoom(), 1/Camera.getZoom()));
			invert.concatenate(AffineTransform.getTranslateInstance(-Camera.getSize().X,-Camera.getSize().Y));
			inverted=Vector2D.fromPoint2D(invert.transform(new Point2D.Double(mPos.X,mPos.Y),null));
			createPanel.Size=new Vector2D();
			createPanel.Visible=false;
			if (leftButton){
				Bot prevSelected=selectedBot;
				selectedBot=null;
				double distSqr=1024;
				for (Bot b:Bots.getAllBots()){
					double d=b.getPosition().distanceSqr(inverted);
					if (d<=distSqr){
						selectedBot=b;
						distSqr=d;
					}
				}
				if (selectedBot!=prevSelected) botPanel.Size=new Vector2D();
			}else{
				selectedBot=null;
				createPanel.Position=mPos;
				createPanel.Visible=true;
				botPanel.Size=new Vector2D();
			}
		}
		updateBotDescription((Graphics2D) getGraphics(),true);
	}

	public void updateBotDescription(Graphics2D gr, boolean forced) {
		if (selectedBot!=null){
			botPanel.Visible=true;
			if (selectedBot.updateTerminal()||forced){
				java.util.List<String> console=selectedBot.getTerminal();
				botList.removeAll();
				synchronized(selectedBot.getMemory()){
					for(String s:console) botList.add(s);
				}
			}
			botLabelID.text=String.valueOf(selectedBot.getId());
			botLabelPos.text=selectedBot.getPosition().toString("%1$.0f|%2$.0f");
			botLabelAngle.text=String.format("%1$.0f", selectedBot.getAngle()*180/Math.PI);
			botLabelWork.text=selectedBot.working()?"Working":"Stopped";
			botLabelWork.FontColor=selectedBot.working()?Color.GREEN.darker():Color.RED.darker();
		}else botPanel.Visible=false;
	}
	
	
}
