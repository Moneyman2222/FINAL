package Main;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.swing.JPanel;



public class GamePane1 extends JPanel implements Runnable{
	//The window's size
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	//This is where the game loop is
	final int FPS = 60;
	Thread gameThread;
	PlayManager pm;
	public static sound music = new sound();
	public static sound se = new sound();
	
	public GamePane1() {
		//These are the panel's settings
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setBackground(Color.black);
		this.setLayout(null);
		// this implements the keyboard
		this.addKeyListener(new KeyManager());
		this.setFocusable(true);
		
		pm = new PlayManager();
	}
	public void LaunchGame() {
		gameThread = new Thread(this);
		gameThread.start();
		
		music.play(0, true);
		music.loop();
	}

	@Override
	public void run() {
		System.out.println("run");
		//This is the game loop itself
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		while(gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;
			
			if(delta >= 1) {
				update();
				repaint();
				delta--;
			}
		
		}
	}
private void update() {
	if(KeyManager.pausePressed == false && pm.gameOver == false) {
		pm.update();
	}
}
public void paintComponent (Graphics g) {
	super.paintComponent(g);
	Graphics2D g2 = (Graphics2D)g;
	pm.draw(g2);
	
	
}
}
