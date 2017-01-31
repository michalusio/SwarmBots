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

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import botControl.Bot;
import botControl.BotFactory;
import main.Main;
import main.Vector2D;

public class MainDrawPanel extends JPanel {
	private static final long serialVersionUID = -3951099059163673209L;

	public final BotFactory Bots;
	public Bot selectedBot=null;
	private final Camera Camera;
	private Image BotImage;
	private AffineTransform a;
	private GraphicPanel botPanel;
	private GraphicList botList;
	private GraphicLabel botLabelWork;
	private GraphicLabel botLabelID;
	private GraphicLabel botLabelPos;
	private GraphicLabel botLabelAngle;
	
	public MainDrawPanel(BotFactory bots){
		super();
		Camera=new Camera(new Vector2D(0,0),new Vector2D(getWidth(),getHeight()),1);
		Bots=bots;
	    URL url = Main.class.getClassLoader().getResource("main/bot.png");
	    System.out.println("Loading bot image: "+url.getPath());
	    try {
			BotImage = ImageIO.read(url);
		} catch (IOException e) {
			BotImage=null;
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
	}
	
    @Override
	public void paintComponent(Graphics g) {
        super.paintComponent(g); 
        Graphics2D gr = (Graphics2D) g;
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
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
        	//Macierz transformacji bot-�wiat to:
        	//1. Obr�t o k�t bota
        	//2. Przesuni�cie ujemne o kamer�
        	//3. Przesuni�cie o pozycj� bota (po��czone z 2.)
        	//4. Skalowanie o zoom kamery
        	//5. Przesuni�cie o p� ekranu (na wy�rodkowanie)
        	a=AffineTransform.getTranslateInstance(Camera.getHalfSize().X,Camera.getHalfSize().Y);
        	a.concatenate(AffineTransform.getScaleInstance(Camera.getZoom(), Camera.getZoom()));
        	a.concatenate(AffineTransform.getTranslateInstance(b.getPosition().X-Camera.getPosition().X, b.getPosition().Y-Camera.getPosition().Y));
        	a.concatenate(AffineTransform.getRotateInstance(b.getAngle()));
        	if (b==selectedBot){
        		Point2D p1=a.transform(new Point2D.Double(),null);
        		botPanel.Position=new Vector2D(p1.getX(),p1.getY()-botPanel.MaxSize.Y);
        		int w1=(int)(32*Camera.getZoom());
        		int w2=w1<<1;
        		gr.drawRect((int)p1.getX() -w1, (int)p1.getY() -w1, w2, w2);
        		updateBotDescription(gr,false);
        	}
        	//6. Przesuni�cie ujemne o 32 pixele (wy�rodkowanie obrazka bota)
        	a.concatenate(AffineTransform.getTranslateInstance(-32,-32));
        	gr.drawImage(BotImage, a, null);
        }
        gr.setColor(Color.BLACK);
        gr.drawString("Free memory: " + String.format("%1$.2f",Runtime.getRuntime().freeMemory()/1048576.0d) +"Mb", getWidth()-140,12);
        botPanel.drawComponent(gr);
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
		if (!botPanel.onMouseClick(mPos)){
			AffineTransform invert=AffineTransform.getTranslateInstance(Camera.getPosition().X, Camera.getPosition().Y);
			invert.concatenate(AffineTransform.getScaleInstance(1/Camera.getZoom(), 1/Camera.getZoom()));
			invert.concatenate(AffineTransform.getTranslateInstance(-Camera.getHalfSize().X,-Camera.getHalfSize().Y));
			Vector2D inverted=Vector2D.fromPoint2D(invert.transform(new Point2D.Double(mPos.X,mPos.Y),null));
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
				if (selectedBot!=prevSelected) botPanel.Size=new Vector2D(0,0);
			}else{
				selectedBot=Bots.getNewBot();
				selectedBot.setPosition(inverted);
				selectedBot.Start();
				botPanel.Size=new Vector2D(0,0);
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
