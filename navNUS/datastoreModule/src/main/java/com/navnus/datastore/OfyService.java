package com.navnus.datastore;

/**
 * Created by Crsytral on 7/12/2016.
 */
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 *
 */
public class OfyService {
    static {
        ObjectifyService.register(Member.class);
    }
    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }
    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}