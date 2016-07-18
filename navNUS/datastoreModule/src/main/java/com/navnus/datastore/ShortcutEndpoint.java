package com.navnus.datastore;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "shortcutApi",
        version = "v1",
        resource = "shortcut",
        namespace = @ApiNamespace(
                ownerDomain = "datastore.navnus.com",
                ownerName = "datastore.navnus.com",
                packagePath = ""
        )
)
public class ShortcutEndpoint {

    private static final Logger logger = Logger.getLogger(ShortcutEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Shortcut.class);
    }

    /**
     * Returns the {@link Shortcut} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Shortcut} with the provided ID.
     */
    @ApiMethod(
            name = "getShortcut",
            path = "shortcut/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Shortcut get(@Named("id") int id) throws NotFoundException {
        logger.info("Getting Shortcut with ID: " + id);
        Shortcut shortcut = ofy().load().type(Shortcut.class).id(id).now();
        if (shortcut == null) {
            throw new NotFoundException("Could not find Shortcut with ID: " + id);
        }
        return shortcut;
    }

/*    @ApiMethod(
            name = "getAllShortcut",
            path = "",
            httpMethod = ApiMethod.HttpMethod.GET)
    public List<Shortcut> getAllShortcut() throws NotFoundException {
        logger.info("Getting ALL Shortcuts");
        //Shortcut shortcut = ofy().load().type(Shortcut.class).id(id).now();
        List<Shortcut> shortcut = ofy().load().type(Shortcut.class).list();
        if (shortcut == null) {
            throw new NotFoundException("Could not find any Shortcut");
        }
        return shortcut;
    }*/

    /**
     * Inserts a new {@code Shortcut}.
     */
    @ApiMethod(
            name = "insertShortcut",
            path = "shortcut",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Shortcut insert(Shortcut shortcut) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that shortcut.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(shortcut).now();
        logger.info("Created Shortcut with ID: " + shortcut.getId());

        return ofy().load().entity(shortcut).now();
    }

    /**
     * Updates an existing {@code Shortcut}.
     *
     * @param id       the ID of the entity to be updated
     * @param shortcut the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Shortcut}
     */
    @ApiMethod(
            name = "updateShortcut",
            path = "shortcut/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Shortcut update(@Named("id") long id, Shortcut shortcut) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(shortcut).now();
        logger.info("Updated Shortcut: " + shortcut);
        return ofy().load().entity(shortcut).now();
    }

    /**
     * Deletes the specified {@code Shortcut}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Shortcut}
     */
    @ApiMethod(
            name = "removeShortcut",
            path = "shortcut/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Shortcut.class).id(id).now();
        logger.info("Deleted Shortcut with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "shortcut",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Shortcut> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Shortcut> query = ofy().load().type(Shortcut.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Shortcut> queryIterator = query.iterator();
        List<Shortcut> shortcutList = new ArrayList<Shortcut>(limit);
        while (queryIterator.hasNext()) {
            shortcutList.add(queryIterator.next());
        }
        return CollectionResponse.<Shortcut>builder().setItems(shortcutList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(long id) throws NotFoundException {
        try {
            ofy().load().type(Shortcut.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Shortcut with ID: " + id);
        }
    }
}