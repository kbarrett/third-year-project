package ch.idsia.agents.controllers.kbarrett;

import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;

public class Encoding {

	//Encodings of Environment
	
	static final public byte COIN = GeneralizerLevelScene.COIN_ANIM;
	
	static final public byte FLOOR = GeneralizerLevelScene.BORDER_CANNOT_PASS_THROUGH;
	static final public byte WALL = -112;
	static final public byte CORNERTOPLEFT = -128;
	static final public byte FLOWERPOT = GeneralizerLevelScene.FLOWER_POT;
	static final public byte BORDER_HILL = GeneralizerLevelScene.BORDER_HILL;
	
	static final public byte LADDER = GeneralizerLevelScene.LADDER;
	
	/*
	 * This has a question mark on it & could contain a coin, a flower, a mushroom or some coins.
	 * Note: always contains something.
	 */
	static final public byte BRICK = GeneralizerLevelScene.UNBREAKABLE_BRICK;
	/*
	 *  This could contain coins, a friendly flower or nothing.
	 */
	static final public byte BREAKABLE_BRICK = GeneralizerLevelScene.BREAKABLE_BRICK;
	
	static final public byte CANNON_MUZZLE = GeneralizerLevelScene.CANNON_MUZZLE;
	static final public byte CANNON_TRUNK = GeneralizerLevelScene.CANNON_TRUNK;
	static final public byte PRINCESS = GeneralizerLevelScene.PRINCESS;
	
	//Encodings of Sprites
	static final public byte ENEMY_FIRE_FLOWER = Sprite.KIND_FIRE_FLOWER;
	static final public byte ENEMY_MUSHROOM = Sprite.KIND_MUSHROOM;
	static final public byte ENEMY_FIREBALL = Sprite.KIND_FIREBALL;
	static final public byte ENEMY_BULLET = Sprite.KIND_BULLET_BILL;
	static final public byte ENEMY_GOOMBA = Sprite.KIND_GOOMBA;
	static final public byte ENEMY_GOOMBA_WING = Sprite.KIND_GOOMBA_WINGED;
	static final public byte ENEMY_GREEN_KOOPA = Sprite.KIND_GREEN_KOOPA;
	static final public byte ENEMY_GREEN_KOOPA_WING = Sprite.KIND_GREEN_KOOPA_WINGED;
	static final public byte ENEMY_RED_KOOPA = Sprite.KIND_RED_KOOPA;
	static final public byte ENEMY_RED_KOOPA_WING = Sprite.KIND_RED_KOOPA_WINGED;
	static final public byte ENEMY_SHELL = Sprite.KIND_SHELL;
	static final public byte ENEMY_WAVE_GOOMBA = Sprite.KIND_WAVE_GOOMBA;
	static final public byte ENEMY_SPIKY = Sprite.KIND_SPIKY;
	static final public byte ENEMY_FLOWER = Sprite.KIND_ENEMY_FLOWER;
	static final public byte ENEMY_SKIPY_WINGED = Sprite.KIND_SPIKY_WINGED;
	
	
	//DEBUG:
	public static int[] knownThings = {0, 2, -60, -112, -128, -90, -62, 61, -22, -20, -82, -80, 5, 3, 25, 84, 80, 95, 81, 96, 82, 97, 13, 98, 93, 91, 99};
}
