package com.traps.trapta;

import java.util.ArrayList;

public class StaticParam {
	

	public static int screenWidth = 600;
	public static int screenHeight = 1024;
	public static boolean speakerEnabled = true;
	public static int heatVolleyCount = 10;
	public static int roundCount = 2;
	public static int matchVolleyMax = 5; 
	public static boolean x10 = false;
	public static int arrowCount = 3;
	public static int targetId = 0;
	public static boolean standAloneMode = false;
	public static TargetInfo[] standAloneTargetInfo;
	public static ArrayList<Archer>[] standAloneArcherList;
	public static boolean colorInverted = false; 
	
	static {
	
		standAloneTargetInfo = new TargetInfo[3];
        standAloneTargetInfo[0] = new TargetInfo(1);
        standAloneTargetInfo[0].addArcher('A', "ARCHER A", false);
        standAloneTargetInfo[0].addArcher('B', "ARCHER B", false);
        standAloneTargetInfo[0].addArcher('C', "ARCHER C", false);
        standAloneTargetInfo[0].addArcher('D', "ARCHER D", false);
        standAloneTargetInfo[1] = new TargetInfo(2);
        standAloneTargetInfo[1].addArcher('A', "ARCHER A", true);
        standAloneTargetInfo[1].addArcher('B', "ARCHER B", true);
        standAloneTargetInfo[1].addArcher('C', "ARCHER C", true);
        standAloneTargetInfo[1].addArcher('D', "ARCHER D", true);
        standAloneTargetInfo[2] = new TargetInfo(3);
        standAloneTargetInfo[2].addArcher('A', "WINNIE L'OURSON", false);
        standAloneTargetInfo[2].addArcher('B', "COCO LAPIN", false);
        standAloneTargetInfo[2].addArcher('C', "TIGROU", true);

        standAloneArcherList = new ArrayList[3];
        standAloneArcherList[0] = new ArrayList<>();
        standAloneArcherList[0].add(new Archer(-1, "123456H", "SHCL", "ARCHER A", 'A', false));
        standAloneArcherList[0].add(new Archer(-2, "789951L", "SFCL", "ARCHER B", 'B',false));
        standAloneArcherList[0].add(new Archer(-3, "496378J", "SHCL", "ARCHER C", 'C',false));
        standAloneArcherList[0].add(new Archer(-4, "713645Y", "SHCL", "ARCHER D", 'D',false));
        standAloneArcherList[1] = new ArrayList<>();
        standAloneArcherList[1].add(new Archer(-5, "653214Q", "SHCL", "ARCHER A", 'A',true));
        standAloneArcherList[1].add(new Archer(-6, "894638S", "SHCL", "ARCHER B", 'B',true));
        standAloneArcherList[1].add(new Archer(-7, "951674G", "SHCL", "ARCHER C", 'C',true));
        standAloneArcherList[1].add(new Archer(-8, "951674G", "SHCL", "ARCHER D", 'D',true));
        standAloneArcherList[2] = new ArrayList<>();
        standAloneArcherList[2].add(new Archer(-9, "145789Y", "SHCL", "WINNIE L'OURSON", 'A',false));
        standAloneArcherList[2].add(new Archer(-10, "145790Y", "SHCL", "COCO LAPIN", 'B',false));
        standAloneArcherList[2].add(new Archer(-11, "145791Y", "SHCL", "TIGROU", 'C',true));

		
	}

}
  