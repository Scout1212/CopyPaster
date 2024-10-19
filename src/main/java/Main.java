import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Main implements NativeKeyListener, NativeMouseListener {
    private static boolean doing;
    private ArrayList<Integer> combo;

    private Main(){
        combo = new ArrayList<>();
        doing = false;
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
            GlobalScreen.addNativeMouseListener(this);
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String [] args){
        Main instance = new Main();
        String input = "";
        while(!doing) {
            if(input.isEmpty()) {
                input = JOptionPane.showInputDialog(null, "input text");
            }
            else
                instance.checkInput(input);
        }


    }

    private static ArrayList<String> sentanceSegmenter(String s){
        ArrayList<String> segments = new ArrayList<>();

        while(s.indexOf('.') != -1){
            String temp = s.substring(0, s.indexOf('.') + 1);
            s = s.substring(s.indexOf('.') + 1);
            segments.add(temp);
        }

        return segments;
    }



    private static void type(ArrayList<String> s){
        Robot r = null;
        try {
            r = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < s.size(); i++) {
            try {
                Thread.sleep(10000 + (int)(Math.random() * 2000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            String sentence = s.get(i);
            for (int j = 0; j < sentence.length(); j++) {
                char c = sentence.charAt(j);
                int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);

                if (c == '\\') {
                    r.keyPress(KeyEvent.VK_ENTER);
                    r.keyPress(KeyEvent.VK_TAB);
                    try {
                        Thread.sleep(1);//60*1000*5
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    r.keyPress(keyCode);
                    r.keyRelease(keyCode);
                }

                try {
                    Thread.sleep(100 + (int)(Math.random() * 200));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        if(!combo.contains(e.getKeyCode())) {
            combo.add(e.getKeyCode());
        }
    }


    public void nativeKeyReleased(NativeKeyEvent e) {
        combo.remove(Integer.valueOf(e.getKeyCode()));
    }

    public void checkInput(String input){
        System.out.print("");
        if(combo.contains(NativeKeyEvent.VC_META) && combo.contains(NativeKeyEvent.VC_X)) {
            JOptionPane.showMessageDialog(null, "Success");
            doing = true;
            type(sentanceSegmenter(input));
            doing = false;
        }

        //todo make it so that the program can be exited with bind

        //maybe editable or force wait time
    }
}
