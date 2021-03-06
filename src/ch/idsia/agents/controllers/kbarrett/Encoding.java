package ch.idsia.agents.controllers.kbarrett;

import ch.idsia.agents.controllers.kbarrett.first.MapSquare;
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
	 * @see ch.idsia.agents.controllers.kbarrett.Encoding#isEnemySprite(byte)
	 */
	static final private byte ENVIRONMENTTYPE = 0;
	static final private byte ENEMYTYPE = 80; 
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
	 * Used to check whether the encoding for a given MapSquare is an Environment type.
	 * @param type - the piece for which the encoding should be checked
	 * @return boolean indicating whether it is an Environment piece or not.
	 */
	static final public boolean isEnvironment(MapSquare type)
	{
		if(type==null) {return false;}
		return isEnvironment(type.getEncoding());
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
	/**
	 * Used to check whether the encoding for a given MapSquare is a Sprite type.
	 * @param type - the piece for which the encoding should be checked
	 * @return boolean indicating whether it is an Sprite piece or not.
	 */
	static final public boolean isSprite(MapSquare type)
	{
		if(type==null) {return false;}
		return isSprite(type.getEncoding());
	}
	
	static final public boolean isEnemySprite(byte type)
	{
		return type >= ENEMYTYPE;
	}
	static final public boolean isEnemySprite(MapSquare type)
	{
		if(type==null) {return false;}
		return isEnemySprite(type.getEncoding());
	}
	/**
	 * Reduces the number of possible values that the elements of an array can take.
	 * @param encoding - the unsimplified encoding
	 * @return a simplified representation of the encoding.
	 */
	static public byte simplify(byte encoding)
	{
		if(isEnvironment(encoding))
		{
			//Map all environment encodings to the same value, as they all affect Mario in the same way.
			return -1;
		}
		else if (encoding == FIREBALL)
		{
			//Fireballs don't affect Mario so ignore them.
			return 0;
		}
		else return encoding;
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
			
			static final public byte ENVIRONMENT1 = -110;
			static final public byte ENVIRONMENT2 = -96;
			static final public byte ENVIRONMENT3 = -94;
			static final public byte ENVIRONMENT4 = -126;
			static final public byte ENVIRONMENT5 = -125;
			static final public byte ENVIRONMENT6 = -109;
			static final public byte ENVIRONMENT7 = -76;
	
	//Encodings of Sprites : represented by positive integers
			
		//Friendly : encodings are numbers between 1 and 25
			/**
			 * Note: Encoding same as a {@link #MUSHROOM}.
			 */
			static final public byte COIN = GeneralizerLevelScene.COIN_ANIM;
			/**
			 * Note: Encoding same as a {@link #COIN}.
			 */
			static final public byte MUSHROOM = Sprite.KIND_MUSHROOM;
			/**
			 * This is a flower that gives you extra life.
			 */
			static final public byte FIRE_FLOWER = Sprite.KIND_FIRE_FLOWER;
			/**
			 * This is the princess, which signals the end of a level.
			 */
			static final public byte PRINCESS = GeneralizerLevelScene.PRINCESS;
			/**
			 * This is the fireball, that Mario can shoot when he "presses" run in fire mode.
			 */
			static final public byte FIREBALL = Sprite.KIND_FIREBALL;
			
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
	
	
	//DEBUG:
	public static int[] knownThings = 
		{
		/*Other*/ 0, 2, -60, -112, -128, -90, -62, 61, -22, -20, -82, -80, 5, 3, -110, -96, -94, -126, -125, -109, -76,
		/*Enemies*/ 25, 84, 80, 95, 81, 96, 82, 97, 13, 98, 93, 91, 99
		};
}
