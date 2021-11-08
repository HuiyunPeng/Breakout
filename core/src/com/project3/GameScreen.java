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
import com.project3.GameLogic.Ball;
import com.project3.GameLogic.Box;


public class GameScreen extends ScreenAdapter {

	private static final int SCREEN_WIDTH = Gdx.graphics.getWidth();
	private static final int SCREEN_HEIGHT = Gdx.graphics.getHeight();

	private Coord mouse = new Coord(0,0);
	private SpriteBatch batch;
	private Sprite img, bar, star;
	private BitmapFont font;
	private Sound collisionSound, failingSound, winSound;
	private float timeSinceCollision = 0;
	private ArrayList<Effect> effects = new ArrayList<Effect>();
	private TextureAtlas textureAtlas;
	private Texture background;

	private boolean soundOn = true;
	private Project3 game;
	private MenuScreen menuScreen;

	private GameLogic gameLogic;

	class MyInput extends InputAdapter
	{
		public boolean touchDown(int screenX, int screenY, int pointer, int button)
		{
			int height = SCREEN_HEIGHT;
			int width = SCREEN_WIDTH;
			mouse = new Coord(screenX, height-screenY);

			gameLogic.initNewBall(bar.getHeight(), bar.getWidth(), mouse.x);
			return false;
		}

		public boolean mouseMoved (int x, int y)
		{
			int height = SCREEN_HEIGHT;
			mouse = new Coord(x, height-y);
			return false;
		}
	}

	class Effect
	{
		Sprite img;
		Coord pos;
		float scale;
		float angle;
		int duration;
		int ticks;

		float p ()
		{
			return ticks/(float)duration;
		}

		boolean draw ()
		{
			ticks++;
			if (p() > 1) return false;

			img.setRotation(angle);
			img.setColor(1,1,1,1-p());
			img.setScale((1+p()*3)*scale);
			pos.position(img);
			img.draw(batch);

			return true;
		}
	}

	public GameScreen(Project3 game, boolean soundOn){
		this.soundOn = soundOn;
		this.game = game;
	}

	private void initColor(){
		textureAtlas = new TextureAtlas("sprites.txt");
		Sprite red = textureAtlas.createSprite("brick-colors-0");
		Sprite orange = textureAtlas.createSprite("brick-colors-1");
		Sprite yellow = textureAtlas.createSprite("brick-colors-2");
		Sprite green = textureAtlas.createSprite("brick-colors-3");
		Sprite blue = textureAtlas.createSprite("brick-colors-4");
		Sprite pink = textureAtlas.createSprite("brick-colors-5");
		Sprite gray = textureAtlas.createSprite("brick-colors-6");
		Sprite black = textureAtlas.createSprite("brick-colors-7");

		HashMap<Integer, Sprite> tintColor = new HashMap<>();
		tintColor.put(0, red);
		tintColor.put(1, orange);
		tintColor.put(2, yellow);
		tintColor.put(3, green);
		tintColor.put(4, blue);
		tintColor.put(5, pink);
		tintColor.put(6, gray);
		tintColor.put(7, black);

		for(Box box : gameLogic.boxes){
			int colorNum = ThreadLocalRandom.current().nextInt(0, 8);
			box.setSprite(tintColor.get(colorNum));
		}
	}

	private void checkGameStatus(float delta){
		if (gameLogic.isLose()){
			font.draw(batch, "Game Over", SCREEN_WIDTH/2-80, SCREEN_HEIGHT/2+50);
			font.draw(batch, "You got " + Integer.toString(gameLogic.point) + " (out of "+ Integer.toString(gameLogic.totalPoint) +") points", SCREEN_WIDTH/2-190, SCREEN_HEIGHT/2+20);
			timeSinceCollision += delta;
			if(timeSinceCollision > 6.0f) {
			    menuScreen = new MenuScreen(game);
				game.setScreen(menuScreen);
			}
		} else if(gameLogic.isWin()) {
			if(soundOn) winSound.play();
			font.draw(batch, "Win!!!", SCREEN_WIDTH/2 - 40, SCREEN_HEIGHT/2+50);
			timeSinceCollision += delta;
			if(timeSinceCollision > 6.0f) {
			    menuScreen = new MenuScreen(game);
				game.setScreen(menuScreen);
			} 
		}
	}

	@Override
    public void show() {
    	Gdx.input.setInputProcessor(new MyInput());
		font = new BitmapFont();

		batch = new SpriteBatch();
		img = new Sprite(new Texture(Gdx.files.internal("ball.png")));
		bar = new Sprite(new Texture(Gdx.files.internal("paddle.png")));
		star = new Sprite(new Texture(Gdx.files.internal("star.png"))); 
		background = new Texture(Gdx.files.internal("background.jpeg"));
		collisionSound = Gdx.audio.newSound(Gdx.files.internal("sound.mp3"));
		failingSound = Gdx.audio.newSound(Gdx.files.internal("failing.wav"));
		winSound = Gdx.audio.newSound(Gdx.files.internal("win.wav"));

		gameLogic = new GameLogic(SCREEN_WIDTH, SCREEN_HEIGHT);

		gameLogic.initBricks(32, 64, 35, SCREEN_HEIGHT-335);
		//init sprite for bricks
		initColor();
    }

	@Override
	public void render(float delta) {
		ScreenUtils.clear(1, 1, 1, 1);
		batch.begin();
		font.setColor(255,255,255,1);
		font.getData().setScale(2, 2);

		batch.draw(background,0,0);


		ArrayList<Object> remove = new ArrayList<Object>();

		bar.setPosition(mouse.x, 0);
		bar.draw(batch);


		for (Box box : gameLogic.boxes){
			Sprite curSprite = box.getSprite();
			box.coord.position(curSprite);
			curSprite.draw(batch);

			//collision
			for(Ball ball : gameLogic.balls){
				if(gameLogic.collision(ball, box)){
					if(soundOn) collisionSound.play();
					remove.add(box);
				}
			}
		}
		gameLogic.boxes.removeAll(remove);

		font.draw(batch, "Score: " + Integer.toString(gameLogic.point), 0, SCREEN_HEIGHT-20);
		font.draw(batch, "Lives: " + Integer.toString(3-gameLogic.removedBall.size()), SCREEN_WIDTH-100, SCREEN_HEIGHT-20);

		for (Effect e : effects)
		{
			if (!e.draw())
			{
				remove.add(e);
			}
		}

		effects.removeAll(remove);
		remove.clear();

		for (Ball ball : gameLogic.balls)
		{
			img.setScale(0.5f);
			ball.pos.position(img);
			ball.vel.rotation(img);
			img.draw(batch);

			Effect e = new Effect();
			e.img = star;
			e.pos = ball.pos;
			e.scale = 0.25f;
			e.angle = ball.vel.theta_deg();
			e.duration = 30;
			effects.add(e);

			ball.tick(bar.getHeight(), bar.getWidth(), mouse.x);
		}
		
		checkGameStatus(delta);

		batch.end();
	}

	@Override
    public void dispose() {
    	batch.dispose();
    	textureAtlas.dispose();
    }
}