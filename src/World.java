import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;


public class World {

	private Rectangle[] tiles = new Rectangle[500];
	Random rand = new Random();
	
	public void draw(Graphics g){
        for(int i = 0; i < tiles.length; ++i){
        	g.setColor(new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
            g.fillRect(tiles[i].x, tiles[i].y, tiles[i].width, tiles[i].height);
        }
	}
	
}
