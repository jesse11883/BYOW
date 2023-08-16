package byow.Core;

import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.In;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 75;
    public static final int HEIGHT = 45;
    GenWorld genWorld = null;
    Guy mainGuy = null;
    String[] savedGame;
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        Menu x = new Menu(this.WIDTH, this.HEIGHT);
        x.drawMenu();
        x.getMenuCommand(this);
        ActionHandler y = new ActionHandler(genWorld, this);
        y.actionDispatcher();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        StringBuilder seed = new StringBuilder();
        int i = 0;
        if (Character.toUpperCase(input.charAt(0)) == 'L') {
            i++; //pass the L
            loadGame();
            String moves = input.substring(i);
            if (makeMove(moves)) {
                return getReturn();
            }
        } else {
            while (Character.toUpperCase(input.charAt(i)) != 'S') {
                seed.append(input.charAt(i));
                i++;
            }
            seed.append(input.charAt(i));
            this.genWorld = worldCreatorGivenSeed(seed.toString());

            i = i + 1; //pass the S
            String moves = input.substring(i);
            if (makeMove(moves)) {
                return getReturn();
            }

        }
        ActionHandler y = new ActionHandler(genWorld, this);
        y.actionDispatcher();
        return getReturn();
    }
    public boolean makeMove(String input) {
        int i = 0;
        while (i != input.length()) {
            if (input.charAt(i) == ':' && Character.toUpperCase(input.charAt(i + 1)) == 'Q') {
                this.saveGame();
                return true; //true means we hit :q and need to stop
            }
            this.mainGuy.move(input.charAt(i));
            i++;
        }
        return false;
    }
    public TETile[][] getReturn() {
        TETile[][] displayPanel = TETile.copyOf(this.genWorld.getTiles());
        displayPanel[mainGuy.curr.locX][mainGuy.curr.locY] = mainGuy.currTile;
        //System.out.println("hi");
        return displayPanel;
    }
    public GenWorld worldCreatorGivenSeed(String input) {
        StringBuilder seed = new StringBuilder();
        long finalSeed;
        int end = input.length() - 1;
        int i = 0;
        if ((input.length() > 2) && Character.toUpperCase(input.charAt(end)) == 'S') {
            if (Character.toUpperCase(input.charAt(0)) == 'N') {
                i = 1;
            } else {
                i = 0;
            }
            while (Character.toUpperCase(input.charAt(i)) != 'S') {
                seed.append(input.charAt(i));
                i++;
            }
        } else {
            System.out.println("incorrect input");
            System.exit(0);
            return null;
        }
        finalSeed = Long.valueOf(seed.toString());
        this.genWorld = new GenWorld(WIDTH, HEIGHT, finalSeed);
        genWorld.start();
        this.mainGuy = createMainGuy();
        return genWorld;
    }
    public void saveGame() {
        try {
            String fileName = "BYOW.txt";
            String seed = genWorld.getSeed().toString();
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(seed + "\n");
            writer.write(mainGuy.curr.locX + "\n");
            writer.write(Integer.toString(mainGuy.curr.locY));
            ArrayList delkeys = genWorld.deleted_Keys;
            writer.write("\n" + genWorld.keys);
            writer.write("\n" + this.genWorld.num_Of_Unlocked_Doors);
            for (Object j: this.genWorld.unlocked_Doors) {
                writer.write("\n" + j);
            }
            for (Object i: delkeys) {
                writer.write("\n" + i);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println(e);
        }

    }
    public String[] readGame() {
        In in = new In("BYOW.txt");
        this.savedGame = in.readAllLines();
        if (this.savedGame == null) {
            return null;
        } else {
            return this.savedGame;
        }
    }
    public void loadGame() {
        String[] s = readGame();
        this.genWorld = new GenWorld(WIDTH, HEIGHT, Long.parseLong(s[0]));
        genWorld.start();
        this.mainGuy = createMainGuy(Integer.parseInt(s[1]), Integer.parseInt(s[2]));
        this.genWorld.keys = Integer.parseInt(s[3]);
        this.genWorld.num_Of_Unlocked_Doors = Integer.parseInt(s[4]);
        for (int j = 5; j < 5 + this.genWorld.num_Of_Unlocked_Doors; j++) {
            this.genWorld.changeToUnlockedDoor(Integer.parseInt(s[j]),Integer.parseInt(s[j+1]));
        }
        for (int i = 5 + this.genWorld.num_Of_Unlocked_Doors * 2; i < s.length; i += 2) {
            this.genWorld.changeToFloor(Integer.parseInt(s[i]),Integer.parseInt(s[i+1]));
        }
    }
    public Guy createMainGuy() {
        int startx = 0;
        int starty = 0;
        boolean stop = false;
        for (int i = 0; i < genWorld.width && !stop; i++) {
            for (int j = 0; j < genWorld.height && !stop; j++) {
                if (genWorld.getTileName(genWorld.getTiles(), i, j).equals("floor")) {
                    startx = i;
                    starty = j;
                    stop = true;
                }
            }
        }
        this.mainGuy = createMainGuy(startx, starty);
        return mainGuy;
    }
    public Guy createMainGuy(int x, int y) {
        this.mainGuy = new Guy(x, y, genWorld);
        return mainGuy;
    }
}
