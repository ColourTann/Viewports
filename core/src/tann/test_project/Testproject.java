package tann.test_project;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Testproject extends ApplicationAdapter {
	ScreenViewport port;
	Stage stage;
	SpriteBatch batch;
	Texture img;
	BitmapFont f;
	ArrayList<ViewportString> vps = new ArrayList<ViewportString>();
	int viewportIndex=0;
	ViewportString current;
	Matrix4 defaultMatrix;
	@Override
	public void create () {
		port = new ScreenViewport();
		f = new BitmapFont();
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		batch = new SpriteBatch();
		defaultMatrix=batch.getProjectionMatrix();
		img = new Texture("kitten.png");
		
		
		vps.add(new ViewportString(new ScreenViewport(), "Screen Viewport: should just display everything normally\nThere is a 400x300 actor added at 0,0 which draws a cat\nThe arguments to the viewports are the world size in pixels, which can be different to the screen size\nleft/right to switch viewport type, should be able to resize the screen too!"));
		vps.add(new ViewportString(new StretchViewport(1000, 1000), "Stretch Viewport(1000,1000): stretches everything from the supplied size to the screen size"));
		vps.add(new ViewportString(new FitViewport(300, 300), "Fit Viewport(300,300): black bar mode"));
		vps.add(new ViewportString(new ExtendViewport(400, 300), "Extend Viewport(400,300): keeps the original aspect ratio and adds bars in one direction only"));
		vps.add(new ViewportString(new ScalingViewport(Scaling.fill, 400, 300), "Scaling Viewport(fill,400,300): fills the screen by keeping the same aspect ratio but cutting bits off"));
		vps.add(new ViewportString(new ScalingViewport(Scaling.none, 400, 300), "Scaling Viewport(none,400,300): there are other arguments and this one just centers it and doesn't scale at all"));
		
		
		stage.addListener(new InputListener(){
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				switch(keycode){
				case Keys.LEFT: viewportIndex--; break;
				case Keys.RIGHT: viewportIndex++; break;
				}
				viewportIndex=Math.max(0, Math.min(vps.size()-1, viewportIndex));
				applyNewViewport();
				return false;
			}
		});
		applyNewViewport();
		
		// here i've used actor as an anonymous class. usually I think it's better to make your own class that extends actor for clarity 
		Actor a =new Actor(){
			@Override
			public void draw(Batch batch, float parentAlpha) {
				batch.draw(img, 0, 0);
			}
		};
		a.setSize(400, 300);
		
		a.addListener(new InputListener(){
			// touchDown means click or tap
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("hi");
			return false;
			}
		});
		stage.addActor(a);
		

	}

	@Override
	public void resize(int width, int height) {
		// this is a bad workaround, there's probably a better way
		defaultMatrix= new SpriteBatch().getProjectionMatrix();
		super.resize(width, height);
	}
	
	void applyNewViewport(){
		current=vps.get(viewportIndex);
		stage.setViewport(current.v);
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),true);
	}
	
	void undo(){
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	void redo(){
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),true);
	}
	
	@Override
	public void render () {
		// clear the screen
		Gdx.gl.glClearColor(.02f, .07f, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// draw the stage
		stage.draw();
		
		// stuff to undo all the stage stuff so I can render normally
		undo();
		batch.begin();
		batch.setProjectionMatrix(defaultMatrix);
		f.draw(batch, current.s, 3, Gdx.graphics.getHeight()-8);
		batch.end();
		redo();
	
	}
}
