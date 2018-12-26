package org.exoplatform.addons.sharemood.services;

import org.exoplatform.addons.sharemood.entity.MoodEntity;

import java.util.Calendar;

public class MoodDTO {
    private String username;
    private Calendar when;
    private MoodEntity.Mood mood;

    public MoodDTO(String userName, MoodEntity.Mood selectedMood, Calendar when) {
        this.username = userName;
        this.mood = selectedMood;
        this.when = when;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Calendar getWhen() {
        return when;
    }

    public void setWhen(Calendar when) {
        this.when = when;
    }

    public MoodEntity.Mood getMood() {
        return mood;
    }

    public void setMood(MoodEntity.Mood mood) {
        this.mood = mood;
    }
}
