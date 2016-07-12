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
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "memberApi",
        version = "v1",
        resource = "member",
        namespace = @ApiNamespace(
                ownerDomain = "datastore.navnus.com",
                ownerName = "datastore.navnus.com",
                packagePath = ""
        )
)
public class MemberEndpoint {

    private static final Logger logger = Logger.getLogger(MemberEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Member.class);
    }

    /**
     * Returns the {@link Member} with the corresponding ID.
     *
     * @param username the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Member} with the provided ID.
     */
    @ApiMethod(
            name = "getMember",
            path = "member/{username}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Member get(@Named("username") String username) throws NotFoundException {
        logger.info("Getting Member with ID: " + username);
        Member member = ofy().load().type(Member.class).id(username).now();
        if (member == null) {
            throw new NotFoundException("Could not find Member with ID: " + username);
        }
        return member;
    }

    /**
     * Inserts a new {@code Member}.
     */
    @ApiMethod(
            name = "insertMember",
            path = "member",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Member insert(Member member) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that member.username has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(member).now();
        logger.info("Created Member with ID: " + member.getUsername());

        return ofy().load().entity(member).now();
    }

    /**
     * Updates an existing {@code Member}.
     *
     * @param username the ID of the entity to be updated
     * @param member   the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code username} does not correspond to an existing
     *                           {@code Member}
     */
    @ApiMethod(
            name = "updateMember",
            path = "member/{username}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Member update(@Named("username") String username, Member member) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(username);
        ofy().save().entity(member).now();
        logger.info("Updated Member: " + member);
        return ofy().load().entity(member).now();
    }

    /**
     * Deletes the specified {@code Member}.
     *
     * @param username the ID of the entity to delete
     * @throws NotFoundException if the {@code username} does not correspond to an existing
     *                           {@code Member}
     */
    @ApiMethod(
            name = "removeMember",
            path = "member/{username}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("username") String username) throws NotFoundException {
        checkExists(username);
        ofy().delete().type(Member.class).id(username).now();
        logger.info("Deleted Member with ID: " + username);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "listMember",
            path = "member",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Member> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Member> query = ofy().load().type(Member.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Member> queryIterator = query.iterator();
        List<Member> memberList = new ArrayList<Member>(limit);
        while (queryIterator.hasNext()) {
            memberList.add(queryIterator.next());
        }
        return CollectionResponse.<Member>builder().setItems(memberList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private boolean checkExists(String username) throws NotFoundException {
        try {
            ofy().load().type(Member.class).id(username).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            System.out.println("Could not find Member with ID: " + username);
            return false;
        }
        return true;
    }
}