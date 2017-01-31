package main;

import java.awt.EventQueue;

import botControl.BotFactory;
import botControl.SwarmCode;
import collisions.CollisionPlane;
import gui.MainWindow;

public final class Main {
	public static SwarmCode sc;
	public static BotFactory bf;
	public final static void main(String[] args) {
		bf=new BotFactory("Fabryka 1");
		sc=new SwarmCode(
		new String[]{
				"/*",
				"To jest komentarz, nie jest czytany przez boty",
				"Aby dodaæ nowego bota, kliknij PPM na planszy",
				"Po planszy poruszasz siê klawiszami WSAD",
				"Mo¿esz przybli¿aæ i oddalaæ kó³kiem myszy",
				"Jeœli LPM wybierzesz robota,",
				"to informacje o nim dostaniesz na panelu \"Bot\"",
				"*/"
		},bf);
		SwarmCode.ColMap=new CollisionPlane();
		bf.Code=sc;
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MainWindow frame = new MainWindow(bf);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
