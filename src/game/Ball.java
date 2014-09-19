package game;

import org.lwjgl.util.vector.Vector2f;

public class Ball {
	private float posX;
	private float posY;
	private Vector2f velocity;
	
	private float height;
	private float width;
	
	public float red, green, blue;
	public boolean deleted = false;
	
	public Ball(float x, float y, float velX, float velY, float height, float width, float r, float g, float b){
		posX = x;
		posY = y;
		velocity = new Vector2f(velX, velY);
		this.height = height;
		this.width = width;
		red = r; green = g; blue = b;
	}
	
	public void move(){
		posX += velocity.getX();
		posY += velocity.getY();
	}
	
	public void changeVel(float x, float y){
		velocity.set(x, y);
	}
	
	//getters
	public float getX(){
		return posX;
	}
	
	public float getY(){
		return posY;
	}
	
	public float getHeight(){
		return height;
	}
	
	public float getWidth(){
		return width;
	}
}
