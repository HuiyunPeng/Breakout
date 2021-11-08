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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class MenuScreen extends ScreenAdapter {
	private static final int BUTTON_WIDTH = 150;
	private static final int BUTTON_HEIGHT = 50;
	private static final int SCREEN_WIDTH = Gdx.graphics.getWidth(); 
	private static final int SCREEN_HEIGHT = Gdx.graphics.getHeight(); 
	private static final int TABLE_X = SCREEN_WIDTH / 2;
	private static final int TABLE_Y = SCREEN_HEIGHT / 2 - BUTTON_HEIGHT * 2;

    private Stage stage;
    private Project3 game;
    private GameScreen gameScreen;
    private boolean soundOn = true;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture background;

	public MenuScreen(Project3 game){
		this.game = game;
	}

	@Override
    public void show() {
    	batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);	

        background = new Texture(Gdx.files.internal("background.jpeg"));

        Table table = new Table();
        table.setPosition(TABLE_X, TABLE_Y);
        stage.addActor(table);

       	Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

       	font = new BitmapFont();
       
        //create buttons
        TextButton newGame = new TextButton("New Game", skin, "small");
        newGame.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y){
				gameScreen = new GameScreen(game, soundOn);
				game.setScreen(gameScreen);			
		    }
		});

		final TextButton sound = new TextButton("Sound On", skin, "small");
        sound.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y){
				soundOn = !soundOn;
				if(sound.getText().toString().equals("Sound On")){
					sound.setText("Sound off");
				} else {
					sound.setText("Sound On");
				}		
		    }
		});

		TextButton exit = new TextButton("Exit", skin, "small");	
		exit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();				
			}
		});

		//add buttons to table
        table.add(newGame).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
		table.row().pad(15, 0, 15, 0);
		table.add(sound).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
		table.row();
		table.add(exit).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
    }

	@Override
	public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    batch.begin();
	    batch.draw(background,0,0);
	    font.setColor(255, 255, 255, 1);
		font.getData().setScale(4, 4);
	    font.draw(batch, "Breakout", 200, SCREEN_HEIGHT - 100);
	    stage.act(delta);
	    stage.draw();
	    batch.end();
	}

	@Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }
}
