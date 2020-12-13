package com.tfc.platformer.display;

import com.tfc.physics.wrapper.common.API.WrapperWorld;
import com.tfc.physics.wrapper.common.backend.interfaces.ICollider;
import com.tfc.platformer.Main;
import com.tfc.platformer.logic.gui.GuiComponent;
import com.tfc.platformer.utils.SpriteMap;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class GraphicsUI extends JComponent {
	private final ICollider player;
	private final JFrame frame;
	
	public ArrayList<GuiComponent> guiComponents = new ArrayList<>();
	public ArrayList<Runnable> clearListeners = new ArrayList<>();
	
	public static final SpriteMap sprites = new SpriteMap("assets/textures/level_editor");
	public static final SpriteMap old_player = new SpriteMap("assets/npcs/old_player_char");
	
	private boolean shouldClear = false;
	
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
	
	public GraphicsUI(ICollider player, JFrame frame) {
		this.player = player;
		this.frame = frame;
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
			Image panel = sprites.get("editor_side_panel");
			int yPos = frame.getHeight() / 2 - panel.getHeight(null) / 2;
			g.drawImage(panel, 0, yPos, null);
			if (mx <= panel.getWidth(null)) {
				if (my >= yPos && my <= yPos + panel.getHeight(null)) {
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
