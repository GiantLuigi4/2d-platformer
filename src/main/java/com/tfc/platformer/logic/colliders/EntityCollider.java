package com.tfc.platformer.logic.colliders;

import java.util.concurrent.atomic.AtomicBoolean;

public interface EntityCollider {
	AtomicBoolean isOnGround = new AtomicBoolean(false);
	
	default boolean isOnGround() {
		return isOnGround.get();
	}
	
	default void setOnGround(boolean onGround) {
		isOnGround.set(onGround);
	}
}
