package util;

import static org.lwjgl.opengl.ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB;
import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

// got to study a bit about FBO and improve the whole class

public class Renderer implements Runnable{
	private HashMap<String, Sprite> spriteMap;
	private final String fontLocation = "/res/pixfont.png"; //http://opengameart.org/content/16x12-terminal-bitmap-font
	private Sprite curSprite;
	private int tex;
	public Random random;
	
	private int fps;
	
	public void run(){
		random = new Random(System.nanoTime());
	}
	
	public boolean startUp(int DW, int DH, int fps, boolean isResizable, boolean vSync){
		this.fps = fps;
		InitGLandDisplay(DW, DH, isResizable, vSync);
		initSprites(fontLocation);
		return true;
	}
	
	public void createQuad(float x, float y, float s){
		createQuad(x,y,s,s,1f,1f,1f,1f,0);
	}
	
	public void createQuad(float x, float y, float s, float r, float g, float b, float a){
		createQuad(x,y,s,s,r,g,b,a,0);
	}
	
	public void createQuad(float x, float y, float w, float h, float r, float g, float b, float a){
		createQuad(x,y,w,h,r,g,b,a,0);
	}
	
	public void createQuad(float x, float y, float w, float h, float r, float g, float b, float a, float rot){
		float hh = h/2;
		float hw = w/2;
		glPushMatrix();
		{
			glTranslatef(x, y, 0);
			glRotatef(rot,0,0,1);
			
			glBegin(GL_QUADS);
			{
				glColor4f(r,g,b,a);
				glVertex2f(-hw,-hh);
				glVertex2f(hw,-hh);
				glVertex2f(hw,hh);
				glVertex2f(-hw,hh);
			}
			glEnd();
		}
		glPopMatrix();
	}
	
	public void drawChar(char ch, float x, float y){
		if(ch != ' ')
			drawSprite(Character.toString(ch), x, y, 12, 16, 0, false);
	}
	
	public void drawSprite(String name, float x, float y, int w, int h){
		drawSprite(name, x, y, w, h, 0, false);
	}
	
	public void drawSpriter(String name, float x, float y, int w, int h, int rotate){
		drawSprite(name, x, y, w, h, rotate, false);
	}

	//draws given sprite centered at given coordinates
	public void drawSprite(String name, float x, float y, int w, int h, int rotate, boolean reverseMap) {
		curSprite = spriteMap.get(name);
		int u = curSprite.x;
		int v = curSprite.y2;
		int u2 = curSprite.x2;
		int v2 = curSprite.y;
		float hh = (float)h/2;
		float hw = (float)w/2;
		if(reverseMap){
			u=curSprite.x2;
			u2=curSprite.x;
		}
		glPushMatrix();
		
		glTranslatef(x,y,0);
		glRotatef(rotate,0,0,1);
		glColor3f(1f,1f,1f);
		
		glBindTexture(GL_TEXTURE_RECTANGLE_ARB, tex);
		{
			glBegin(GL_QUADS);
			{
				glTexCoord2f(u, v);		glVertex2f(-hw, -hh);
				glTexCoord2f(u2, v);	glVertex2f(hw, -hh);
				glTexCoord2f(u2, v2);	glVertex2f(hw, hh);
				glTexCoord2f(u, v2);	glVertex2f(-hw, hh);
			}
			glEnd();
		}
		glBindTexture(GL_TEXTURE_RECTANGLE_ARB, 0);
		glPopMatrix();
	}
	
	public void drawShakingString(String string, float x, float y, float magnitude){
		float deltax = random.nextFloat()*magnitude;
		float deltay = random.nextFloat()*magnitude;
		if(random.nextBoolean())
			deltax *= -1;
		if(random.nextBoolean())
			deltay *= -1;
		drawString(string, deltax+x, deltay+y);
	}
	
	public void drawString(String string, float x, float y){
		char[] charArray = string.toCharArray();
		int j=0;
		float tempY;
		for(char i : charArray){
			String name = Character.toString(i);
			if(i == 'j') tempY = y-1;
			else if(i == 'g') tempY = y-2;
			else if(i == 'p' || i == 'q') tempY = y-2;
			else tempY = y;
			if(name != " ")
			drawSprite(name, (j*12)+x, tempY, 12, 16);
			j++;    
		}
	}
	
