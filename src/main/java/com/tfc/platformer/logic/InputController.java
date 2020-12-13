package com.tfc.platformer.logic;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class InputController implements KeyListener, MouseListener, MouseMotionListener {
	ArrayList<Integer> keys = new ArrayList<>();
	boolean isLeftDown = false;
	
	Point mouse = null;
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (!keys.contains(e.getKeyCode())) {
			keys.add(e.getKeyCode());
			System.out.println(e.getKeyCode());
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if (keys.contains(e.getKeyCode())) {
			keys.remove((Object) e.getKeyCode());
		}
	}
	
	public boolean isKeyPressed(int d) {
		return keys.contains(d);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == 1) {
			isLeftDown = true;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == 1) {
			isLeftDown = false;
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	
	}
	
	public boolean isLeftDown() {
		return isLeftDown;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		mouse = e.getPoint();
	}
	
	public Point getMouse() {
		return mouse;
	}
}
