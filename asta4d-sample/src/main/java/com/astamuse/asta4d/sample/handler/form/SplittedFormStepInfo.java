package com.astamuse.asta4d.sample.handler.form;

public interface SplittedFormStepInfo {
    default String inputStep1Name() {
        return "input-1";
    }

    default String inputStep2Name() {
        return "input-2";
    }
}
