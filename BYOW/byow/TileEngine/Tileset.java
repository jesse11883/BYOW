package byow.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('⚇', Color.magenta, Color.pink, "you");
    public static final TETile WALL = new TETile('\u20C0', new Color(216, 128, 128), Color.darkGray,
            "wall");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile FLOWER = new TETile(' ', Color.magenta, Color.pink, "floor");
    public static final TETile LOCKED_DOOR = new TETile('█', new Color(216, 128, 128), Color.darkGray,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢',  Color.magenta,Color.pink,
            "unlocked door");
    public static final TETile LR = new TETile('≈', Color.magenta, Color.pink, "path");

    public static final TETile CROWN = new TETile('▲', Color.gray, Color.black, "crown");
    public static final TETile TREE = new TETile('♠', Color.magenta, Color.pink,"key");
}


