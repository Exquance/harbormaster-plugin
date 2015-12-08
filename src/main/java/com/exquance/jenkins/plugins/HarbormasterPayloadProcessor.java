// Copyright (c) 2015 Exquance Software Oy
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

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
