package com.traps.trapta;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Database {

	private MySQLiteHelper dbHelper;
	
	public Database(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}
	
	public void addVolley(Archer archer, int heatIndex, int volleyIndex, Volley volley) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues arrowMap = new ContentValues(10);
		arrowMap.put("id", archer.getId());
		arrowMap.put("run", heatIndex);
		arrowMap.put("volley", volleyIndex);
		int valueArray[] = volley.getArrowArray();
		for (int i=0; i<valueArray.length; i++) arrowMap.put("arrow"+i, valueArray[i]);
		long row = db.insert("volleytable", null, arrowMap);
		if (row<0) {
			Log.e("DB", "Cannot insert volley for "+archer.getName());
		}
		db.close();
		
	}
	
	public void updateVolley(Archer archer, int heatIndex, int volleyIndex, Volley volley) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues arrowMap = new ContentValues(6);
		int valueArray[] = volley.getArrowArray();
		for (int i=0; i<valueArray.length; i++) arrowMap.put("arrow"+i, valueArray[i]);
		int row = db.update("volleytable", arrowMap, "id="+archer.getId()+" AND run="+heatIndex+" AND volley="+volleyIndex, null);
		if (row<0) {
			Log.e("DB", "Cannot update volley for "+archer.getName());
		}
		db.close();
	}
	
	public void setArcherList(List<Archer> archerList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM `archertable`");
		db.execSQL("DELETE FROM `volleytable`");
		ContentValues map = new ContentValues(5);
		for (Archer archer:archerList) {
			map.put("id", archer.getId());
			map.put("license", archer.getLicense());
			map.put("category", archer.getCategory());
			map.put("name", archer.getName());
			map.put("letter", ""+(int)archer.getLetter());
			if (archer.isTrispot()) map.put("trispot", true);
			else map.put("trispot", false);
			db.insert("archertable", null, map); 
			// now insert points
			List<Heat> runList = archer.getHeatList(); 
			for (int heatIndex=0; heatIndex<runList.size(); heatIndex++) {
				List<Volley> volleyList = runList.get(heatIndex).getVolleyList();
				for (int volleyIndex=0; volleyIndex<volleyList.size(); volleyIndex++) {
					Volley volley = volleyList.get(volleyIndex);
					ContentValues arrowMap = new ContentValues(10);
					arrowMap.put("id", archer.getId());
					arrowMap.put("run", heatIndex);
					arrowMap.put("volley", volleyIndex);
					int valueArray[] = volley.getArrowArray();
					for (int i=0; i<valueArray.length; i++) arrowMap.put("arrow"+i, valueArray[i]);
					long rowId = db.insert("volleytable", null, arrowMap);
					if (rowId<0) {
						Log.e("DB", "Cannot insert volley for "+archer.getName());
					}
				}
			}
			
		}
		
		db.close();
		
	}
	
// create table `archertable` (`id` INTEGER, `license` char(10), `category` char(8), `name` char(100), `letter` INTEGER, `trispot` INTEGER	
//create table `volleytable` (`index` INTEGER PRIMARY KEY AUTOINCREMENT, `id` INTEGER, `run` INTEGER, `volley` INTEGER, 
	//`arrow0` INTEGER, `arrow1` INTEGER, `arrow2` INTEGER, `arrow3` INTEGER, `arrow4` INTEGER, `arrow5` INTEGER
	
	public List<Archer> getArcherList() { 
		List<Archer> archerList = new ArrayList<Archer>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query("archertable", null, null, null, null, null, null); 
		while (cursor.moveToNext()) {
			
			int id = cursor.getInt(0);
			String license = cursor.getString(1);
			String categ = cursor.getString(2);
			String name = cursor.getString(3);
			char letter = (char)(cursor.getInt(4));
			int trispotVal = cursor.getInt(5);
			boolean trispot = false;
			if (trispotVal==1) trispot = true;

			archerList.add(new Archer(id, license, categ, name, letter, trispot));

		}
		cursor.close();
		
		for (Archer archer: archerList) {
			int[][][] tab = new int[4][10][6];
			for (int i=0; i<4; i++)
				for (int j=0; j<10; j++)
					for (int k=0; k<6; k++) tab[i][j][k] = -1;
			Cursor cur = db.query("volleytable", null, "id="+archer.getId(), null, null, null, null);
			while (cur.moveToNext()) {
				int heatIndex = cur.getInt(2);
				int volleyIndex = cur.getInt(3);
				for (int index=0; index<6; index++) tab[heatIndex][volleyIndex][index]=cur.getInt(4+index);	
			}
			cur.close();
			for (int heatIndex=0; heatIndex<4; heatIndex++) {
				for (int volleyIndex=0; volleyIndex<10; volleyIndex++) {
					Volley volley = new Volley(tab[heatIndex][volleyIndex]);
					if (volley.getArrowList().size()>0) {
						Heat heat = archer.getHeatList().get(heatIndex);
						heat.getVolleyList().add(volley);
					}
				}
			}
		}
		db.close();
		return archerList;
		
	}
	
}
