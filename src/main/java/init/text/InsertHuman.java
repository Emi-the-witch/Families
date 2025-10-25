package init.text;

import init.race.Race;
import init.type.HCLASS;
import init.type.HTYPE;
import settlement.entity.ENTITY;
import settlement.entity.humanoid.Humanoid;
import settlement.main.SETT;
import settlement.stats.Induvidual;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.datatypes.DIR;
import snake2d.util.sets.ArrayListGrower;
import snake2d.util.sets.LIST;
import snake2d.util.sprite.text.Str;
import util.data.GETTER_TRANS;
import util.dic.Dic;

import java.util.Arrays;

public final class InsertHuman extends Inserter<Humanoid>{

	public static LIST<Humanoid> getReproductiveMembers(HTYPE htype, Race race) {
		ArrayListGrower<Humanoid> members = new ArrayListGrower<>();

		ENTITY[] allEntities = SETT.ENTITIES().getAllEnts();
		int maxIndex = SETT.ENTITIES().Imax();

		for (int i = 0; i <= maxIndex; i++) {
			ENTITY e = allEntities[i];

			if (!(e instanceof Humanoid)) {
				continue;
			}

			Humanoid h = (Humanoid) e;
			Induvidual ind = h.indu();

			if (ind.hType() != htype || ind.race() != race) {
				continue;
			}

			members.add(h);
		}
		return members;
	}

	InsertHuman(){
		super();
		new II("TITLE") {
			@Override
			public void set(Humanoid t, Str str) {
				str.add(t.title());
			}
		}; 
		new II("WEIGHT") {
			@Override
			public void set(Humanoid a, Str str) {
				str.add(a.physics.getMass(), 1);
			}
		}; 
		new II("HEIGHT") {
			@Override
			public void set(Humanoid a, Str str) {
				str.add(a.physics.getHeight(), 1);
			}
		};

		/// #!# Add mom and dad
		new II("MOM") {
			@Override
			public void set(Humanoid a, Str str) {
				str.add(a.physics.getMom());

			}
		};
		new II("DAD") {
			@Override
			public void set(Humanoid a, Str str) {
				str.add(a.physics.getDad());
			}
		};
		/// #!#

		new II("LOC") {
			@Override
			public void set(Humanoid b, Str str) {
				if (b != null) {
					DIR d = DIR.get(SETT.TWIDTH/2, SETT.THEIGHT/2, b.tc().x(), b.tc().y());
					if (COORDINATE.tileDistance(SETT.TWIDTH/2, SETT.THEIGHT/2, b.tc().x(), b.tc().y()) < 150)
						d = DIR.C;
					str.add(Dic.get(d));
				}
			}
		};
		
		join(new InsertIndu(), new GETTER_TRANS<Humanoid, Induvidual>(){

			@Override
			public Induvidual get(Humanoid f) {
				if (f == null)
					return null;
				return f.indu();
			}
			
		});
	}
	
}