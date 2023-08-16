package byow.Core;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;

public class Menu {
    int width;
    int height;
    public boolean is_chinese = false;
    ArrayList<Character> valid_inputs = new ArrayList<>(Arrays.asList('N', 'n', 'T', 't', 'Q', 'q', 'L', 'l'));

    public Menu (int width, int height) {
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }
    public void drawMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, (this.width + this.height) / 3);
        Font fontBigger = new Font("Monaco", Font.BOLD, (this.width + this.height) / 2);
        Font small = new Font("Monaco", Font.BOLD, (this.width + this.height) / 10);

        StdDraw.setFont(fontBig);
        //make the + 2 a ratio of the font size somehow
        StdDraw.text(this.width / 2, this.height / 3 + 3, "New Game (N)");
        StdDraw.text(this.width / 2, this.height / 3, "Load Game (L)");
        StdDraw.text(this.width / 2, this.height / 3 - 3, "Quit (Q)");
        StdDraw.text(this.width / 2, this.height / 3 - 6, "Translate to Chinese (T)");
        StdDraw.setFont(small);
        StdDraw.text(this.width / 2, this.height / 3 - 10, "Toggles on/off the light! (P)");
        StdDraw.text(this.width / 2, this.height / 3 - 11, "to save and quit! (:Q)");
        StdDraw.text(this.width / 2, this.height / 3 - 12, "Collect Keys! Unlock Door! Win Game!");




        StdDraw.setFont(fontBigger);
        StdDraw.text(this.width / 2, this.height * 2 / 3, "CS61B: The Game");

        StdDraw.show();
    }
    public void drawChineseMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, (this.width + this.height) / 3);
        Font fontBigger = new Font("Monaco", Font.BOLD, (this.width + this.height) / 2);
        Font small = new Font("Monaco", Font.BOLD, (this.width + this.height) / 10);

        StdDraw.setFont(fontBig);
        //make the + 2 a ratio of the font size somehow
        StdDraw.text(this.width / 2, this.height / 3 + 3, "新游戏 (N)");
        StdDraw.text(this.width / 2, this.height / 3, "加载游戏 (L)");
        StdDraw.text(this.width / 2, this.height / 3 - 3, "退出 (Q)");
        StdDraw.text(this.width / 2, this.height / 3 - 6, "翻译成英文 (T)");

        StdDraw.setFont(small);
        StdDraw.text(this.width / 2, this.height / 3 - 10, "开灯, 关灯 (P)");
        StdDraw.text(this.width / 2, this.height / 3 - 11, "保存并退出！(:Q)");
        StdDraw.text(this.width / 2, this.height / 3 - 12, "收集钥匙！ 打开门！ 赢得比赛！");



        StdDraw.setFont(fontBigger);
        StdDraw.text(this.width / 2, this.height * 2 / 3, "CS61B: The 游戏");

        StdDraw.show();
    }
    public void drawWinScreen() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBigger = new Font("Monaco", Font.BOLD, (this.width + this.height) / 2);

        StdDraw.setFont(fontBigger);
        StdDraw.text(this.width / 2, this.height * 2 / 3, "You win!");
        Font small = new Font("Monaco", Font.BOLD, (this.width + this.height) / 10);
        StdDraw.setFont(small);
        StdDraw.text(this.width / 2, this.height / 3 + 1, "Press N to Play Again!");

        StdDraw.text(this.width / 2, this.height / 3, "Press Q to Quit");

        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                Character c = StdDraw.nextKeyTyped();
                if (c == 'q' || c == 'Q') {
                    System.exit(1);
                } else if (c == 'N'|| c == 'n') {
                    String x = askForSeedType();
                    Engine e = new Engine();
                    ActionHandler y = new ActionHandler(e.worldCreatorGivenSeed(x), e);
                    y.actionDispatcher();
                }
            }
        }
    }
    public void askForSeed(String x) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, (this.width + this.height) / 3);
        StdDraw.setFont(fontBig);
        //make the + 2 a ratio of the font size somehow
        StdDraw.text(this.width / 2, this.height / 2, "Input Seed:" + x);


        StdDraw.show();
    }
    public String askForSeedType() {
        String x = "";
        askForSeed(x);
        boolean stop = false;
        while (!stop) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                switch (c) {
                    case 's':
                    case 'S': {
                        stop = true;
                        x = x + c;
                        return x;
                    }
                    case '\b': {
                        x = removeLastChar(x);
                        askForSeed(x);
                        break;
                    }
                    default: {
                        x = x + c;
                        askForSeed(x);
                        break;
                    }
                }
            }
        }
        return x;
    }
    public static String removeLastChar(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 1));
    }
    public static void main(String[] args) {

    }
    public void getMenuCommand(Engine e) {
        char c;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                c = StdDraw.nextKeyTyped();
                if (valid_inputs.contains(c)) {
                    break;
                }
            }
        }
        if (c == 'N'|| c == 'n') {
            String x = askForSeedType();
            e.worldCreatorGivenSeed(x);
        } else if (c == 'L' || c == 'l') {
            e.loadGame();
        } else if (c == 'Q' || c == 'q') {
            System.exit(0);
        } else if (c == 'T' || c == 't') {
            if (!is_chinese) {
                this.drawChineseMenu();
            } else {
                this.drawMenu();
            }
            is_chinese = !is_chinese;
            this.getMenuCommand(e);
        }
    }
}