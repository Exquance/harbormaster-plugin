package com.exquance.jenkins.plugins;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

public class HarbormasterPayloadProcessor {

    private final HarbormasterJobProbe probe;

    public HarbormasterPayloadProcessor(HarbormasterJobProbe probe) {
        this.probe = probe;
    }

    public HarbormasterPayloadProcessor() {
        this(new HarbormasterJobProbe());
    }

/*
{
    "repository": {
        "uri": The URI to clone or checkout the repository from.
        "vcs": The version control system, either "svn", "hg" or "git".
        "callsign": The callsign of the repository in Phabricator.
        "phid": The PHID of the repository in Phabricator.
        "staging": {
            "ref": The ref name for this change in the staging repository.
            "uri": The URI of the staging repository.
        }
    },
    "initiator": {
        "phid": The PHID of the user or Object that initiated the build, if applicable.
    },
    "buildable": {
        "commit": The commit identifier, if applicable.
        "diff": The differential diff ID, if applicable.
        "revision": The differential revision ID, if applicable.
    }
}
*/
    public void processPayload(JSONObject payload, HttpServletRequest request) {
        JSONObject repo = payload.getJSONObject("repository");
        LOGGER.info("Received commit hook notification for "+repo.getString("callsign"));

        String user = payload.getJSONObject("initiator").getString("phid");
        // TODO: Query back user name from PHID via phabricator api?
        String url = repo.getString("uri");
        String scm = repo.getString("vcs");

        probe.triggerMatchingJobs(user, url, scm);
    }

    private static final Logger LOGGER = Logger.getLogger(HarbormasterPayloadProcessor.class.getName());

}
