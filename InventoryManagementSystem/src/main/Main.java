package main;

import view.LoginForm;

public class Main {
    public static void main(String[] args) {
        try {
            // Optional: set modern look and feel
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start with Login Form
        new LoginForm().setVisible(true);
    }
}
