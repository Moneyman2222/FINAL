package Main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;

import mino.Block;
import mino.Mino_Cube;
import mino.Mino_L1;
import mino.Mino_L2;
import mino.Mino_Line;
import mino.Mino_T;
import mino.Mino_Z1;
import mino.Mino_Z2;
import mino.mino;

public class PlayManager {
	// This is the main play area
	final int WIDTH = 360;
	final int HEIGHT = 600;
	public static int left_x;
	public static int right_x;
	public static int top_y;
	public static int bottom_y;
	
	// The blocks
	mino currentmino;
	final int MINO_START_X;
	final int MINO_START_Y;
	mino nextMino;
	final int NEXTMINO_X;
	final int NEXTMINO_Y;
	public static ArrayList<Block> staticBlocks = new ArrayList<>();
	
	//Others
	public static int dropInterval = 60; // blocks drop every 60 frames
	
	boolean gameOver;
	
	//effects
	boolean effectCounterOn;
	int effectCounter;
	ArrayList<Integer> effectY = new ArrayList<>();
	
	// Level and Score
	int level = 1;
	int lines;
	int score;
	
	
	public PlayManager() {
		//This is where main area frame is
		left_x = (GamePane1.WIDTH/2) - (WIDTH/2); // 1280/2 - 360/2 = 460
		right_x = left_x + WIDTH;
		top_y = 50;
		bottom_y = top_y + HEIGHT;
		
		MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
		MINO_START_Y = top_y + Block.SIZE;
		
		NEXTMINO_X = right_x + 175;
		NEXTMINO_Y = top_y + 500;
		
		//this sets the starting block
		currentmino = pickMino();
		currentmino.setXY(MINO_START_X, MINO_START_Y);
		nextMino = pickMino();
		nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
	}
	private mino pickMino() {
		// This picks a block to use
		mino mino = null;
		int i = new Random().nextInt(7);
		
		switch(i) {
		case 0: mino = new Mino_L1();break;
		case 1: mino = new Mino_L2();break;
		case 2: mino = new Mino_Cube();break;
		case 3: mino = new Mino_Line();break;
		case 4: mino = new Mino_T();break;
		case 5: mino = new Mino_Z1();break;
		case 6: mino = new Mino_Z2();break;
		}
		return mino;
	}
	public void update() {
		//This checks to see if the current Block is active
		if(currentmino.active == false) {
			//if the current block isn't active, this puts it into the static blocks
			staticBlocks.add(currentmino.b[0]);
			staticBlocks.add(currentmino.b[1]);
			staticBlocks.add(currentmino.b[2]);
			staticBlocks.add(currentmino.b[3]);
			
			//this checks to see if the game is over
			if(currentmino.b[0].x == MINO_START_X && currentmino.b[0].y == MINO_START_Y) {
			//this means the current block immediately hit a different block and couldnt move
				//so it's xy's are the same with the next block 
			gameOver = true;
			GamePane1.music.stop();
			GamePane1.se.play(2, false);
			}
			
			
			currentmino.deactivating = false;
			
			// This replaces the current block with the next block
			currentmino = nextMino;
			currentmino.setXY(MINO_START_X, MINO_START_Y);
			nextMino = pickMino();
			nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
		
		//when a block becomes inactive, this checks to see if a line is created
		checkDelete();	
		
		}
		else {
			currentmino.update();
		}
	}
	public void checkDelete() {
		int x = left_x;
		int y = top_y;
		int blockCount = 0;
		int lineCount = 0;
		
		while(x < right_x && y < bottom_y) {
			
			for(int i = 0; i < staticBlocks.size(); i++) {
				if(staticBlocks.get(i).x == x && staticBlocks.get(i).y == y) {
					//this increases the count if there is static blocks
					blockCount++;
				}
			}
			
			x += Block.SIZE;
			
			if(x == right_x) {
				
				// if the blockCount hits 12, that means a line is filled 
				// so we delete the line.
				if(blockCount == 12) {
					
					effectCounterOn = true;
					effectY.add(y);
					
					for(int i = staticBlocks.size()-1; i > -1; i--) {
						//This removes the current line
						if(staticBlocks.get(i).y == y) {
							staticBlocks.remove(i);
					}
				}
					
					lineCount++;
					lines++;
					// Drop Speed
					// if the Line score hits a certain number the level increases so does the drop speed
					// i is the fastest
					if(lines % 10 == 0 && dropInterval > 1) {
						level++;
						if(dropInterval > 10) {
							dropInterval -= 10;
						}
						else {
							dropInterval -= 1;
						}
					}
					
					
					
					//a line has been deleted so the blocks needs to slide down
					for(int i = 0; i < staticBlocks.size(); i++) {
					//when a line is deleted this will make the line above slide down
					if(staticBlocks.get(i).y < y) {
						staticBlocks.get(i).y += Block.SIZE;
					}
				}
			}
				blockCount = 0;
				x = left_x;
				y += Block.SIZE;
			}
		}
		
		// add score
		if(lineCount > 0) {
			GamePane1.se.play(1, false);
			int singleLineScore = 10 * level;
			score += singleLineScore * lineCount;
		}
	}
	public void draw(Graphics2D g2) {
		// this draws the play area frame
		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(4f));
		g2.drawRect(left_x-4,top_y-4,WIDTH+8,HEIGHT+8);
		
