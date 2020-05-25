package server;

import user.User;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataServer {

    // 用户集合
    public static Vector<User> listuser = new Vector<User>();

    // 线程池
    public static ExecutorService pool = Executors.newFixedThreadPool(6);

}
