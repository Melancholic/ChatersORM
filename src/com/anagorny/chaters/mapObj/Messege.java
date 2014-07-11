package com.anagorny.chaters.mapObj;

import com.anagorny.chaters.DAO.Connector;
import com.anagorny.chaters.messege.MSG;
import com.anagorny.chaters.user.UserInstance;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by sosnov on 10.04.14.
 */
@Entity
@Table(name = "Messeges")
public class Messege implements Comparable {
    private Long msg_id;
    private User usr_id;
    private Room room_id;
    private String msg_txt ;
    private Date msg_date;

    public Messege() {
    }

    public Messege(User usr_id, Room room_id, String msg_txt) {
        this.usr_id = usr_id;
        this.room_id = room_id;
        this.msg_txt = msg_txt;
        this.msg_date=new Date();
    }

    public Messege(JSONObject json) throws SQLException {
        String unick= (String)json.get(MSG.USR_NICK);
        this.usr_id=   Connector.getInstance().getUserDAO().getUsrFromNick(unick);
        Long rid = (Long)json.get(MSG.ROOM_ID);
        this.msg_txt = (String)json.get(MSG.USR_MSG.TEXT);
        this.room_id= Connector.getInstance().getRoomDAO().getObjById(rid);
        this.msg_date=new Date();
    }
    @Id
    @Column(name = "msg_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(Long msg_id) {
        this.msg_id = msg_id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    public User getUsr_id() {
        return usr_id;
    }
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    public Room getRoom_id() {
        return room_id;
    }
    @Column(name = "msg_txt", unique = false, nullable =false,length = 8000)
    public String getMsg_txt() {
        return msg_txt;
    }
    @Temporal(TemporalType.DATE)
    @Column(name = "msg_date", unique = false, nullable =true)
    public Date getMsg_date() {
        return msg_date;
    }

    public void setUsr_id(User usr_id) {
        this.usr_id = usr_id;
    }

    public void setRoom_id(Room room_id) {
        this.room_id = room_id;
    }

    public void setMsg_txt(String msg_txt) {
        this.msg_txt = msg_txt;
    }

    public void setMsg_date(Date msg_date) {
        this.msg_date = msg_date;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}

