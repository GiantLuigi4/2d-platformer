package com.tfc.platformer.display;

import com.tfc.physics.wrapper.common.backend.interfaces.ICollider;
import com.tfc.platformer.Main;
import com.tfc.platformer.blocks.BasicBlock;
import com.tfc.platformer.logic.gui.GuiComponent;
import com.tfc.platformer.logic.gui.IKeyListenerComponent;
import com.tfc.platformer.utils.SpriteMap;
import com.tfc.platformer.utils.math.MathHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GraphicsUI extends JComponent implements KeyListener {
	private final GraphicsWorld worldGraphics;
	private final ICollider player;
	private final JFrame frame;
	
	public ArrayList<GuiComponent> guiComponents = new ArrayList<>();
	public ArrayList<GuiComponent> guiComponentsBlock = new ArrayList<>();
	public ArrayList<Runnable> clearListeners = new ArrayList<>();
	
	public static final SpriteMap sprites = new SpriteMap("assets/textures/level_editor");
	public static final SpriteMap old_player = new SpriteMap("assets/npcs/old_player_char");
	
	public int focusedComponent = -1;
	
	private boolean shouldClear = false;
	
	private float lPanelExtendProgress = 0;
	private boolean isLPanelExtending = false;
	public BasicBlock selectedBlock = null;
	private boolean hasBlockSelected = false;
	
	private final String[] old_player_title_anim = new String[]{
			"idle",
			"look_side",
			"directly_sideways",
			"look_back",
			"look_back",
			"look_back",
			"directly_sideways",
			"look_side",
	};
	
	private float old_player_title_anim_frame = 0;
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		try {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) guiComponents.get(focusedComponent).onClick();
			System.out.println(e.getKeyCode());
			if (e.getKeyCode() == KeyEvent.VK_BACK_QUOTE) {
				if (Main.inputController.isKeyPressed(KeyEvent.VK_SHIFT)) focusedComponent -= 1;
				else focusedComponent += 1;
				if (focusedComponent < 0) focusedComponent = guiComponents.size() - 1;
				else if (focusedComponent >= guiComponents.size()) focusedComponent = 0;
			}
			
			if (guiComponents.get(focusedComponent) instanceof IKeyListenerComponent)
				((IKeyListenerComponent) guiComponents.get(focusedComponent)).onKeyPressed(e.getKeyCode());
		} catch (Throwable ignored) {
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
	}
	
	public GraphicsUI(ICollider player, JFrame frame, GraphicsWorld worldGraphics) {
		this.player = player;
		this.frame = frame;
		this.worldGraphics = worldGraphics;
	}
	
	@Override
	public void paint(Graphics g) {
		Point mouse = Main.inputController.getMouse();
		float mx = -1;
		float my = -1;
		
		if (mouse != null) {
			mx = ((float) (mouse.x - 7) / frame.getWidth());
			my = ((float) (mouse.y - 27) / frame.getHeight());
		}
		
		final float finalMx = mx;
		final float finalMy = my;
		
		if (mouse != null) {
			mx *= frame.getWidth();
			my *= frame.getHeight();
		}
		
		AtomicBoolean didClick = new AtomicBoolean(false);
		guiComponents.forEach((component) -> {
			if (component.isInBounds(finalMx, finalMy) && Main.inputController.isLeftDown()) didClick.set(true);
		});
		
		g.setColor(new Color(0, 0, 0, 0));
		
		Graphics2D g2d = ((Graphics2D) g);
		
		//TODO: rewrite the entire editor
		if (Main.inEditor) {
			{
				AffineTransform transform = g2d.getTransform();
				
				g2d.translate(frame.getWidth() / 2f, frame.getHeight() / 2f);
				g2d.scale(5, 5);
				
				g2d.translate(worldGraphics.lastPosX, worldGraphics.lastPosY);
				
				g2d.translate(4, 4);
				g2d.translate(
						(Math.round((player.getPositionSupplier().get().x / 8) - 1)) * 8,
						(Math.round((player.getPositionSupplier().get().y / 8) - 1.75)) * 8
				);
				
				float offX = ((Math.round((player.getPositionSupplier().get().x / 8) - 1)) * 8) + worldGraphics.lastPosX;
				float offY = ((Math.round((player.getPositionSupplier().get().y / 8) - 1.75)) * 8) + worldGraphics.lastPosY;
				
				float mxFromCenter = -10000000;
				float myFromCenter = -10000000;
				if (mouse != null) {
					mxFromCenter = mx - frame.getWidth() / 2f;
					myFromCenter = my - frame.getHeight() / 2f;
				}
				
				mxFromCenter -= 10;
				myFromCenter -= 18;
				
				float screenX = worldGraphics.lastPosX;
				float screenY = worldGraphics.lastPosY;
				
				for (int x = -10; x <= 10; x++) {
					AffineTransform transform1 = g2d.getTransform();
					g2d.translate(x * 8, -80);
					g2d.scale(0.1f, 0.1f);
					for (int y = -10; y <= 10; y++) {
						if (x == 0 || y == 0) g2d.setColor(new Color(0, 255, 0));
						else g2d.setColor(new Color(128, 128, 128));
						
						int scl = 8 * 5;
						if (((mxFromCenter - offX)) / scl >= (x - 1) && ((mxFromCenter - offX)) / scl <= (x))
							if (((myFromCenter - offY)) / scl >= (y) && ((myFromCenter - offY)) / scl <= (y + 1)) {
								g2d.setColor(new Color(255, 0, 0));
								if (Main.inputController.isLeftDown()) {
									if (!didClick.get()) {
										AtomicBoolean hasThing = new AtomicBoolean(false);
										int finalY = y;
										int finalX = x;
										
										AtomicReference<BasicBlock> newSelectedBlock = new AtomicReference<>(null);
										Main.world.getColliders().forEach((collider) -> {
											if (collider instanceof BasicBlock) {
												if (
														(((BasicBlock) collider).getX()) == -(int) (screenX / 8) * 8 + (finalX * 8) &&
																((BasicBlock) collider).getY() == -(int) (screenY / 8) * 8 + (finalY * 8)
												) {
													hasThing.set(true);
													newSelectedBlock.set((BasicBlock) collider);
													hasBlockSelected = true;
												}
											}
										});
										if (newSelectedBlock.get() == null) {
											hasBlockSelected = false;
											guiComponentsBlock.clear();
											runClear();
										} else {
											runClear();
											guiComponentsBlock.clear();
											newSelectedBlock.get().addProperties(guiComponentsBlock);
											guiComponents.addAll(guiComponentsBlock);
										}
										
										if (!hasThing.get()) {
											if (selectedBlock != null) {
//												System.out.println(selectedBlock.getString());
												Main.world.addCollider(BasicBlock.fromString(selectedBlock.getString().split(",")).move(
														-(int) (screenX / 8) * 8 + (x * 8),
														-(int) (screenY / 8) * 8 + (y * 8)
												));
											} else {
												BasicBlock block = new BasicBlock(4, 4, () -> false);
												block.move(
														-(int) (screenX / 8) * 8 + (x * 8),
														-(int) (screenY / 8) * 8 + (y * 8)
												);
												block.setImmovable();
												Main.world.addCollider(block);
											}
										}
										selectedBlock = newSelectedBlock.get();
									}
								} else if (Main.inputController.isRightDown()) {
									int finalY = y;
									int finalX = x;
									try {
										Main.world.getColliders().forEach((collider) -> {
											if (collider instanceof BasicBlock) {
												if (
														((BasicBlock) collider).getX() == -(int) (screenX / 8) * 8 + (finalX * 8) &&
																((BasicBlock) collider).getY() == -(int) (screenY / 8) * 8 + (finalY * 8)
												) Main.world.removeCollider(collider);
											}
										});
									} catch (Throwable ignored) {
									}
								}
							}
						
						g2d.translate(0, 80);
						g2d.drawRect(1, 1, 77, 77);
						g2d.drawRect(2, 2, 75, 75);
					}
					g2d.setTransform(transform1);
				}
				g2d.translate(-4, -4);
				
				g2d.setTransform(transform);
			}
			{
				Image panel = sprites.get("editor_side_panel");
				int yPos = frame.getHeight() / 2 - panel.getHeight(null) / 2;
				float x = MathHelper.lerp(lPanelExtendProgress, 0, 120);
				g.setColor(new Color(63, 63, 63));
				g.fillRect(0, yPos, (int) x + 1, (panel.getHeight(null)));
				AffineTransform transform = g2d.getTransform();
				g2d.translate(x, 0);
				g.drawImage(panel, 0, yPos, null);
				for (int i = 0; i < guiComponentsBlock.size(); i++) {
					guiComponentsBlock.get(i).setX((x / (float) frame.getWidth()) - 0.1f);
					guiComponentsBlock.get(i).setY((i / 10f) + 0.3f);
				}
				g2d.setTransform(transform);
				isLPanelExtending = false;
				if (mx <= panel.getWidth(null) + x) {
					if (my >= yPos && my <= yPos + panel.getHeight(null)) {
						isLPanelExtending = true;
					}
				}
				
				if (isLPanelExtending) {
					lPanelExtendProgress += 0.01f;
					if (lPanelExtendProgress >= 1) {
						lPanelExtendProgress = 1;
					}
				} else {
					lPanelExtendProgress -= 0.01f;
					if (lPanelExtendProgress <= 0) {
						lPanelExtendProgress = 0;
					}
				}
			}
		} else if (Main.inGame) {
		} else {
			AffineTransform transform = g2d.getTransform();
			g2d.translate(frame.getWidth(), frame.getHeight());
			g2d.scale(4, 4);
			g2d.translate(-20, -30);
			if (old_player_title_anim_frame >= 4) {
				g2d.translate(16, 0);
				g2d.scale(-1, 1);
			}
			
			g.drawImage(old_player.get(old_player_title_anim[Math.min((int) old_player_title_anim_frame, old_player_title_anim.length - 1)]), 0, 0, 16, 20, null);
			g2d.setTransform(transform);
			
			old_player_title_anim_frame += 0.25f;
			if (old_player_title_anim_frame > old_player_title_anim.length)
				old_player_title_anim_frame = 0;
			
			g2d.setColor(new Color(255, 255, 255));
		}
		
		g2d.scale(frame.getWidth(), frame.getHeight());
		
		AtomicInteger index = new AtomicInteger(0);
		guiComponents.forEach((component) -> {
			component.draw((Graphics2D) g, component.isInBounds(finalMx, finalMy), focusedComponent == index.get());
			if (component.isInBounds(finalMx, finalMy) && Main.inputController.isLeftDown()) {
				component.onClick();
				focusedComponent = index.get();
			}
			index.getAndAdd(1);
		});
		
		if (shouldClear) {
			runClear();
		}
	}
	
	public void queueClear() {
		shouldClear = true;
	}
	
	public void addClearListener(Runnable onClear) {
		clearListeners.add(onClear);
	}
	
	public void runClear() {
		guiComponents.clear();
		shouldClear = false;
		clearListeners.forEach(Runnable::run);
		focusedComponent = -1;
	}
}
