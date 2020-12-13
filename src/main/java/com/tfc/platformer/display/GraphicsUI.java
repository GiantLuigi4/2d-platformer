package com.tfc.platformer.display;

import com.tfc.physics.wrapper.common.backend.interfaces.ICollider;
import com.tfc.platformer.Main;
import com.tfc.platformer.logic.gui.GuiComponent;
import com.tfc.platformer.utils.SpriteMap;
import com.tfc.platformer.utils.math.MathHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class GraphicsUI extends JComponent {
	private final GraphicsWorld worldGraphics;
	private final ICollider player;
	private final JFrame frame;
	
	public ArrayList<GuiComponent> guiComponents = new ArrayList<>();
	public ArrayList<Runnable> clearListeners = new ArrayList<>();
	
	public static final SpriteMap sprites = new SpriteMap("assets/textures/level_editor");
	public static final SpriteMap old_player = new SpriteMap("assets/npcs/old_player_char");
	
	private boolean shouldClear = false;
	
	private float lPanelExtendProgress = 0;
	private boolean isLPanelExtending = false;
	
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
			mx = ((float)(mouse.x-7)/frame.getWidth());
			my = ((float)(mouse.y-27)/frame.getHeight());
		}
		
		final float finalMx = mx;
		final float finalMy = my;
		
		if (mouse != null) {
			mx *= frame.getWidth();
			my *= frame.getHeight();
		}
		
		g.setColor(new Color(0, 0, 0, 0));
		
		Graphics2D g2d = ((Graphics2D) g);
		
		if (Main.inEditor) {
			{
				AffineTransform transform = g2d.getTransform();
				
				g2d.translate(frame.getWidth() / 2f, frame.getHeight() / 2f);
				g2d.scale(10, 10);
				
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
				
				mxFromCenter -= 55;
				myFromCenter += 40;
				
				float screenX = worldGraphics.lastPosX;
				float screenY = worldGraphics.lastPosY;
				
				if (Main.inEditor) {
					for (int x = -10; x <= 10; x++) {
						AffineTransform transform1 = g2d.getTransform();
						g2d.translate(x * 8, -80);
						g2d.scale(0.1f, 0.1f);
						for (int y = -10; y <= 10; y++) {
							if (x == 0 || y == 0) {
								g2d.setColor(new Color(0, 255, 0));
							} else {
								g2d.setColor(new Color(128, 128, 128));
							}
							
							if (((mxFromCenter + offX)) / 80 >= (x - 1) && ((mxFromCenter + offX)) / 80 <= (x))
								if (((myFromCenter - offY)) / 80 >= (y) && ((myFromCenter - offY)) / 80 <= (y + 1))
									g2d.setColor(new Color(255, 0, 0));
							
							g2d.translate(0, 80);
							g2d.drawRect(1, 1, 77, 77);
							g2d.drawRect(2, 2, 75, 75);
						}
						g2d.setTransform(transform1);
					}
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
		
		g2d.scale(frame.getWidth(),frame.getHeight());
		
		guiComponents.forEach((component) -> {
			component.draw((Graphics2D) g, component.isInBounds(finalMx, finalMy));
			if (component.isInBounds(finalMx,finalMy) && Main.inputController.isLeftDown()) {
				component.onClick();
			}
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
	}
}
