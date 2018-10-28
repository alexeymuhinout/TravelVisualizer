package com.rustedbrain.diploma.travelvisualizer;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RegistrationFormKeyListener implements View.OnKeyListener {

    private final int[] secretKeyCombination;
    private final LinkedList<Integer> secretQueue;
    private final List<SuccessListener> successListeners = new ArrayList<>();

    public RegistrationFormKeyListener(int[] secretKeyCombination, LinkedList<Integer> secretQueue, List<SuccessListener> successListeners) {
        this.secretKeyCombination = secretKeyCombination;
        this.secretQueue = secretQueue;
        this.successListeners.addAll(successListeners);
    }

    public RegistrationFormKeyListener(int[] secretKeyCombination, LinkedList<Integer> secretQueue, SuccessListener successListener) {
        this.secretKeyCombination = secretKeyCombination;
        this.secretQueue = secretQueue;
        this.successListeners.add(successListener);
    }

    public RegistrationFormKeyListener(int[] secretKeyCombination, LinkedList<Integer> secretQueue) {
        this.secretKeyCombination = secretKeyCombination;
        this.secretQueue = secretQueue;
    }

    public boolean removeListener(SuccessListener successListener) {
        return successListeners.remove(successListener);
    }

    public boolean addListener(SuccessListener successListener) {
        return successListeners.add(successListener);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.i(LoginActivity.class.getName(), "Login key event key code: " + String.valueOf(event.getKeyCode()));
        if (secretQueue.size() < secretKeyCombination.length) {
            secretQueue.addLast(event.getKeyCode());
        } else {
            secretQueue.removeFirst();
            secretQueue.addLast(event.getKeyCode());
        }
        boolean successCombination = true;
        for (int idx = 0; idx < secretKeyCombination.length; idx++) {
            try {
                int inputtedKey = secretQueue.get(idx);
                if (inputtedKey != secretKeyCombination[idx]) {
                    successCombination = false;
                    break;
                }
            } catch (IndexOutOfBoundsException ex) {
                successCombination = false;
            }
        }

        if (successCombination) {
            for (SuccessListener listener : successListeners) {
                listener.combinationSuccess();
            }
            }
        return true;
    }

    public interface SuccessListener {
        void combinationSuccess();
    }
}