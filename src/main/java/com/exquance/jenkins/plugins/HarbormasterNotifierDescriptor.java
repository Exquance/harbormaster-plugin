// Copyright (c) 2015 Uber Technologies, Inc.
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

import com.exquance.jenkins.plugins.conduit.ConduitCredentials;
import com.exquance.jenkins.plugins.conduit.ConduitCredentialsDescriptor;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Descriptor for {@link HarbormasterNotifier}. Used as a singleton.
 * The class is marked as public so that it can be accessed from views.
 *
 * <p>
 * See <tt>src/main/resources/hudson/plugins/.../HarbormasterNotifier/*.jelly</tt>
 * for the actual HTML fragment for the configuration screen.
 */
@SuppressWarnings("UnusedDeclaration")
@Extension
public final class HarbormasterNotifierDescriptor extends BuildStepDescriptor<Publisher> {
    private String credentialsID;

    public HarbormasterNotifierDescriptor() {
        super(HarbormasterNotifier.class);
        load();
    }

    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
        // Indicates that this builder can be used with all kinds of project types
        return true;
    }

    /**
     * This human readable name is used in the configuration screen.
     */
    public String getDisplayName() {
        return "Notify Harbormaster of build status";
    }

    @SuppressWarnings("unused")
    public ListBoxModel doFillCredentialsIDItems(@AncestorInPath Jenkins context,
                                                 @QueryParameter String remoteBase) {
        return ConduitCredentialsDescriptor.doFillCredentialsIDItems(
                context);
    }

    public ConduitCredentials getCredentials(Job owner) {
        return ConduitCredentialsDescriptor.getCredentials(owner, credentialsID);
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
        // To persist global configuration information,
        // set that to properties and call save().
        req.bindJSON(this, formData.getJSONObject("harbormaster"));
        save();
        return super.configure(req, formData);
    }

    public String getCredentialsID() {
        return credentialsID;
    }

    public void setCredentialsID(String credentialsID) {
        this.credentialsID = credentialsID;
    }
}
