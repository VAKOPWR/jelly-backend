package com.vako.application.user.model;

@FunctionalInterface
public interface StealthChoiceUpdater {
    int update(Long userOneId, Long userTwoId, StealthChoice stealthChoice);
}