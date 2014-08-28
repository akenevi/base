package util;

import static org.lwjgl.opengl.ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB;
import static org.lwjgl.opengl.GL11.*;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

// got to study a bit about FBO and improve the whole class

public class Renderer implements Runnable{
	public Random random;
	private int fps;
	
	public void run(){
		random = new Random(System.nanoTime());
	}
	
	public boolean startUp(int DW, int DH, int fps, boolean isResizable, boolean vSync){
		this.fps = fps;
		InitGLandDisplay(DW, DH, isResizable, vSync);
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
//		glPushMatrix();
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
//		glPopMatrix();
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
		glPushMatrix();
	}
	
	public void afterRender(){
	//swap buffers, poll new input and finally autotune (Display.sync(fps)) Thread sleep to meet stated fps
		glPopMatrix();
		Display.update();
		Display.sync(fps);
	}
}