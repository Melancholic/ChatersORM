package com.anagorny.chaters.mapObj;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sosnov
 * Date: 04.01.14
 * Time: 19:07
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "Rooms")
public class Room implements Comparable {

    private Long room_id;
    private String room_name;
    private String room_descript;
    private Boolean room_open;
    private Set<User> users = new HashSet<>();
    private Set<Messege> messeges= new HashSet<>();
    public Room() {
    }

    public Room(String R_n, String R_d, boolean r_op) {
        this.room_name = R_n;
        this.room_descript = R_d;
        this.room_open = r_op;
    }

    @Id
    @Column(name = "room_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getRoom_id() {
        return room_id;
    }

    @Column(name = "room_name", unique = true, nullable = true, length = 20)
    public String getRoom_name() {
        return room_name;
    }

    @Column(name = "room_descript", unique = false, nullable = true, length = 1000)
    public String getRoom_descript() {
        return room_descript;
    }

    @Column(name = "room_open", unique = false, nullable = true)
    public boolean isRoom_open() {
        return room_open;
    }

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "rooms")
    public Set<User> getUsers_list() {
        return users;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "room_id")
    public Set<Messege> getMesseges() {
        return messeges;
    }

    public void setMesseges(Set<Messege> messeges) {
        this.messeges = messeges;
    }

    public void setRoom_id(long room_id) {
        this.room_id = room_id;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public void setRoom_descript(String room_descript) {
        this.room_descript = room_descript;
    }

    public void setRoom_open(boolean room_open) {
        this.room_open = room_open;
    }

    public void setUsers_list(Set<User> users_list) {
        this.users = users_list;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Room) && (room_id == ((Room) o).room_id);
    }

    @Override
    public int hashCode() {
        int result = room_id != null ? room_id.hashCode() : 0;
        return result;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof User) {
            Long i = ((User) o).getUser_id();
            return (i < room_id ? -1 : (i == room_id ? 0 : 1));
        } else {
            return 1;
        }
    }
}

