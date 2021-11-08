package com.project3;
//Huiyun Peng
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Input;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class GameLogic {

	private int screenWidth;
	private int screenHeight;
	public ArrayList<Ball> balls;
	public ArrayList<Box> boxes;
	public HashSet<Ball> removedBall;
	public int totalPoint;
	public int point = 0;

	public GameLogic(int screenWidth, int screenHeight){
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		removedBall = new HashSet<>();
		balls = new ArrayList<>();
		boxes = new ArrayList<>();
	}

	public class Ball
	{
		Coord pos = new Coord(0,0);
		Coord vel = new Coord(4,4);

		void tick (float barHeight, float barWidth, float mouseX)
		{
			pos = pos.plus(vel);

			if (pos.y < 0){
				removedBall.add(this);
			} else if(pos.y <= barHeight && pos.y > 0 && (pos.x >= mouseX && pos.x <= mouseX + barWidth)) {
				vel = vel.times(new Coord(1, -1));
				pos.y = barHeight;
			} else if (pos.y > screenHeight) {
				vel = vel.times(new Coord(1, -1));
				pos.y = screenHeight;
			} else if (pos.x < 0){
				vel = vel.times(new Coord(-1, 1));
				pos.x = 0;
			} else if (pos.x > screenWidth){
				vel = vel.times(new Coord(-1, 1));
				pos.x = screenWidth;
			}
		}
	}

	public class Box{
		Coord coord;
		private Sprite sprite;
		Box(Coord coord){
			this.coord = coord;
		}

		public void setSprite(Sprite sprite){
			this.sprite = sprite;
		}

		public Sprite getSprite(){
			return this.sprite;
		}
	}

	public void initNewBall(float barHeight, float barWidth, float mouseX){
		if (balls.size() < 3)
		{
			Ball ball = new Ball();
			ball.pos = new Coord(mouseX+barWidth/2, barHeight);
			balls.add(ball);
		}
	}

	public void initBricks(float brickHeight, float brickWidth, float x, float y){
		while(y < screenHeight){
			if(x >= screenWidth){
				x = 35;
				y += brickHeight;
			}
			if(y > screenHeight){
				break;
			}
			int randomNum = ThreadLocalRandom.current().nextInt(0, 10);
			if(randomNum > 4){
				boxes.add(new Box(new Coord(x,y)));
			}
			x+=brickWidth;
		}
		totalPoint = boxes.size();
	}

	public boolean collision(Ball ball, Box box){
		if(ball.pos.x+15 > box.coord.x-17.5 && ball.pos.x+15 < box.coord.x+17.5 && ball.pos.y+15 > box.coord.y-35.5 && ball.pos.y+15 < box.coord.y + 35.5){
			point++;
			ball.vel.y *= -1;
			return true;
		}
		return false;
	}

	public boolean isLose(){
		return boxes.size() > 0 && removedBall.size() == 3;
	}

	public boolean isWin(){
		return boxes.size() == 0;
	}

}