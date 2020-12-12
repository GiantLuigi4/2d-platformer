package com.tfc.platformer.logic;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class InputController implements KeyListener {
	ArrayList<Integer> keys = new ArrayList<>();
	
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
}
