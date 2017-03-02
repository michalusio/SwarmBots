package gui;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import botControl.Bot;
import botControl.BotFactory;
import botControl.SwarmCode;
import main.Vector2D;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.UIManager;

public final class MainWindow extends JFrame {
	private static final long serialVersionUID = -619417547337441461L;
	
	private final JPanel contentPane;
	private final MainDrawPanel canvas;
	private final JPanel Factorypanel;
	
	private Thread Refresher;
	private Vector2D diff=new Vector2D();
	private JTextArea textArea;
	
	public MainWindow(BotFactory bots) {
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				canvas.MoveWheel(-arg0.getWheelRotation());
			}
		});
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 768);
		contentPane = new JPanel();
		contentPane.setFocusable(true);
		contentPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_DOWN||arg0.getKeyCode() == KeyEvent.VK_S) {
					diff.Y=Math.min(1,diff.Y+1);
				}
				if (arg0.getKeyCode() == KeyEvent.VK_UP||arg0.getKeyCode() == KeyEvent.VK_W) {
					diff.Y=Math.max(-1,diff.Y-1);
				}
				if (arg0.getKeyCode() == KeyEvent.VK_RIGHT||arg0.getKeyCode() == KeyEvent.VK_D) {
					diff.X=Math.min(1,diff.X+1);
				}
				if (arg0.getKeyCode() == KeyEvent.VK_LEFT||arg0.getKeyCode() == KeyEvent.VK_A) {
					diff.X=Math.max(-1,diff.X-1);
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_DOWN||arg0.getKeyCode() == KeyEvent.VK_S) {
					diff.Y=Math.max(0,diff.Y-1);
				}
				if (arg0.getKeyCode() == KeyEvent.VK_UP||arg0.getKeyCode() == KeyEvent.VK_W) {
					diff.Y=Math.min(0,diff.Y+1);
				}
				if (arg0.getKeyCode() == KeyEvent.VK_RIGHT||arg0.getKeyCode() == KeyEvent.VK_D) {
					diff.X=Math.max(0,diff.X-1);
				}
				if (arg0.getKeyCode() == KeyEvent.VK_LEFT||arg0.getKeyCode() == KeyEvent.VK_A) {
					diff.X=Math.min(0,diff.X+1);
				}
			}
		});
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		canvas = new MainDrawPanel(bots);
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				contentPane.grabFocus();
				try {
					canvas.clickMouse(new Vector2D(arg0.getX(),arg0.getY()),arg0.getButton()==MouseEvent.BUTTON1);
				} catch (NoninvertibleTransformException e) {
					e.printStackTrace();
				}
			}
		});
		
		Factorypanel = new JPanel();
		Factorypanel.setBackground(UIManager.getColor("Panel.background"));
		Factorypanel.setBorder(new LineBorder(Color.BLACK, 2, false));
		Factorypanel.setBounds(0, 0, 291, 433);
		contentPane.add(Factorypanel);
		Factorypanel.setLayout(null);
		
		textArea = new JTextArea();
		Action bracket = new AbstractAction()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = -3887308316342199824L;

			@Override
		    public void actionPerformed(ActionEvent e)
		    {
		        String s=textArea.getText();
		        int prevCaretPos=textArea.getCaretPosition();
		        textArea.setText(s.substring(0, textArea.getCaretPosition())+"{}"+s.substring(textArea.getCaretPosition()));
		        textArea.setCaretPosition(prevCaretPos+1);
		    }
		};
		textArea.getInputMap().put(KeyStroke.getKeyStroke('{'), "Brackets");
		textArea.getActionMap().put("Brackets",bracket);
		textArea.setTabSize(1);
		JScrollPane sp = new JScrollPane(textArea);
		sp.setBounds(3,3,285,400);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		Factorypanel.add(sp);
		
		JButton btnUpdateCode = new JButton("Update Code");
		btnUpdateCode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String[] newCode=textArea.getText().split("\n");
				for(Bot b:canvas.Bots.getAllBots()){
					b.Interrupt();
				}
				canvas.Bots.Code=null;
				canvas.Bots.Code=new SwarmCode(newCode,canvas.Bots);
				for(Bot b:canvas.Bots.getAllBots()){
					b.Restart();
					b.Start();
				}
				contentPane.grabFocus();
			}
		});
		btnUpdateCode.setBounds(10, 406, 271, 20);
		Factorypanel.add(btnUpdateCode);
		
		textArea.setVisible(false);
		textArea.setText("");
		textArea.setVisible(true);
		canvas.setFocusable(false);
		canvas.setBounds(new Rectangle(0, 0, 1024, 768));
		canvas.setLayout(null);
		contentPane.add(canvas);
    	for(String s:canvas.Bots.Code.Commands){
    		textArea.append(s);
    		textArea.append("\n");
    	}
    	
		Refresher=new Thread(() ->
        {
             while(true) {
            	 Refresh();
            	 try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
             }
        });
		Refresher.start();
		canvas.grabFocus();
	}
	
	private void Refresh(){
		canvas.MoveMouse(diff);
		this.repaint();
	}
}
