package com.tfc.platformer;

import com.tfc.physics.wrapper.Physics;
import com.tfc.physics.wrapper.common.API.Vector2;
import com.tfc.physics.wrapper.common.API.WrapperWorld;
import com.tfc.physics.wrapper.common.API.listeners.CollisionListener;
import com.tfc.platformer.blocks.BasicBlock;
import com.tfc.platformer.display.GraphicsUI;
import com.tfc.platformer.display.GraphicsWorld;
import com.tfc.platformer.logic.GroundCollisionListener;
import com.tfc.platformer.logic.InputController;
import com.tfc.platformer.logic.colliders.TexturedBoxEntityCollider;
import com.tfc.platformer.logic.gui.ImageButtonComponent;
import com.tfc.platformer.logic.gui.TextButtonComponent;
import com.tfc.platformer.utils.CSVReader;
import com.tfc.platformer.utils.ResourceManager;
import com.tfc.platformer.utils.math.MathHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Main {
	private static final BufferedImage playerAsset = ResourceManager.getImage("assets/textures/player_temp.png");
	
	public static TexturedBoxEntityCollider playerCollider = new TexturedBoxEntityCollider(4, 8, () -> playerAsset, () -> false);
	public static int jumpTime = 0;
	public static int maxJumpTime = 30;
	
	public static boolean inGame = false;
	public static boolean inEditor = false;
	
	public static WrapperWorld world;
	public static InputController inputController = new InputController();
	public static JFrame window = new JFrame();
	
	public static final CollisionListener collisionListener = new GroundCollisionListener();
	
	public static void main(String[] args) {
		Physics.init();
		
		world = new WrapperWorld(new Vector2(0, 0.05f));
		world.addCollider(playerCollider);
		
		world.allowSleep(false);

//		int y = 20 / 8;
//		y += new Random().nextInt(3) - 1;
//		int i;
//		for (i = 0; i <= 10; i++) {
//			addColumn(i, y);
//			y += new Random().nextInt(3) - 1;
//		}
		
		loadRoom("test_room");
		
		GraphicsWorld world_graphics = new GraphicsWorld(world, playerCollider, window);
		window.add(world_graphics);
		GraphicsUI gui = new GraphicsUI(playerCollider, window, world_graphics);
		gui.addClearListener(() -> {
			if (inGame) {
				gui.guiComponents.add(new ImageButtonComponent("assets/textures/game/quit_game.png", 0.95f, 0, 0.05f, 0.1f, () -> {
					inGame = false;
					gui.queueClear();
				}));
			} else if (!inEditor) {
				gui.guiComponents.add(new TextButtonComponent("Play Game", 0.5f - (0.25f / 2), 0.4f, 0.25f, 0.1f, new Color(128, 128, 128), () -> {
					inGame = true;
					gui.queueClear();
				}));
				gui.guiComponents.add(new TextButtonComponent("Open Editor",0.5f-(0.25f/2),0.5f,0.25f,0.1f,new Color(128,128,128),()->{
					inEditor = true;
					gui.queueClear();
				}));
			}
			if (inEditor) {
				gui.guiComponents.add(new TextButtonComponent("Leave Editor",0.5f-(0.25f/2),0,0.25f,0.1f,new Color(128,128,128),()->{
					inEditor = false;
					inGame = false;
					gui.queueClear();
				}));
				if (!inGame) {
					gui.guiComponents.add(new TextButtonComponent("Play Test",0,0,0.25f,0.1f,new Color(128,128,128),()->{
						playerCollider.getPositionSetter().setX(0);
						playerCollider.getPositionSetter().setY(0);
						inGame = true;
						gui.queueClear();
					}));
				}
			}
		});
		gui.runClear();
		gui.setOpaque(false);
		world_graphics.addChild(gui);
		window.addKeyListener(inputController);
		window.addKeyListener(gui);
		window.addMouseListener(inputController);
		window.addMouseMotionListener(inputController);
		
		window.setTitle("2D Platformer");
		window.setSize(1000, 1000);
		window.setVisible(true);
		
		world.addCollisionListener(collisionListener);
		
		while (window.isVisible()) tick();
		
		Runtime.getRuntime().exit(0);
	}
	
	public static void loadRoom(String name) {
		CSVReader reader = new CSVReader(Main.class.getClassLoader().getResourceAsStream("rooms/" + name + ".csv"));
		for (int i = 0; i < reader.countRows(); i++) {
			world.addCollider(BasicBlock.fromString(reader.getRow(i)));
		}
	}
	
	public static void tick() {
		try {
			if (inGame) {
				world.tick();
				
				playerCollider.setAngle(0);
				
				double xStrength = playerCollider.isOnGround() ? 0.035 : (0.1 / Math.max(1, ((Math.min(jumpTime, 18) + 14) / 14f)));
				if (inputController.isKeyPressed(68)) playerCollider.addVelocity(xStrength, 0);
				if (inputController.isKeyPressed(65)) playerCollider.addVelocity(-xStrength, 0);
				if (inputController.isKeyPressed(87) && (playerCollider.isOnGround() || jumpTime < maxJumpTime)) {
					playerCollider.addVelocity(0, -0.2f);
					jumpTime++;
				}
				
				if (playerCollider.isOnGround()) jumpTime = 0;
				else if (!inputController.isKeyPressed(87)) jumpTime = 9999;
				maxJumpTime = 14;
				
				if (playerCollider.isOnGround())
					playerCollider.setAngle(MathHelper.lerp(0.1f, playerCollider.getAngle(), 0));
			} else if (inEditor) {
				if (inputController.isKeyPressed(68))
					playerCollider.getPositionSetter().setX(playerCollider.getX() + 0.5);
				if (inputController.isKeyPressed(65))
					playerCollider.getPositionSetter().setX(playerCollider.getX() - 0.5);
				if (inputController.isKeyPressed(87))
					playerCollider.getPositionSetter().setY(playerCollider.getY() - 0.5);
				if (inputController.isKeyPressed(83))
					playerCollider.getPositionSetter().setY(playerCollider.getY() + 0.5);
			}
			
			window.repaint();
			
			Thread.sleep(10);
		} catch (Throwable ignored) {
		}
	}
	
	private static void addColumn(int x, int y) {
//		TexturedBoxCollider collider = (TexturedBoxCollider)new TexturedBoxCollider(4,4,()->Graphics.mintyFields.getAsset("grass_side"),()->false).move(x*8,y*8).setImmovable();
		for (int yPos = y; yPos<=10;yPos++) {
			BasicBlock collider;
			collider = (BasicBlock) new BasicBlock(4, 4, () -> false).move(x * 8, yPos * 8).setImmovable();
//			if (yPos == y) {
//				collider = (TexturedBoxCollider)new TexturedBoxCollider(4,4,()-> GraphicsWorld.mintyFields.getAsset("grass_side"),()->false).move(x*8,yPos*8).setImmovable();
//			} else {
//				collider = (TexturedBoxCollider)new TexturedBoxCollider(4,4,()-> GraphicsWorld.mintyFields.getAsset("dirt"),()->false).move(x*8,yPos*8).setImmovable();
//			}
			world.addCollider(collider);
		}
	}
}
