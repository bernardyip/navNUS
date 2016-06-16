package com.navnus.utility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by berna on 16/6/2016.
 */
public class Util {

 /*
 * Write an object to a serializable file
 */
    public static void writeSerializable(Serializable object, String path) {
        try {
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.close();
            fileOut.close();
        } catch(IOException i) {
            i.printStackTrace();
        }
    }

    /*
     * Read a serializable file and convert to an object
     *
     * @returns The serializable object, remember to cast it after the return
     */
    public static Serializable readSerializable(InputStream path) {
        try {
            ObjectInputStream in = new ObjectInputStream(path);
            Serializable serializable = (Serializable) in.readObject();
            in.close();
            path.close();
            return serializable;
        } catch(IOException i) {
            i.printStackTrace();
        } catch(ClassNotFoundException c) {
            c.printStackTrace();
        }
        return null;
    }
}
