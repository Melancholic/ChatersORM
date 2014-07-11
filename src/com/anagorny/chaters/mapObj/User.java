package com.anagorny.chaters.mapObj;

import com.anagorny.chaters.user.UserInstance;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sosnov
 * Date: 04.01.14
 * import org.hibernate.annotations.Table;
 * import java.util.HashSet;
 * Time: 19:12
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "Users")
public class User implements Comparable {
    @Id
    @Column(name = "user_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    @Column(name = "user_name", unique = false, nullable = true, length = 20)
    private String user_name;

    @Column(name = "user_surname", unique = false, nullable = true, length = 20)
    private String user_surname;

    @Column(name = "user_nick", unique = true, nullable = false, length = 20)
    private String user_nick;

    @Column(name = "user_descript", unique = false, nullable = true, length = 2000)
    private String user_descript;

    @Column(name = "user_pass", unique = false, nullable = false, length = 20)
    private String pass;
    //fetch = FetchType.LAZY
    @ManyToMany(fetch = FetchType.EAGER/*, cascade = CascadeType.ALL*/)
    @JoinTable(name = "user_room", joinColumns = {
            @JoinColumn(name = "user_id", nullable = false, updatable = true)},
            inverseJoinColumns = {@JoinColumn(name = "room_id",
                    nullable = false, updatable = false)})
    private Set<Room> rooms = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "usr_id")
    private Set<Messege> messeges = new HashSet<>();

    //  private ClientThread sock;

    public User(String us_nick, String us_pass/*, ClientThread ct*/) {
        //this.user_id = this.hashCode();
        this.user_nick = us_nick;
        this.pass = us_pass;
        //  this.sock = ct;
    }

    public User() {
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User){
           return (user_id == ((User) o).user_id);
        }else if(o instanceof  UserInstance){
            return (user_id == ((UserInstance) o).getUser().getUser_id());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = user_id != null ? user_id.hashCode() : 0;
        return result;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof User) {
            Long i = ((User) o).getUser_id();
            return (i < user_id ? -1 : (i == user_id ? 0 : 1));
        } else if (o instanceof UserInstance) {
            if (((UserInstance) o).getUser().getUser_id() == user_id) {
                return 0;
            } else if (((UserInstance) o).getUser().getUser_id() < user_id) {
                return 1;
            } else {
                return -1;
            }
        }
        return 1;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_surname() {
        return user_surname;
    }

    public String getUser_nick() {
        return user_nick;
    }

    public String getUser_descript() {
        return user_descript;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_surname(String user_surname) {
        this.user_surname = user_surname;
    }

    public void setUser_nick(String user_nick) {
        this.user_nick = user_nick;
    }

    public void setUser_descript(String user_descript) {
        this.user_descript = user_descript;
    }

    public String getPass() {
        return pass;
    }


    public void setPass(String pass) {
        this.pass = pass;
    }


    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }


    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }


    public Set<Messege> getMesseges() {
        return messeges;
    }

    public void setMesseges(Set<Messege> messeges) {
        this.messeges = messeges;
    }

    public Set<Room> getRooms() {
        return rooms;
    }

}
