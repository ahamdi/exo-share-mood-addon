package org.exoplatform.addons.sharemood.entity;


import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.util.Calendar;

@ExoEntity
@Entity
@Table(name = "ADDON_MOOD_ENTITY")
@NamedQueries({@NamedQuery(
        name = "MoodEntity.findByUsernameAndDate",
        query = "SELECT m FROM MoodEntity m WHERE m.userName= :userName AND m.when = :when"),
        @NamedQuery(
                name = "MoodEntity.findByUsername",
                query = "SELECT m FROM MoodEntity m WHERE m.userName= :userName"),
        @NamedQuery(
                name = "MoodEntity.findByUsernameAndMood",
                query = "SELECT m FROM MoodEntity m WHERE m.userName= :userName AND selectedMood= :mood"
        )})
public class MoodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name="USERNAME")
    private String userName;

    @Column(name = "MOOD_SELECTED")
    private Mood selectedMood;

    @Temporal(TemporalType.DATE)
    @Column(name = "UPDATED")
    private Calendar when;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Mood getSelectedMood() {
        return selectedMood;
    }

    public void setSelectedMood(Mood selectedMood) {
        this.selectedMood = selectedMood;
    }

    public Calendar getWhen() {
        return when;
    }

    public void setWhen(Calendar when) {
        this.when = when;
    }

    public enum Mood {FLAUGH,SMILE,SPEECHLESS,SAD,CRYING}
}
