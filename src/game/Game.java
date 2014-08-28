package game;

import util.Renderer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class Game {
	//display opt
	public static final int DW = 800;
	public static final int DH = 600;
	private static boolean isResizable = false;
	private static boolean vSync = true;
	private static int fps = 60;
	
	private static boolean gameExit = false;
	
	public static Renderer render = new Renderer();
	
	public static void main(String args[]){
		Thread renderer = new Thread(render);
		renderer.setDaemon(true);
		renderer.setName("Graphics Thread");
		renderer.start();
		if(render.startUp(DW, DH, fps, isResizable, vSync))
			startGame();
	}
	
	public static void gameLoop(){
		while(!gameExit){
			logic();
			render();
			
			if(Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
				gameExit = true;
		//	System.out.println("update!");
		}
		render.CleanUp();
	}
	
	private static void startGame(){
		gameExit = false;
		gameLoop();
	}
	
	private static void logic(){
		
	}
	
	private static void render(){
		render.preRender();
		
		render.afterRender();
	}
}