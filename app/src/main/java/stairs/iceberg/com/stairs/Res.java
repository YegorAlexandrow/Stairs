package stairs.iceberg.com.stairs;

public class Res {

    public static int getId(String name) {
        for (int i=0; i<names.length; i++) if(names[i].equals(name)) return i;

        return 0;
    }

    public static String getItemId(int id) {
        return names[getId(names[id] + "_item")];
    }

    public static String str(int id) {
        return id == -1 ? "free" : names[id];
    }

    public static int getCount() {
        return names.length;
    }

    public static int floor = 0;
    public static int stair  = 1;
    public static int exit = 2;
    public static int exit_item = 3;
    public static int cloud2 = 4;
    public static int wall_r = 5;
    public static int wall_l = 6;
    public static int wall_rb = 7;
    public static int wall_lb = 8;
    public static int wall_sr = 9;
    public static int wall_sl = 10;
    public static int bg_r = 11;
    public static int bg_l = 12;
    public static int bg0_r = 13;
    public static int bg0_l = 14;
    public static int bg1_r = 15;
    public static int bg1_l = 16;
    public static int bg2_r = 17;
    public static int bg2_l = 18;
    public static int bridge_l0 = 19;
    public static int bridge_l1 = 20;
    public static int gray_bridge_l2 = 21;
    public static int gray_bridge_l3 = 22;
    public static int bridge_r0 = 23;
    public static int bridge_r1 = 24;
    public static int gray_bridge_r2 = 25;
    public static int gray_bridge_r3 = 26;
    public static int bridge = 27;
    public static int pointer = 28;
    public static int bg = 29;
    public static int bgm = 30;
    public static int big = 31;
    public static int little = 32;
    public static int parapet_r = 33;
    public static int parapet_l = 34;
    public static int parapet = 35;
    public static int grass_r1 = 36;
    public static int grass_l1 = 37;
    public static int grass_mid1 = 38;
    public static int grass_deep1 = 39;
    public static int grass = 40;
    public static int grass1 = 41;
    public static int grass_r = 42;
    public static int grass_l = 43;
    public static int grass_mid = 44;
    public static int grass_mid_r = 45;
    public static int grass_mid_l = 46;
    public static int grass_deep = 47;
    public static int grass_deep_r = 48;
    public static int grass_deep_l = 49;
    public static int grass_deep2 = 50;
    public static int hero = 51;
    public static int ghost = 52;
    public static int anvil = 53;
    public static int fish = 54;
    public static int decor = 55;
    public static int portal = 56;
    public static int flag = 57;
    public static int key_yellow = 58;
    public static int key_green = 59;
    public static int key_orange = 60;
    public static int key_blue = 61;
    public static int crown = 62;
    public static int star = 63;
    public static int crystal = 64;
    public static int sword = 65;
    public static int egg = 66;
    public static int coin = 67;
    public static int potion = 68;
    public static int bone = 69;
    public static int crown_item = 70;
    public static int star_item = 71;
    public static int crystal_item = 72;
    public static int sword_item = 73;
    public static int egg_item = 74;
    public static int coin_item = 75;
    public static int potion_item = 76;
    public static int bone_item = 77;
    public static int key_yellow_item = 78;
    public static int key_green_item = 79;
    public static int key_orange_item = 80;
    public static int key_blue_item = 81;
    public static int chest_yellow = 82;
    public static int chest_green = 83;
    public static int chest_orange = 84;
    public static int chest_blue = 85;
    public static int lock_yellow = 86;
    public static int lock_green = 87;
    public static int lock_orange = 88;
    public static int lock_blue = 89;
    public static int parapet_lite_r = 90;
    public static int parapet_lite_l = 91;
    public static int roof0_l = 92;
    public static int roof1_l = 93;
    public static int roof2_l = 94;
    public static int roof3_l = 95;
    public static int gray_roof4_l = 96;
    public static int roof_m = 97;
    public static int roof0_r = 98;
    public static int roof1_r = 99;
    public static int roof2_r = 100;
    public static int roof3_r = 101;
    public static int gray_roof4_r = 102;
    public static int water_bl = 103;
    public static int water_br = 104;
    public static int water_bm = 105;
    public static int water_tl = 106;
    public static int water_tr = 107;
    public static int water_tm = 108;
    public static int balloon = 109;
    public static int questman = 110;
    public static int questman_mage = 111;
    public static int questman_merchant = 112;
    public static int questman_goblin = 113;
    public static int questman_mroom = 114;
    public static int questman_snail = 115;
    public static int questman_smurf = 116;
    public static int clock = 117;
    public static int clock_dot = 118;
    public static int clock_numbers = 119;
    public static int clock_pointer_long = 120;
    public static int clock_pointer_short = 121;
    public static int thread = 122;
    public static int swipes = 123;
    public static int chest_green_item = 124;
    public static int question = 125;
    public static int question_item = 126;
    public static int lift = 127;
    public static int lift_ver = 128;

