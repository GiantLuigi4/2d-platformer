package com.tfc.platformer.display;

import com.tfc.physics.wrapper.common.API.WrapperWorld;
import com.tfc.physics.wrapper.common.backend.interfaces.ICollider;
import com.tfc.platformer.Main;
import com.tfc.platformer.utils.SpriteSheet;
import com.tfc.platformer.utils.math.MathHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class GraphicsWorld extends JComponent {
	private final WrapperWorld world;
	private final ICollider player;
	private final JFrame frame;
	
	public static final SpriteSheet legacy = new SpriteSheet("assets/textures/levels/legacy");
	protected float lastPosX = 0;
	
	public static final SpriteSheet mintyFields = new SpriteSheet("assets/textures/levels/minty");
	protected float lastPosY = 0;
	
	public GraphicsWorld(WrapperWorld world, ICollider player, JFrame frame) {
		this.world = world;
		this.player = player;
		this.frame = frame;
	}
	
	private final ArrayList<Component> children = new ArrayList<>();
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		g.setColor(new Color(0));
		g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
		
		if (Main.inEditor || Main.inGame) {
			g.setColor(new Color(255, 255, 255));
			
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform transform = g2d.getTransform();
			
			g2d.translate(frame.getWidth() / 2f, frame.getHeight() / 2f);
			if (!Main.inEditor) {
				g2d.scale(10, 10);
			} else {
				g2d.scale(5, 5);
			}
			
			g2d.translate(lastPosX, lastPosY);
			if (Main.inEditor) {
				lastPosX = -player.getPositionSupplier().get().x;
				lastPosY = -player.getPositionSupplier().get().y;
			}
			lastPosX = MathHelper.lerp(0.1f, lastPosX, -player.getPositionSupplier().get().x);
			lastPosY = MathHelper.lerp(0.1f, lastPosY, -player.getPositionSupplier().get().y);
			
			world.getColliders().forEach(collider -> {
				AffineTransform transform1 = g2d.getTransform();
				collider.draw(g2d);
				g2d.setTransform(transform1);
			});
			player.draw((Graphics2D) g);

//			Main.oldPlayerNPC.render(g2d);
			Main.defaultPlayerNPC.render(g2d);
			
			g2d.setTransform(transform);
		}
		
		children.forEach((c) -> {
			c.paint(g);
		});
	}
	
	public void addChild(Component g) {
		children.add(g);
	}
	
	public static SpriteSheet getSheet(String name) {
		switch (name) {
			case "minty":
				return mintyFields;
			case "legacy":
				return legacy;
			default:
				return null;
		}
	}
	
	public static Collection<String> getAllSheets() {
		return Arrays.asList("minty", "legacy");
	}
}
