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

import com.exquance.jenkins.plugins.conduit.ConduitAPIClient;
import com.exquance.jenkins.plugins.conduit.ConduitAPIException;
import com.exquance.jenkins.plugins.conduit.ConduitCredentials;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Job;
import hudson.model.Result;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import net.sf.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;

public class HarbormasterNotifier extends Notifier {
    private static final Logger LOGGER = Logger.getLogger(HarbormasterNotifier.class.getName());
    private static final String CONDUIT_TAG = "conduit";

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public HarbormasterNotifier() {
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    /**
     * Main method.
     */
    @Override
    public final boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher,
                                 final BuildListener listener) throws InterruptedException, IOException {
        EnvVars environment = build.getEnvironment(listener);
        String phid = environment.get("PHID");
        final boolean harbormasterSuccess = build.getResult().isBetterOrEqualTo(Result.SUCCESS);

        JSONObject params = new JSONObject();
        params.element("type", harbormasterSuccess ? "pass" : "fail")
                .element("buildTargetPHID", phid);

        try {
            ConduitAPIClient client = getConduitClient(build.getParent());
            client.perform("harbormaster.sendmessage", params);
        } catch (ConduitAPIException e) {
            LOGGER.log(Level.WARNING, CONDUIT_TAG, e);
            return false;
        }

        return true;
    }

    private ConduitAPIClient getConduitClient(Job owner) throws ConduitAPIException {
        ConduitCredentials credentials = getConduitCredentials(owner);
        if (credentials == null) {
            throw new ConduitAPIException("No credentials configured for conduit");
        }
        return new ConduitAPIClient(credentials.getUrl(), credentials.getToken().getPlainText());
    }

    private ConduitCredentials getConduitCredentials(Job owner) {
        return getDescriptor().getCredentials(owner);
    }

    private String getPhabricatorURL(Job owner) {
        ConduitCredentials credentials = getDescriptor().getCredentials(owner);
        if (credentials != null) {
            return credentials.getUrl();
        }
        return null;
    }

    // Overridden for better type safety.
    @Override
    public HarbormasterNotifierDescriptor getDescriptor() {
        return (HarbormasterNotifierDescriptor) super.getDescriptor();
    }
}
