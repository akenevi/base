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
		for(int i=0; i<balls.length; i++){
			balls[i] = new Ball(render.random.nextInt(DW),render.random.nextInt(DH),newFloat(),newFloat(),5f,5f,newFloat(),newFloat(),newFloat());
		}
		gameLoop();
	}
	
	
	public static Ball[] balls = new Ball[10];
//	public static Ball ball = new Ball(DW/2,DH/2,1f,0f,10f,10f);
	
	public static void changeBalls(Ball newBall){
		Ball[] temp = new Ball[balls.length+1];
		for(int i = 0; i<balls.length; i++){
			if(balls[i].deleted)
				temp[i] = new Ball(DW/2,DH/2,newFloat(),newFloat(),balls[i].getWidth()+1f,balls[i].getHeight()+1f,newFloat(),newFloat(),newFloat());
			else
				temp[i]=balls[i];
		}
		temp[balls.length] = newBall;
		balls = temp;
//		System.out.println("length of ball array : "+balls.length);
	}
	
	public static void checkPosition(Ball ball){
		if(ball.getX() < 0 || ball.getX() > DW || ball.getY() < 0 || ball.getY() > DH){
			changeBalls(new Ball(DW/2,DH/2,newFloat(),newFloat(),ball.getWidth()+1f,ball.getHeight()+1f,newFloat(),newFloat(),newFloat()));
			ball.deleted = true;
		}
	}
	
	private static float newFloat(){
		float increment = 1;
		float temp = render.random.nextFloat()*increment;
		if(render.random.nextBoolean())
			return temp;
		else
			return temp*-1;
	}
	
	private static void logic(){
		for(int i = 0; i<balls.length; i++){
			checkPosition(balls[i]);
			balls[i].changeVel(newFloat(), newFloat());
			balls[i].move();
		}
//		ball.changeVel(newFloat(), newFloat());
//		ball.move();
	}
	
	private static void render(){
		render.preRender();
		for(int i = 0; i<balls.length; i++){
			balls[i].red = newFloat(); balls[i].green = newFloat(); balls[i].blue = newFloat();
			render.createQuad(balls[i].getX(), balls[i].getY(), balls[i].getWidth(), balls[i].getHeight(), balls[i].red, balls[i].green, balls[i].blue, 1);
		}
//		render.createQuad(ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight(), newFloat(), newFloat(), newFloat(), 1);
		render.drawString("balls : " + balls.length, DW/2, 10);
		render.afterRender();
	}
}