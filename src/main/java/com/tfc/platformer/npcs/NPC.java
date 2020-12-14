package com.tfc.platformer.npcs;

import com.tfc.platformer.Main;
import com.tfc.platformer.logic.helpers.Vector2D;

import java.awt.*;
import java.util.ArrayList;

public class NPC {
	public final ArrayList<Vector2D> pathFindingNodes = new ArrayList<>();
	public double x, y, jumpHeight, width, height, moveSpeed;
	public double goalX = 0;
	public double goalY = 0;
	
	public NPC(double x, double y, double jumpHeight, double width, double height, double moveSpeed) {
		this.x = x;
		this.y = y;
		this.jumpHeight = jumpHeight;
		this.width = width;
		this.height = height;
		this.moveSpeed = moveSpeed;
	}
	
	public void tick() {
//		pathFindingNodes.clear();
		//TODO: make it make sure jumps are possible instead of just assuming that it is
		if (pathFindingNodes.isEmpty()) {
			Vector2D point = new Vector2D(x + 8, y);
			if (Main.getBlock((int) (x / 8 + 1), (int) (y / 8)) == null) {
				if (Main.getBlock((int) (x / 8 + 1), (int) (y / 8 + 1)) != null) {
					if (!pathFindingNodes.contains(point)) pathFindingNodes.add(point);
				} else pathFindingNodes.remove(point);
			} else pathFindingNodes.remove(point);
		} else {
			Vector2D lastNode = pathFindingNodes.get(pathFindingNodes.size() - 1);
			if (
					Main.getBlock((int) (lastNode.x / 8), (int) (lastNode.y / 8) + 1) == null ||
							Main.getBlock((int) (lastNode.x / 8), (int) (lastNode.y / 8)) != null
			) {
				pathFindingNodes.remove(lastNode);
				return;
			}
			boolean hasMetGoal = false;
			if (lastNode.dist(new Vector2D(goalX, goalY)) <= 1) hasMetGoal = true;
			if (!hasMetGoal) {
				boolean isGoingLeft = goalX < lastNode.x;
				boolean goingUp = goalY < lastNode.y;
				for (int offX = 0; offX++ < moveSpeed; ) {
					double min = -Math.ceil(jumpHeight);
					double max = jumpHeight;
//					if (goingUp) {
//						max = jumpHeight;
//						min = -4;
//					}
					for (double offY = min; offY++ < max; ) {
						Vector2D point = new Vector2D(lastNode.x + (isGoingLeft ? -offX : offX) * 8, lastNode.y + -offY * 8);
						if (Main.getBlock((int) (lastNode.x / 8 + (isGoingLeft ? -offX : offX)), (int) (lastNode.y / 8 + -offY)) == null) {
							if (Main.getBlock((int) (lastNode.x / 8 + (isGoingLeft ? -offX : offX)), (int) (lastNode.y / 8 + -offY + 1)) != null) {
								if (!pathFindingNodes.contains(point)) {
									pathFindingNodes.add(point);
									return;
								}
							} else pathFindingNodes.remove(point);
						} else {
							pathFindingNodes.remove(point);
							if (offX == 1) {
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public void render(Graphics2D g2d) {
		g2d.setColor(new Color(255, 0, 0));
		for (int node = 0; node++ < pathFindingNodes.size() - 1; ) {
			g2d.drawLine(
					(int) pathFindingNodes.get(node - 1).x,
					(int) pathFindingNodes.get(node - 1).y,
					(int) (pathFindingNodes.get(node).x),
					(int) (pathFindingNodes.get(node).y)
			);
			g2d.drawRect((int) (pathFindingNodes.get(node).x) - 2, (int) (pathFindingNodes.get(node).y) - 2, 4, 4);
			g2d.drawRect((int) (pathFindingNodes.get(node - 1).x) - 2, (int) (pathFindingNodes.get(node - 1).y) - 2, 4, 4);
		}
		if (!pathFindingNodes.isEmpty()) {
			int node = 0;
			g2d.drawLine(
					(int) x,
					(int) y,
					(int) (pathFindingNodes.get(node).x - x),
					(int) (pathFindingNodes.get(node).y - y)
			);
		}
		g2d.setColor(new Color(255, 255, 255));
		g2d.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) (x + width), (int) (y + height));
		g2d.setColor(new Color(0, 255, 0));
//		g2d.fillRect((int)(goalX-width/2),(int)(goalY-height/2),(int)(width),(int)(height));
		g2d.drawRect((int) (goalX) - 2, (int) (goalY) - 2, 4, 4);
	}
}
