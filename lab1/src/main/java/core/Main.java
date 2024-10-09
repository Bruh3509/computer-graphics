package core;

import javax.swing.*;

public class Main {
    static class Converter extends JFrame {
        Converter() {
            JColorChooser chooser = new JColorChooser();
            add(chooser);
            setSize(700, 500);
            setVisible(true);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }
    }
    public static void main(String[] args) {
        new Converter();
    }
}