		// this draws the waiting room for the blocks
		int x = right_x + 100;
		int y = bottom_y - 200;
		g2.drawRect(x, y, 200, 200);
		g2.setFont(new Font("Arial", Font.PLAIN, 30));
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.drawString("NEXT", x+60, y+60);
		
		// this draws the score and level box
		g2.drawRect(x, top_y, 250, 300);
		x += 40;
		y = top_y + 90;
		g2.setColor(Color.orange);
		g2.setFont(new Font("Merriweather", Font.ITALIC,30));
		g2.drawString("LEVEL: " + level, x, y); y+= 70;
		g2.setColor(Color.cyan);
		g2.setFont(new Font("Merriweather", Font.ITALIC,30));
		g2.drawString("LINES: " + lines, x, y); y+= 70;
		g2.setColor(Color.white);
		g2.setFont(new Font("Merriweather", Font.ITALIC,30));
		g2.drawString("SCORE: " + score, x, y);
		// This draws the current Block
		if(currentmino != null) {
		currentmino.draw(g2);
		}
		// this draws the next block
		nextMino.draw(g2);
		
		//this draws the static block
		for (int i = 0; i < staticBlocks.size(); i++) {
			staticBlocks.get(i).draw(g2);
		}
		
		// this draws the effect
		if(effectCounterOn) {
			effectCounter++;
			
			g2.setColor(Color.white);
			for(int i = 0; i < effectY.size(); i ++) {
				g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
			}
			
			if(effectCounter == 10) {
				effectCounterOn = false;
				effectCounter = 0;
				effectY.clear();
			}
		}
		
		// pause screen or game over
		g2.setColor(Color.white);
		g2.setFont(g2.getFont().deriveFont(50f));
		if(gameOver) {
			x = left_x + 25;
			y = top_y + 320;
			g2.drawString("GAME OVER!", x, y);
		}
		else if(KeyManager.pausePressed) {
			x = left_x + 70;
			y = top_y + 320;
			g2.drawString("PAUSED", x, y);
		}
		// this Draws the game title
		x = 35;
		y = top_y + 320;
		g2.setColor(Color.red);
		g2.setFont(new Font("Merriweather", Font.ITALIC, 60));
		g2.drawString("T", x, y);
		x = 55;
		y = top_y + 320;
		g2.setColor(Color.orange);
		g2.setFont(new Font("Merriweather", Font.ITALIC, 60));
		g2.drawString("E", x+20, y);
		x = 55;
		y = top_y + 320;
		g2.setColor(Color.yellow);
		g2.setFont(new Font("Merriweather", Font.ITALIC, 60));
		g2.drawString("T", x+60, y);
		x = 55;
		y = top_y + 320;
		g2.setColor(Color.green);
		g2.setFont(new Font("Merriweather", Font.ITALIC, 60));
		g2.drawString("R", x+100, y);
		x = 55;
		y = top_y + 320;
		g2.setColor(Color.cyan);
		g2.setFont(new Font("Merriweather", Font.ITALIC, 60));
		g2.drawString("I", x+150, y);
		x = 55;
		y = top_y + 320;
		g2.setColor(Color.pink);
		g2.setFont(new Font("Merriweather", Font.ITALIC, 60));
		g2.drawString("S", x+170, y);
		x = 55;
		y = top_y + 320;
		g2.setColor(Color.white);
		g2.setFont(new Font("Merriweather", Font.ITALIC, 60));
		g2.drawString(":", x+210, y);
		y = top_y + 320;
		g2.setColor(Color.white);
		g2.setFont(new Font("Merriweather", Font.ITALIC, 60));
		g2.drawString("BLAST!", x, y+50);
	}
}