    private static String[] names = {
                "&_floor",
                "&_stair",
                "exit",
                "exit_item",
                "cloud2",
                "&_wall_r",
                "&_wall_l",
                "&_wall_rb",
                "&_wall_lb",
                "&_wall_sr",
                "&_wall_sl",
                "&_bg_r",
                "&_bg_l",
                "&_bg0_r",
                "&_bg0_l",
                "&_bg1_r",
                "&_bg1_l",
                "&_bg2_r",
                "&_bg2_l",
                "&_bridge_l0",
                "&_bridge_l1",
                "gray_bridge_l2",
                "gray_bridge_l3",
                "&_bridge_r0",
                "&_bridge_r1",
                "gray_bridge_r2",
                "gray_bridge_r3",
                "&_bridge",
                "pointer",
                "&_bg",
                "&_bgm",
                "&_big",
                "&_little",
                "&_parapet_r",
                "&_parapet_l",
                "&_parapet",
                "grass_r",
                "grass_l",
                "grass_mid",
                "grass_deep",
                "grass",
                "&_grass",
                "&_grass_r",
                "&_grass_l",
                "&_grass_mid",
                "&_grass_mid_r",
                "&_grass_mid_l",
                "&_grass_deep",
                "&_grass_deep_r",
                "&_grass_deep_l",
                "&_grass_deep2",
                "hero",
                "ghost",
                "anvil",
                "fish",
                "&_decor",
                "&_portal",
                "&_flag",
                "key_yellow",
                "key_green",
                "key_orange",
                "key_blue",
                "crown",
                "star",
                "crystal",
                "sword",
                "egg",
                "coin",
                "potion",
                "bone",
                "crown_item",
                "star_item",
                "crystal_item",
                "sword_item",
                "egg_item",
                "coin_item",
                "potion_item",
                "bone_item",
                "key_yellow_item",
                "key_green_item",
                "key_orange_item",
                "key_blue_item",
                "chest_yellow",
                "chest_green",
                "chest_orange",
                "chest_blue",
                "lock_yellow",
                "lock_green",
                "lock_orange",
                "lock_blue",
                "&_parapet_lite_r",
                "&_parapet_lite_l",
                "&_roof0_l",
                "&_roof1_l",
                "&_roof2_l",
                "&_roof3_l",
                "gray_roof4_l",
                "&_roof_m",
                "&_roof0_r",
                "&_roof1_r",
                "&_roof2_r",
                "&_roof3_r",
                "gray_roof4_r",
                "&_water_bl",
                "&_water_br",
                "&_water_bm",
                "&_water_tl",
                "&_water_tr",
                "&_water_tm",
                "balloon",
                "questman",
                "questman_mage",
                "questman_merchant",
                "questman_goblin",
                "questman_mroom",
                "questman_snail",
                "questman_smurf",
                "clock",
                "clock_dot",
                "clock_numbers",
                "clock_pointer_long",
                "clock_pointer_short",
                "thread",
                "swipes",
                "chest_green_item",
                "question",
                "question_item",
                "lift",
                "lift_ver"
    };
}