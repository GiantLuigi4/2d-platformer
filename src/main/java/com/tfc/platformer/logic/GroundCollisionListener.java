package com.tfc.platformer.logic;

import com.tfc.physics.wrapper.common.API.colliders.BoxCollider;
import com.tfc.physics.wrapper.common.API.listeners.CollisionListener;
import com.tfc.physics.wrapper.common.backend.collision.Collision;
import com.tfc.physics.wrapper.common.backend.collision.CollisionPreSolve;
import com.tfc.physics.wrapper.common.backend.interfaces.ICollider;
import com.tfc.platformer.logic.colliders.EntityCollider;
import com.tfc.platformer.logic.helpers.Box2D;
import com.tfc.platformer.logic.helpers.Line2D;

public class GroundCollisionListener extends CollisionListener {
	@Override
	public void preSolve(CollisionPreSolve collision) {
		test(collision.bodyA,collision.bodyB);
		test(collision.bodyB,collision.bodyA);
	}
	
	@Override
	public void finishContact(Collision collision) {
		if (collision.bodyA instanceof EntityCollider) ((EntityCollider) collision.bodyA).setOnGround(false);
		if (collision.bodyB instanceof EntityCollider) ((EntityCollider) collision.bodyB).setOnGround(false);
	}
	
	private void test(ICollider bodyA, ICollider bodyB) {
		if (bodyA instanceof EntityCollider && bodyA.getPositionSupplier().get().y <= bodyB.getPositionSupplier().get().y) {
			if (bodyA instanceof BoxCollider && bodyB instanceof BoxCollider) {
				if (createBox((BoxCollider)bodyA).collides(createBox((BoxCollider)bodyB)))
						((EntityCollider) bodyA).setOnGround(true);
			} else {
				((EntityCollider) bodyA).setOnGround(true);
			}
		}
	}
	
	private Box2D createBox(BoxCollider collider) {
		return new Box2D(new Line2D(
				collider.getPositionSupplier().get().x-(collider.width-0.5f),collider.getPositionSupplier().get().y-(collider.height+0.1f),
				collider.getPositionSupplier().get().x+(collider.width-0.5f),collider.getPositionSupplier().get().y+(collider.height+0.1f)
		));
	}
}
