package com.tfc.platformer;

import com.tfc.physics.wrapper.Physics;
import com.tfc.physics.wrapper.common.API.Vector2;
import com.tfc.physics.wrapper.common.API.WrapperWorld;
import com.tfc.physics.wrapper.common.API.listeners.CollisionListener;
import com.tfc.physics.wrapper.common.backend.interfaces.ICollider;
import com.tfc.platformer.blocks.BasicBlock;
import com.tfc.platformer.display.GraphicsUI;
import com.tfc.platformer.display.GraphicsWorld;
import com.tfc.platformer.logic.GroundCollisionListener;
import com.tfc.platformer.logic.InputController;
import com.tfc.platformer.logic.colliders.TexturedBoxEntityCollider;
import com.tfc.platformer.logic.gui.ImageButtonComponent;
import com.tfc.platformer.logic.gui.TextButtonComponent;
import com.tfc.platformer.logic.helpers.Vector2D;
import com.tfc.platformer.npcs.NPC;
import com.tfc.platformer.utils.CSVReader;
import com.tfc.platformer.utils.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class Main {
	private static final BufferedImage playerAsset = ResourceManager.getImage("assets/textures/player_temp.png");
	
	public static TexturedBoxEntityCollider playerCollider = new TexturedBoxEntityCollider(4, 8, () -> playerAsset, () -> false);
	public static int jumpTime = 0;
	public static int maxJumpTime = 30;
	
	public static boolean inGame = false;
	public static boolean inEditor = false;
	
	//TODO: remove
	public static final NPC oldPlayerNpc = new NPC(0, 0, 6, 8, 8, 4);
	
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
		
		loadRoom("pathfinding_test");
//		genRoom(10,10);
		window.setIconImage(ResourceManager.getImage("assets/textures/icon/16x.png"));
		
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
				gui.guiComponents.add(new TextButtonComponent("Open Editor", 0.5f - (0.25f / 2), 0.5f, 0.25f, 0.1f, new Color(128, 128, 128), () -> {
					inEditor = true;
					gui.queueClear();
				}));
			}
			if (inEditor) {
				gui.guiComponents.add(new TextButtonComponent("Leave Editor", 0.5f - (0.25f / 2), 0, 0.25f, 0.1f, new Color(128, 128, 128), () -> {
					inEditor = false;
					inGame = false;
					gui.queueClear();
				}));
				if (!inGame) {
					gui.guiComponents.add(new TextButtonComponent("Play Test", 0, 0, 0.25f, 0.1f, new Color(128, 128, 128), () -> {
						playerCollider.getPositionSetter().setX(0);
						playerCollider.getPositionSetter().setY(0);
						inGame = true;
						gui.queueClear();
					}));
					gui.guiComponents.add(new TextButtonComponent("Export To File", 0, 0.1f, 0.25f, 0.1f, new Color(128, 128, 128), () -> {
						try {
							File f = new File("rooms/exported/room.csv");
							if (!f.exists()) {
								f.getParentFile().mkdirs();
								f.createNewFile();
							}
							FileOutputStream stream = new FileOutputStream(f);
							StringBuilder builder = new StringBuilder();
							world.getColliders().forEach((collider) -> {
								if (collider instanceof BasicBlock) {
									builder.append(((BasicBlock) collider).getString()).append("\n");
								}
							});
							stream.write(builder.toString().getBytes());
							stream.close();
						} catch (Throwable ignored) {
						}
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
	
	public static void clearWorld() {
		ArrayList<ICollider> collidersToRemove = new ArrayList<>();
		world.getColliders().forEach((collider) -> {
			if (collider instanceof BasicBlock) collidersToRemove.add(collider);
		});
		collidersToRemove.forEach(world::removeCollider);
	}
	
	public static void genRoom(int i, int i1, int xPos, int yPos, long firstSeed, long secondSeed) {
		for (int x = 0; x < i + 1; x++) {
			world.addCollider(new BasicBlock(4, 4, () -> false).move(
					x * 8 + (xPos * 8), (yPos * 8)).setImmovable());
			world.addCollider(new BasicBlock(4, 4, () -> false).move(
					x * 8 + (xPos * 8), i1 * -8 + (yPos * 8)).setImmovable());
		}
		
		for (int x = 1; x < i1; x++) {
			world.addCollider(new BasicBlock(4, 4, () -> false).move(0, x * -8 + (yPos * 8)).setImmovable());
			world.addCollider(new BasicBlock(4, 4, () -> false).move(i * 8, x * -8 + (yPos * 8)).setImmovable());
		}
		
		Random entrances = new Random(firstSeed);
		Random other = new Random(secondSeed);
		
		boolean hasRoofEntrance = false;
		
		int num = entrances.nextInt(3) + 1;
		
		ArrayList<Vector2D> starts = new ArrayList<>();
		
		for (int entrance = 0; entrance <= num; entrance++) {
			boolean isWall = entrances.nextBoolean();
			boolean isLower = entrances.nextBoolean();
			
			if (!isWall) hasRoofEntrance = true;
			
			if (entrance == num && !hasRoofEntrance) isWall = false;
			
			int block = entrances.nextInt(isWall ? i : i1);
			
			if (!isWall) {
				removeBlock(block + xPos, isLower ? 2 : -i1 + yPos);
				starts.add(new Vector2D(block, isLower ? 2 : -i1 + 2));
			} else {
				removeBlock(isLower ? 0 : i + xPos, -block - 1 + yPos);
				if (block >= i1 - 2) {
					removeBlock(isLower ? 0 : i + xPos, -block + yPos);
					starts.add(new Vector2D(isLower ? 0 : i, -block));
				} else {
					removeBlock(isLower ? 0 : i + xPos, -block + yPos);
					starts.add(new Vector2D(isLower ? 0 : i, -block + 3));
				}
			}
		}
		
		Vector2D lastPos = null;
		
		for (int numb = 0; numb++ < starts.size() - 1; ) {
			int target = entrances.nextInt(starts.size() - 1);
//				if (target == numb) {
//					target = 2;
//				}
			for (int lerpering = 0; lerpering++ <= 20; ) {
				Vector2D vector2D = starts.get(target).lerp(lerpering / 21f, starts.get(numb));
				
				if (lastPos == null) lastPos = vector2D;
				
				if (entrances.nextInt(10) >= 9 || vector2D.dist(lastPos) >= 4) {
					removeBlock((int) vector2D.x + xPos, (int) vector2D.y + yPos);
					
					lastPos = vector2D;
					
					world.addCollider(
							new BasicBlock(4, 4, () -> false)
									.move((int) vector2D.x * 8 + xPos * 8, (int) vector2D.y * 8 + (yPos - 2) * 8)
									.setImmovable()
					);
				}
			}
		}
		
		num = other.nextInt(3) + 1;
		
		for (int entrance = 0; entrance <= num; entrance++) {
			boolean isWall = other.nextBoolean();
			boolean isLower = other.nextBoolean();
			
			if (!isWall) hasRoofEntrance = true;
			
			if (entrance == num && !hasRoofEntrance) isWall = false;
			
			int block = other.nextInt(isWall ? i : i1);
			
			if (!isWall) {
				removeBlock(block + xPos, isLower ? 2 : -i1 + yPos);
				starts.add(new Vector2D(block, isLower ? 2 : -i1 + 2));
			} else {
				removeBlock(isLower ? 0 : i + xPos, -block - 1 + yPos);
				if (block >= i1 - 2) {
					removeBlock(isLower ? 0 : i + xPos, -block + yPos);
					starts.add(new Vector2D(isLower ? 0 : i, -block));
				} else {
					removeBlock(isLower ? 0 : i + xPos, -block + yPos);
					starts.add(new Vector2D(isLower ? 0 : i, -block + 3));
				}
			}
		}
	}
	
	public static void removeBlock(int x, int y) {
		ArrayList<ICollider> toRemove = new ArrayList<>();
		world.getColliders().forEach(collider -> {
			if (collider instanceof BasicBlock) {
//				if (y==6)
//				System.out.println(((BasicBlock) collider).getY()/8);
				if ((int) (((BasicBlock) collider).getX() / 8) == x) {
					if ((int) (((BasicBlock) collider).getY() / 8) == y) {
						toRemove.add(collider);
					}
				}
			}
		});
		toRemove.forEach(world::removeCollider);
	}
	
	public static BasicBlock getBlock(int x, int y) {
		ArrayList<BasicBlock> toRemove = new ArrayList<>();
		world.getColliders().forEach(collider -> {
			if (collider instanceof BasicBlock) {
				if ((int) (((BasicBlock) collider).getX() / 8) == x) {
					if ((int) (((BasicBlock) collider).getY() / 8) == y) {
						toRemove.add((BasicBlock) collider);
					}
				}
			}
		});
		if (!toRemove.isEmpty()) return toRemove.get(0);
		return null;
	}
	
	public static void loadRoom(String name) {
		CSVReader reader = new CSVReader(Main.class.getClassLoader().getResourceAsStream("rooms/" + name + ".csv"));
		for (int i = 0; i < reader.countRows(); i++) {
			world.addCollider(BasicBlock.fromString(reader.getRow(i)));
		}
	}
	
	public static void tick() {
		try {
			if (!inGame) oldPlayerNpc.pathFindingNodes.clear();
			if (inGame) {
//				System.out.println(playerCollider.getX());
//				System.out.println(playerCollider.getY());
				oldPlayerNpc.tick();
				oldPlayerNpc.goalX = 96;
//				oldPlayerNpc.goalX = 136;
				oldPlayerNpc.goalY = -8;

//				clearWorld();
//				Random rand = new Random(392313);
//				int firstSeed = rand.nextInt();
//				int secondSeed = rand.nextInt();
//				genRoom(6,6,0,-2,firstSeed, secondSeed);
//				genRoom(6,6,6,-2,secondSeed,firstSeed);
				
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
				
				playerCollider.setAngle(
						Math.toRadians(Math.min(
								Math.max(
										-45,
										Math.toDegrees(playerCollider.getAngle())
								),
								45
						))
				);

//				if (playerCollider.isOnGround()) {
//					playerCollider.setAngle(Math.toRadians(MathHelper.lerp(0.9f, Math.toDegrees(playerCollider.getAngle()), 0)));
//				}
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
		for (int yPos = y; yPos <= 10; yPos++) {
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
