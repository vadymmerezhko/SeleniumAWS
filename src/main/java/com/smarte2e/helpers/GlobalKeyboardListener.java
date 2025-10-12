package com.smarte2e.helpers;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalKeyboardListener implements NativeKeyListener {

    public static AtomicBoolean ctrlKeyPressed = new AtomicBoolean(false);
    public static AtomicBoolean escKeyPressed = new AtomicBoolean(false);
    public static AtomicInteger keyTypedChar = new AtomicInteger();


    public GlobalKeyboardListener() {
        ctrlKeyPressed.set(false);
        escKeyPressed.set(false);
        keyTypedChar.set(0);
    }

    public void nativeKeyPressed(NativeKeyEvent e) {

        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            escKeyPressed.set(true);
        }

        if (e.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
            ctrlKeyPressed.set(true);
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {

        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            escKeyPressed.set(false);
        }

        if (e.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
            ctrlKeyPressed.set(false);
        }
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
        keyTypedChar.set(e.getKeyChar());
    }

    public void initialize() {

        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
        }

        GlobalScreen.addNativeKeyListener(new GlobalKeyboardListener());
    }
}