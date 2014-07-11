package com.anagorny.chaters.DAO;

/**
 * Created by sosnov on 05.04.14.
 */
public class Connector {
    private static UserDAO userDAO = null;
    private static RoomDAO roomDAO = null;
    private static MsgsDAO msgDAO = null;
    private static Connector connector = null;

    public UserDAO getUserDAO() {
        if (userDAO == null) {
            userDAO = new UserDAO();
        }
        return userDAO;
    }

    public RoomDAO getRoomDAO() {
        if (roomDAO == null) {
            roomDAO = new RoomDAO();
        }
        return roomDAO;

    }

    public MsgsDAO getMsgDAO() {
        if (msgDAO == null) {
            msgDAO = new MsgsDAO();
        }
        return msgDAO;

    }

    public static synchronized Connector getInstance() {
        if (connector == null) {
            connector = new Connector();
        }
        return connector;
    }
}
