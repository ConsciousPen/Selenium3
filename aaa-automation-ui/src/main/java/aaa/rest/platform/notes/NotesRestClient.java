package aaa.rest.platform.notes;

import aaa.rest.IRestClient;
import aaa.rest.platform.notes.model.NotesResponse;
import toolkit.datax.TestData;
import toolkit.rest.ResponseWrapper;
import toolkit.rest.RestServiceUtil;

public class NotesRestClient implements IRestClient {

    private ThreadLocal<RestServiceUtil> restClient = new ThreadLocal<RestServiceUtil>() {
        @Override protected RestServiceUtil initialValue() {
            return new RestServiceUtil("notes-rs");
        }
    };

    /**
     * <b>Target:</b> Notes
     */
    public ResponseWrapper postNotes(TestData data) {
        return restClient.get().processRequest("NOTES", RestServiceUtil.RestMethod.POST, data);
    }

    public NotesResponse getPostNotesResponse(TestData data) {
        return postNotes(data).getResponse().readEntity(NotesResponse.class);
    }

    /**
     * <b>Target:</b> /notes/{entityType}/{entityRefNo}
     */
    public ResponseWrapper getNotesByTypeAndRefItem(TestData data) {
        return restClient.get().processRequest("NOTESBYTYPEANDREF.ITEM", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /notes/{noteId}
     */
    public ResponseWrapper getNotesItem(TestData data) {
        return restClient.get().processRequest("NOTESBYTYPE.ITEM", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /notes/{noteId}
     */
    public ResponseWrapper putNotesItem(TestData data) {
        return restClient.get().processRequest("NOTESBYTYPE.ITEM", RestServiceUtil.RestMethod.PUT, data);
    }

    /**
     * <b>Target:</b> SwaggerJson
     */
    public ResponseWrapper getSwaggerJson(TestData data) {
        return restClient.get().processRequest("SWAGGERJSON", RestServiceUtil.RestMethod.GET, data);
    }

    public RestServiceUtil getRestClient() {
        return this.restClient.get();
    }

}
