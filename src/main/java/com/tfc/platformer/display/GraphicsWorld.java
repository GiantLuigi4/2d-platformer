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

public class GraphicsWorld extends JComponent {
	private final WrapperWorld world;
	private final ICollider player;
	private final JFrame frame;
	
	private float lastPosX = 0;
	private float lastPosY = 0;
	
	public static final SpriteSheet mintyFields = new SpriteSheet("assets/textures/levels/minty");
	
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
			g2d.scale(10,10);
			
			g2d.translate(lastPosX,lastPosY);
			lastPosX = MathHelper.lerp(0.1f,lastPosX,-player.getPositionSupplier().get().x);
			lastPosY = MathHelper.lerp(0.1f,lastPosY,-player.getPositionSupplier().get().y);
			
			world.getColliders().forEach(collider -> {
				AffineTransform transform1 = g2d.getTransform();
				collider.draw(g2d);
				g2d.setTransform(transform1);
			});
			player.draw((Graphics2D)g);

			g2d.setTransform(transform);
		}
		
		children.forEach((c)->{
			c.paint(g);
		});
	}
	
	public void addChild(Component g) {
		children.add(g);
	}
}
