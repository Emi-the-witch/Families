package settlement.entity;

import game.GAME;
import game.GameSpec;
import game.save.GameLoader;
import init.C;
import init.paths.ModInfo;
import init.paths.PATH;
import init.paths.PATHS;
import snake2d.util.datatypes.BODY_HOLDER;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.datatypes.Coo;
import snake2d.util.datatypes.Rec;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import snake2d.util.rnd.RND;
import java.io.IOException;
import java.util.Objects;
import static modchecker.ModCheckScript.ss;

public interface EPHYSICS extends BODY_HOLDER{
	///#!#
	boolean  Save_Var = false;
	///#!#

	public static int MOM_TRESHOLD = 7*C.TILE_SIZE;
	public static double MOM_TRESHOLDI = 1.0/MOM_TRESHOLD;
	
	/**
	 * 
	 * @return the tile which the centre occupies
	 */
	public COORDINATE tileC();

	/**
	 * 
	 * @return Height of the entity.
	 */
	public double getHeight();
	/**
	 * 
	 * @return the hight over ground
	 */
	public double getZ();
	/**
	 * 
	 * @return bounciness of entity.
	 */
	public double getRestitution();
	/**
	 * 
	 * @return mass of the entity. -1 for solid. 0 for no mass
	 */
	public double getMass();
	
	/**
	 * 
	 * @return inverted mass. Note that mass <= 0 will have inverted mass = 0
	 */
	public double getMassI();
	
	/**
	 * 
	 * @param other
	 * @return - 1 if other is "taller" than this and should be rendered above
	 * 			 1 if opposite
	 */
	public default int compareRenderHeight(EPHYSICS other){
		double h1 = getHeight()+getZ();
		double h2 = other.getHeight()+other.getZ();
		if (h1 == h2)
			return 0;
		
		return h1 - h2 < 0 ? -1 : 1;
	}
	
	public static class Solid implements EPHYSICS{

		private static final int MASK = ~(C.SCALE-1);


		/**
		 * Can be used by by all static entities 
		 */
		protected double heightOverGround = 0;
		protected double height = RND.rFloat(0.01);
		protected double restitution = 0.2f;
		///#!#  Add "mom" and "dad" to the physics info of an individual. e.g.
		String mom = "unknown mother";
		String dad = "unknown father";
		/// #!#
		private float mass = 1;
		protected float massI = 0;
		protected final Rec hitbox = new Rec() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Rec moveX1(double X1) {
				super.moveX1(X1);
				currentTile.set(cX() >> C.T_SCROLL, currentTile.y());
				return this;
			};
			
			@Override
			public Rec moveY1(double Y1) {
				super.moveY1(Y1);
				currentTile.set(currentTile.x(), cY() >> C.T_SCROLL);
				return this;
				
			};
		};
		protected final Coo currentTile = new Coo();
		int x1,x2,y1,y2 = -1;
		/**
		 * loss/s
		 */
		protected static final double AIR_REDUCER = 0.005;
		/**
		 * m/s/mass
		 */
		protected static final double GROUND_FRICTION = C.TILE_SIZE;
		private final static double FLIGHTM = C.TILE_SIZE*10;
		short tx1,tx2,ty1,ty2;
		
		void initMoveCheck(){
			x1 = body().x1();
			x2 = body().x2();
			y1 = body().y1();
			y2 = body().y2();
			
		}
		
		boolean MoveCheck(){
			
//			return (x1 -  body().x1()) >> SCROLL == 0 && (x2 - body().x2()) >> SCROLL == 0
//					&& (y1 - body().y1()) >> SCROLL == 0 &&  (y2 - body().y2()) >> SCROLL == 0;
//			
			return ((x1 &MASK) == (body().x1()&MASK) && (y1&MASK) == (body().y1()&MASK));
			
//			return (x1 == body().x1() && x2 == body().x2() && y1 == body().y1() && y2 == body().y2());
		}
		
		@Override
		public double getHeight(){return height;}
		public void setHeight(double height){this.height = height;}

/// #!# New functions to overwrite the default "" mom and dad I created above.
		public String getMom(){return mom;}
		public void setMom(String mom){this.mom = mom;}

		public String getDad(){return dad;}
		public void setDad(String dad){this.dad = dad;}


/// #!#


		@Override
		public double getRestitution(){return restitution;}
		public void setRestitution(float r){this.restitution = r;}
		
		@Override
		public double getMass(){return mass;}
		public void setMass(double mass){
			this.mass = (float) mass; 
			if (mass > 0)
				this.massI = 1f/this.mass;
			else
				this.massI = 0;
		}
		@Override
		public double getMassI() {
			return massI;
		}
		
		@Override
		public double getZ(){return heightOverGround;}
		public void setHeightOverGround(double height){this.heightOverGround = height;}
		
		@Override
		public Rec body() {
			return hitbox;
		}
		
		@Override
		public COORDINATE tileC(){
			return currentTile;
		}
		
		public void initPosition(Solid other){
			hitbox.set(other);
			currentTile.set(other.currentTile);
		}
		
		public void initPosition(int x, int y, int hitBoxWidth, int hitBoxHeight){
			hitbox.setWidth(hitBoxWidth);
			hitbox.setHeight(hitBoxHeight);
			hitbox.moveC(x, y);
			currentTile.set(x >> C.T_SCROLL, y >> C.T_SCROLL);
		}
		
		public void initPosition(BODY_HOLDER e) {
			this.initPosition(e.body().cX(), e.body().cY(), e.body().width(), e.body().height());
		}
		
		public boolean move(ENTITY e, ESpeed.Imp speed, float ds) {
			if (speed.magnitude() > 0){
				hitbox.incr(speed.x()*ds, speed.y()*ds);
				return true;
			}
			
			return false;
		
		}

		public double getFlightMomentum() {
			return getMass()*FLIGHTM;
		}

		public boolean isWithinTile() {
			return tx1 == tx2 && ty1 == ty2;
		}
		
		public int tx1() {
			return tx1;
		}
		public int tx2() {
			return tx2;
		}
		public int ty1() {
			return ty1;
		}
		public int ty2() {
			return ty2;
		}

		void save(FilePutter file) {
			file.d(heightOverGround);
			file.d(height);
			///#!#
			if(Save_Var == true){
				file.chars(mom);
				file.chars(dad);
			}
			///#!#
			file.d(restitution);
			file.f(mass);
			file.f(massI);
			hitbox.save(file);
			currentTile.save(file);
			file.s(tx1).s(tx2).s(ty1).s(ty2);
		}
		
		void load(FileGetter file) throws IOException {
			heightOverGround = file.d();
			height = file.d();
			///#!#
            try {
                for (String mm : ss.mods) {
                    if (Objects.equals(mm, "Families")) {
                        mom = file.chars();
                        dad = file.chars();
                    }
                    break;
                }
            } catch(Exception e) {}
			///#!#
			restitution = file.d();
			mass = file.f();
			massI = file.f();
			hitbox.load(file);
			currentTile.load(file);
			tx1 = file.s();
			tx2 = file.s();
			ty1 = file.s();
			ty2 = file.s();
		}


		
	}
	
}