	public void initSprites(String location){
		spriteMap = new HashMap<String, Sprite>();
		int texture = glGenTextures();
		tex = texture;
		glBindTexture(GL_TEXTURE_RECTANGLE_ARB, texture);
		InputStream in = null;
		try {
			in = getClass().getResourceAsStream(location); 
			PNGDecoder decoder = new PNGDecoder(in);
			ByteBuffer buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
			buffer.flip();
			in.close();
			glTexParameteri(GL_TEXTURE_RECTANGLE_ARB, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_RECTANGLE_ARB, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_RECTANGLE_ARB, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		} catch (NullPointerException e) {
			System.err.println("File couldn't be found at "+ location);
		} catch (IOException e) {
			System.err.println("File couldn't be loaded from "+ location);
		}
		glBindTexture(GL_TEXTURE_RECTANGLE_ARB, 0);
		System.out.println("Loading font");
		if(location == fontLocation){
			spriteMap.put(" ", new Sprite(" ", 0*12, 0*16, 12, 16));
			spriteMap.put("%", new Sprite("%", 5*12, 0*16, 12, 16));
			spriteMap.put(",", new Sprite(",", 12*12, 0*16, 12, 16));
			spriteMap.put(".", new Sprite(".", 14*12, 0*16, 12, 16));
			spriteMap.put("!", new Sprite("!", 1*12, 0*16, 12, 16));
			spriteMap.put("?", new Sprite("?", 15*12, 1*16, 12, 16));
			spriteMap.put(":", new Sprite(":", 10*12, 1*16, 12, 16));
			
			spriteMap.put("0", new Sprite("0", 0*12, 1*16, 12, 16));
			spriteMap.put("1", new Sprite("1", 1*12, 1*16, 12, 16));
			spriteMap.put("2", new Sprite("2", 2*12, 1*16, 12, 16));
			spriteMap.put("3", new Sprite("3", 3*12, 1*16, 12, 16));
			spriteMap.put("4", new Sprite("4", 4*12, 1*16, 12, 16));
			spriteMap.put("5", new Sprite("5", 5*12, 1*16, 12, 16));
			spriteMap.put("6", new Sprite("6", 6*12, 1*16, 12, 16));
			spriteMap.put("7", new Sprite("7", 7*12, 1*16, 12, 16));
			spriteMap.put("8", new Sprite("8", 8*12, 1*16, 12, 16));
			spriteMap.put("9", new Sprite("9", 9*12, 1*16, 12, 16));

			spriteMap.put("A", new Sprite("A", 1*12, 2*16, 12, 16));
			spriteMap.put("B", new Sprite("B", 2*12, 2*16, 12, 16));
			spriteMap.put("C", new Sprite("C", 3*12, 2*16, 12, 16));
			spriteMap.put("D", new Sprite("D", 4*12, 2*16, 12, 16));
			spriteMap.put("E", new Sprite("E", 5*12, 2*16, 12, 16));
			spriteMap.put("F", new Sprite("F", 6*12, 2*16, 12, 16));
			spriteMap.put("G", new Sprite("G", 7*12, 2*16, 12, 16));
			spriteMap.put("H", new Sprite("H", 8*12, 2*16, 12, 16));
			spriteMap.put("I", new Sprite("I", 9*12, 2*16, 12, 16));
			spriteMap.put("J", new Sprite("J", 10*12, 2*16, 12, 16));
			spriteMap.put("K", new Sprite("K", 11*12, 2*16, 12, 16));
			spriteMap.put("L", new Sprite("L", 12*12, 2*16, 12, 16));
			spriteMap.put("M", new Sprite("M", 13*12, 2*16, 12, 16));
			spriteMap.put("N", new Sprite("N", 14*12, 2*16, 12, 16));
			spriteMap.put("O", new Sprite("O", 15*12, 2*16, 12, 16));
			spriteMap.put("P", new Sprite("P", 0*12, 3*16, 12, 16));
			spriteMap.put("Q", new Sprite("Q", 1*12, 3*16, 12, 16));
			spriteMap.put("R", new Sprite("R", 2*12, 3*16, 12, 16));
			spriteMap.put("S", new Sprite("S", 3*12, 3*16, 12, 16));
			spriteMap.put("T", new Sprite("T", 4*12, 3*16, 12, 16));
			spriteMap.put("U", new Sprite("U", 5*12, 3*16, 12, 16));
			spriteMap.put("V", new Sprite("V", 6*12, 3*16, 12, 16));
			spriteMap.put("W", new Sprite("W", 7*12, 3*16, 12, 16));
			spriteMap.put("X", new Sprite("X", 8*12, 3*16, 12, 16));
			spriteMap.put("Y", new Sprite("Y", 9*12, 3*16, 12, 16));
			spriteMap.put("Z", new Sprite("Z", 10*12, 3*16, 12, 16));
			
			spriteMap.put("a", new Sprite("a", 1*12, 4*16, 12, 16));
			spriteMap.put("b", new Sprite("b", 2*12, 4*16, 12, 16));
			spriteMap.put("c", new Sprite("c", 3*12, 4*16, 12, 16));
			spriteMap.put("d", new Sprite("d", 4*12, 4*16, 12, 16));
			spriteMap.put("e", new Sprite("e", 5*12, 4*16, 12, 16));
			spriteMap.put("f", new Sprite("f", 6*12, 4*16, 12, 16));
			spriteMap.put("g", new Sprite("g", 7*12, 4*16, 12, 16));
			spriteMap.put("h", new Sprite("h", 8*12, 4*16, 12, 16));
			spriteMap.put("i", new Sprite("i", 9*12, 4*16, 12, 16));
			spriteMap.put("j", new Sprite("j", 10*12, 4*16, 12, 16));
			spriteMap.put("k", new Sprite("k", 11*12, 4*16, 12, 16));
			spriteMap.put("l", new Sprite("l", 12*12, 4*16, 12, 16));
			spriteMap.put("m", new Sprite("m", 13*12, 4*16, 12, 16));
			spriteMap.put("n", new Sprite("n", 14*12, 4*16, 12, 16));
			spriteMap.put("o", new Sprite("o", 15*12, 4*16, 12, 16));
			spriteMap.put("p", new Sprite("p", 0*12, 5*16, 12, 16));
			spriteMap.put("q", new Sprite("q", 1*12, 5*16, 12, 16));
			spriteMap.put("r", new Sprite("r", 2*12, 5*16, 12, 16));
			spriteMap.put("s", new Sprite("s", 3*12, 5*16, 12, 16));
			spriteMap.put("t", new Sprite("t", 4*12, 5*16, 12, 16));
			spriteMap.put("u", new Sprite("u", 5*12, 5*16, 12, 16));
			spriteMap.put("v", new Sprite("v", 6*12, 5*16, 12, 16));
			spriteMap.put("w", new Sprite("w", 7*12, 5*16, 12, 16));
			spriteMap.put("x", new Sprite("x", 8*12, 5*16, 12, 16));
			spriteMap.put("y", new Sprite("y", 9*12, 5*16, 12, 16));
			spriteMap.put("z", new Sprite("z", 10*12, 5*16, 12, 16));
		}
		// create sprite map usind method below, upper left corner of SpriteSheet == (0,0)
		// spriteMap.put("key name", new Sprite("sprite name", x, y, size x, size y));
		
		
		System.out.println("Font loading done, Hash map has " + spriteMap.size() +" items");
	}
	
	private static void InitGLandDisplay(int DW, int DH, boolean isResizable, boolean vSync){
		try{
			Display.setDisplayMode(new DisplayMode(DW,DH));
			Display.create();
			Display.setResizable(isResizable);
			Display.setVSyncEnabled(vSync);
			Keyboard.create();
		} catch (Exception e) {
			System.err.println("Failed to create window");
		}
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0,DW,0,DH,-1,1);
		
		glEnable(GL_TEXTURE_RECTANGLE_ARB);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glClearColor(0,0,0,1);
		glDisable(GL_DEPTH_TEST);
		
		glMatrixMode(GL_MODELVIEW);
	}
	
	public void CleanUp(){
		Display.sync(0);
		Display.destroy();
		Keyboard.destroy();
		Keyboard.enableRepeatEvents(true);
	}
	
	public void preRender(){
	//clearing old stuff
		glClear(GL_COLOR_BUFFER_BIT);
		glLoadIdentity();
	}
	
	public void afterRender(){
	//swap buffers, poll new input and finally autotune (Display.sync(fps)) Thread sleep to meet stated fps
		Display.update();
		Display.sync(fps);
	}
}

class Sprite {
    String name;
    public int x, y, x2, y2, w, h;

    public Sprite(String name, int x, int y, int w, int h) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        x2 = x + w;
        y2 = y + h;
    }
}