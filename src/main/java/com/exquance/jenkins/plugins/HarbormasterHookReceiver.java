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

import hudson.Extension;
import hudson.model.UnprotectedRootAction;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.StaplerRequest;

/**
 */
@Extension
public class HarbormasterHookReceiver implements UnprotectedRootAction {

    private final HarbormasterPayloadProcessor payloadProcessor = new HarbormasterPayloadProcessor();
    private final String HARBORMASTER_HOOK_URL = "harbormaster-hook";

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return HARBORMASTER_HOOK_URL;
    }

    /**
     * Harbormaster sends payload as urlencoded query string <pre>payload=JSON</pre>
     * @throws IOException
     */
    public void doIndex(StaplerRequest req) throws IOException {
        if (req.getRequestURI().contains("/" + HARBORMASTER_HOOK_URL + "/")) {
            String body = URLDecoder.decode(req.getQueryString());
            if (body.startsWith("payload=")) body = body.substring(8);

            LOGGER.log(Level.FINE, "Received commit hook notification : {0}", body);
            JSONObject payload = JSONObject.fromObject(body);

            payloadProcessor.processPayload(payload, req);
        } else {
            LOGGER.log(Level.WARNING, "The Jenkins job cannot be triggered. You might not have configured correctly the WebHook on Harbormaster with the last slash `http://<JENKINS-URL>/harbormaster-hook/`");
        }
    }

    private static final Logger LOGGER = Logger.getLogger(HarbormasterHookReceiver.class.getName());

}
