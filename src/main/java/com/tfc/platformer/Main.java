package com.tfc.platformer;

import com.tfc.physics.wrapper.Physics;
import com.tfc.physics.wrapper.common.Vector2;
import com.tfc.physics.wrapper.common.WrapperWorld;
import com.tfc.physics.wrapper.common.colliders.BoxCollider;
import com.tfc.platformer.display.Graphics;
import com.tfc.platformer.logic.InputController;
import com.tfc.platformer.logic.TexturedBoxCollider;
import com.tfc.platformer.logic.helpers.Box2D;
import com.tfc.platformer.logic.helpers.Line2D;
import com.tfc.platformer.utils.ResourceManager;

import javax.swing.*;

public class Main {
	public static BoxCollider playerCollider = new TexturedBoxCollider(16, 32, () -> ResourceManager.getImage("assets/textures/player_temp.png"), () -> false);
	
	public static WrapperWorld world;
	public static InputController inputController = new InputController();
	public static JFrame window = new JFrame();
	
	private static final BoxCollider testFloor = (BoxCollider)new BoxCollider(100,1).move(0,100).setImmovable();
	
	public static void main(String[] args) {
		Physics.init();
		
		world = new WrapperWorld(new Vector2(0, 0.1f));
		world.addCollider(playerCollider);
		world.addCollider(testFloor);
		
		window.add(new Graphics(world, playerCollider, window));
		window.addKeyListener(inputController);
		
		window.setTitle("2D Platformer");
		window.setSize(1000,1000);
		window.setVisible(true);
		
		while (window.isVisible()) tick();
		
		Runtime.getRuntime().exit(0);
	}
	
	public static void tick() {
		try {
			window.repaint();
			world.tick();
			
			double xStrength = 0.1;
			if (inputController.isKeyPressed(68)) playerCollider.addVelocity(xStrength, 0);
			if (inputController.isKeyPressed(65)) playerCollider.addVelocity(-xStrength, 0);
			
			Box2D playerBox = new Box2D(new Line2D(
					playerCollider.getPositionSupplier().get().x-playerCollider.width,
					(playerCollider.getPositionSupplier().get().y-playerCollider.height)+2,
					playerCollider.getPositionSupplier().get().x+playerCollider.width,
					(playerCollider.getPositionSupplier().get().y+playerCollider.height)+2
			));
			Box2D otherBox = new Box2D(new Line2D(
					testFloor.getPositionSupplier().get().x-testFloor.width,
					testFloor.getPositionSupplier().get().y-testFloor.height,
					testFloor.getPositionSupplier().get().x+testFloor.width,
					testFloor.getPositionSupplier().get().y+testFloor.height
			));
			
			boolean onGround = false;
			if (playerBox.collides(otherBox)) {
				if (playerBox.getCenter().y <= otherBox.getCenter().y) {
					onGround = true;
				}
			}
			
			if (inputController.isKeyPressed(87) && (onGround)) {
				playerCollider.addVelocity(0,-100);
				System.out.println("hi");
			}
			
			Thread.sleep(10);
		} catch (Throwable ignored) {
		}
	}
}
