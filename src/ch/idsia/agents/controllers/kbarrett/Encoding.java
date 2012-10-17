package ch.idsia.agents.controllers.kbarrett;

import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;

/**
 * This class stores the encodings of all objects stored in levelScene. 
 * @author Kim
 */
public class Encoding 
{

	/**
	 * Used to check type. 
	 * If an encoding < ENVIRONMENTTYPE, it is a enviroment piece (e.g. brick/wall/floor). 
	 * If it is > ENVIRONMENTTYPE, it is a Sprite (e.g. enemy/coin).
	 * Note: if they are equal, the encoding represents nothing.
	 * @see ch.idsia.agents.controllers.kbarrett.Encoding#isEnvironment(byte)
	 * @see ch.idsia.agents.controllers.kbarrett.Encoding#isSprite(byte)
	 */
	static final public byte ENVIRONMENTTYPE = 0;
	/**
	 * Used to check whether a given encoding is an Environment type.
	 * @param type - the encoding of the piece
	 * @return boolean indicating whether it is an Environment piece or not.
	 */
	static final public boolean isEnvironment(byte type)
	{
		return type < ENVIRONMENTTYPE;
	}
	/**
	 * Used to check whether a given encoding is a Sprite type.
	 * @param type - the encoding of the piece
	 * @return boolean indicating whether it is an Sprite piece or not.
	 */
	static final public boolean isSprite(byte type)
	{
		return type > ENVIRONMENTTYPE;
	}
	
	//Encodings of Environment : represented by negative integers
			/** 
			 * If there is nothing in the square.
			 */
			static final public byte NOTHING = 0;
			static final public byte FLOOR = GeneralizerLevelScene.BORDER_CANNOT_PASS_THROUGH;
			static final public byte WALL = -112;
			static final public byte CORNERTOPLEFT = -128;
			static final public byte FLOWERPOT = GeneralizerLevelScene.FLOWER_POT;
			static final public byte BORDER_HILL = GeneralizerLevelScene.BORDER_HILL;
			static final public byte LADDER = GeneralizerLevelScene.LADDER;
			static final public byte CANNON_MUZZLE = GeneralizerLevelScene.CANNON_MUZZLE;
			static final public byte CANNON_TRUNK = GeneralizerLevelScene.CANNON_TRUNK;
			/**
			 * This has a question mark on it & could contain a coin, a flower, a mushroom or some coins.
			 * Note: always contains something.
			 */
			static final public byte BRICK = GeneralizerLevelScene.UNBREAKABLE_BRICK;
			/**
			 *  This could contain coins, a friendly flower or nothing.
			 */
			static final public byte BREAKABLE_BRICK = GeneralizerLevelScene.BREAKABLE_BRICK;
	
	//Encodings of Sprites : represented by positive integers
			
		//Friendly : encodings are numbers between 1 and 5
			/**
			 * Note: Encoding same as a mushroom.
			 * @see ch.idsia.agents.controllers.kbarrett.Encoding#MUSHROOM
			 */
			static final public byte COIN = GeneralizerLevelScene.COIN_ANIM;
			/**
			 * Note: Encoding same as a coin.
			 * @see ch.idsia.agents.controllers.kbarrett.Encoding#COIN
			 */
			static final public byte MUSHROOM = Sprite.KIND_MUSHROOM;
			static final public byte FIRE_FLOWER = Sprite.KIND_FIRE_FLOWER;
			static final public byte PRINCESS = GeneralizerLevelScene.PRINCESS;
			
		//Enemies : encodings are numbers between 80 and 100
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
			
		//Other
			static final public byte ENEMY_FIREBALL = Sprite.KIND_FIREBALL;
	
	
	//DEBUG:
	private boolean debug = FirstAgent.debug; 
	public static int[] knownThings = {0, 2, -60, -112, -128, -90, -62, 61, -22, -20, -82, -80, 5, 3, 
		/*Enemies from here*/ 25, 84, 80, 95, 81, 96, 82, 97, 13, 98, 93, 91, 99};
}
