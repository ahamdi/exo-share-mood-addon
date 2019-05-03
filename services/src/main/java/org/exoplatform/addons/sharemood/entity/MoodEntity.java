package org.exoplatform.addons.sharemood.entity;


import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.util.Calendar;

@ExoEntity
@Entity
@Table(name = "ADDON_MOOD_ENTITY")
@NamedQueries({
    @NamedQuery(
      name = "MoodEntity.findByUsernameAndDate",
      query = "SELECT m FROM MoodEntity m WHERE m.userName= :userName AND m.when = :when"),
    @NamedQuery(
      name = "MoodEntity.findByUsername",
      query = "SELECT m FROM MoodEntity m WHERE m.userName= :userName  ORDER BY m.when ASC"),
    @NamedQuery(
      name = "MoodEntity.findByUsernameAndMood",
      query = "SELECT m FROM MoodEntity m WHERE m.userName= :userName AND m.selectedMood= :mood ORDER BY m.when ASC"),
    @NamedQuery(
      name = "MoodEntity.countAllMoodsByUser",
      query = "SELECT count(m) FROM MoodEntity m WHERE m.userName= :userName AND m.when >= :since"),
    @NamedQuery(
      name = "MoodEntity.findAllByUserAndSince",
      query = "SELECT m FROM MoodEntity m WHERE m.userName= :userName AND m.when >= :since ORDER BY m.when ASC"),
    @NamedQuery(
      name = "MoodEntity.findByUsernameAndMoodAndSince",
      query = "SELECT m FROM MoodEntity m WHERE m.userName= :userName AND m.selectedMood= :mood AND m.when >= :since  ORDER BY m.when ASC")
})
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

    public enum Mood {CRYING,SAD,SPEECHLESS,SMILE,FLAUGH}
}
