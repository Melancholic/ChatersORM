package com.anagorny.chaters.server;

import com.anagorny.chaters.DAO.Connector;
import com.anagorny.chaters.DAO.HibernateUtil;
import com.anagorny.chaters.room.MainRoom;
import com.anagorny.chaters.mapObj.Room;
import com.anagorny.chaters.mapObj.User;
import com.anagorny.chaters.user.UserInstance;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sosnov
 * Date: 04.01.14
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */
public class Server {
    private String date;
    private static HashSet<UserInstance>  UsrsInstc;
    static Room mainRoom;
    public static void main(String[] args) {
        HibernateUtil.createSessionFactory();
        UsrsInstc=new HashSet<UserInstance>();
        mainRoom=null;
        try {
            createMainRoom();
            ServerSocket Socket = new ServerSocket(Config.PORT());
            Config.PORT();
            System.err.println("Worked at " + Config.PORT() + " port ...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<User> tmpUsrList =null;
                    ArrayList<Room> tmpRoomList=null;
                  /*  while (true) {
                        try {
                        System.err.printf("\n\n\nnull%n");
                        System.err.println(getDate());
                        Calendar.getInstance();
                        System.err.println("Register user: ");

                            tmpUsrList=(ArrayList<User>)Connector.getInstance().getUserDAO().getAllObj();
                        for (User i : tmpUsrList) {
                            System.err.println("\t " + i.getUser_id() + " " + i.getUser_nick() + ":" + i.getPass());
                        }

                        System.err.println("On-line user: ");
                        for (User i : Server.GetOnLineUsers()) {
                            System.err.println("\t " + i.getUser_id() + " " + i.getUser_nick() + ":" + i.getPass());
                        }

                        System.err.println("Register room: ");
                            tmpRoomList=(ArrayList<Room>)Connector.getInstance().getRoomDAO().getAllObj();
                        for (Room i : tmpRoomList) {
                            System.err.println("\t " + i.getRoom_id() + " " + i.getRoom_name());
                        }

                        System.err.println("In room: ");
                        for (Room i : tmpRoomList) {
                            System.err.println("\t " + i.getRoom_id() + " " + i.getRoom_name() + ":");
                            ArrayList<User> themp= (ArrayList<User>) Connector.getInstance().getRoomDAO().getUsersByRoom(i);
                           for (User j : themp) {
                               if(j!=null){
                                System.err.println("\t " + j.getUser_id() + " " + j.getUser_nick() + ":" + j.getPass());
                               }
                            }
                        }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }  */
                }
            }).start();
            while (true) {
                Socket client = null;
                do {
                    System.err.println("Waiting for connection ...");
                    client = Socket.accept();
                } while (client == null);
                new ClientThread(client);
                //ClientsSockets.add(new ClientThread(client));

            }
        } catch (SocketException se) {
            System.err.println("Socket Exception while created socket!");
            se.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("IO Exception while created socket!");
            ioe.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static ArrayList<User> GetOnLineUsers() {
        ArrayList<User> res = new ArrayList<>();
        for (UserInstance i : Server.UsrsInstc) {
            if (i.getUser() != null) {
                res.add(i.getUser());
            }
        }
        return res;
    }

    public static ArrayList<String> GetUsrsName(ArrayList<User> arr) {
        ArrayList<String> res = new ArrayList<>();
        for (User i : arr) {
            res.add(i.getUser_nick());
        }
        return res;
    }
 /*
    public static void createRoom(Room nr, User usr) {
     //   Server.RoomsList.add(nr);
     //   Server.RoomsNameList.add(nr.getRoom_name());
        try {
            Connector.getInstance().getRoomDAO().addObj(nr);
           // Connector.getInstance().getUserDAO().AddRoom(usr,nr);
            usr.getRooms().add(nr) ;
            Connector.getInstance().getUserDAO().updateObj(usr);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }    */
 /*
    public static void addUsrToRoom(long roomId, long userId) {
        //Server.RoomUsrs.get(roomId).add(userId);
        if (Server.RoomUsrs.get(roomId).indexOf(userId) == -1) {
            Server.RoomUsrs.get(roomId).add(userId);
        }
    }
   */
    private static void createMainRoom() {
        mainRoom= Server.getRoomFromName(MainRoom.MAIN_ROOM_NAME);
        if(mainRoom==null){
            mainRoom = new Room(MainRoom.MAIN_ROOM_NAME, MainRoom.MAIN_ROOM_NAME, true);
            try {
                Connector.getInstance().getRoomDAO().addObj((Room)mainRoom);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
 /*       Server.RoomsList.add(nr);
        Server.RoomsNameList.add(nr.getRoom_name());
        ArrayList<Long> users = new ArrayList<Long>();
        Server.RoomUsrs.put(nr.getRoom_id(), users);   */
    }

   /* public static void createUser(User usr, Socket cliSock) {
        Server.UsersList.add(usr);//В общий список
        Server.UsrSock.put(usr, cliSock);//В список с сокетами(онлайн)
        Server.UsersNickList.add(usr.getUser_nick()); //Юзер-ник
        Server.UsersIDList.add(usr.getUser_id());   // Юзер-ID
        Server.RoomUsrs.get(MainRoom.MAIN_ROOM_ID).add(usr.getUser_id());
    } */
 /*
    public static ArrayList<User> getUserInTheRoom(Room rm) {
        ArrayList<User> res = new ArrayList<>();
        for (long i : RoomUsrs.get(rm.getRoom_id())) {
            User usr = getUserFromId(i);
            if (usr != null) {
                res.add(usr);
            }
        }
        return res;
    }

    public static User getUserFromId(long id) {
        for (User i : UsersList) {
            if (i.getUser_id() == id) {
                return i;
            }
        }
        return null;
    }

    public static Room getRoomFromId(long id) {
        for (Room i : RoomsList) {
            if (i.getRoom_id() == id) {
                return i;
            }
        }
        return null;
    }
       */
    public static Room getRoomFromName(String name) {
        ArrayList<Room> rooms = null;
        try {
            rooms = (ArrayList<Room>) Connector.getInstance().getRoomDAO()
                    .getRoomsFromParam("room_name", name);
            if (rooms == null ) {
                System.err.println("WARNING: rooms with name "+name+ " is null");
                return null;
            }else if(rooms.size()==0){
                System.err.println("WARNING: rooms.size() with name "+name+ " is 0");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //ar.addAll(Server.GetUsrsName(Server.getUserInTheRoom(Server.getRoomFromName(RoomName))));
        return rooms.get(0);
    }

    public static UserInstance newUsrOnLine(User usr, ClientThread ct){
        UserInstance uInsc=new UserInstance(usr,ct);

        UsrsInstc.add(uInsc);
        return uInsc;
    }

    public static HashSet<UserInstance> getUsrsInstc() {
        return UsrsInstc;
    }

    static public String getDate() {
        return "[" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()) + "]: ";
    }


}
