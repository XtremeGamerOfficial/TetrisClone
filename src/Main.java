import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;


public class Main extends JFrame implements Runnable{
	public final int WIDTH = 20 * 10;
	public final int HEIGHT = 20 * 16;
	public final int SCALE = 2;
	public final Dimension GAME_DIM = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
	private final String TITLE = "JTetris";
	private boolean running = false;
	
	Image dbImage;
	Graphics dbg;
	
	public Main(){
		this.setTitle(TITLE);
		this.setSize(GAME_DIM);
		this.setVisible(true);
		this.setResizable(false);
		this.setBackground(new Color(075, 075, 075));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void tick(){
		
	}
	
	public void draw(Graphics g){
	}
	
	public void start(){
		new Thread(this).start();
		running = true;
	}
	
	private void stop(){
		running = false;
	}
	
	public void paint(Graphics g){
		dbImage = createImage(getWidth(), getHeight());
		dbg = dbImage.getGraphics();
		draw(dbg);
		g.drawImage(dbImage, 0, 0, null);
	}
	
	@Override
	public void run() {
		while(running){
			tick();
			try{
				Thread.sleep(5);
				repaint();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		Main m = new Main();
		m.start();
	}
}
