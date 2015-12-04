package com.exquance.jenkins.plugins;

import hudson.triggers.SCMTrigger;

import java.io.File;
import java.io.IOException;

/**
 */
public class HarbormasterPushCause extends SCMTrigger.SCMTriggerCause {

    private String pushedBy;

    public HarbormasterPushCause(String pusher) {
        this("", pusher);
    }

    public HarbormasterPushCause(String pollingLog, String pusher) {
        super(pollingLog);
        pushedBy = pusher;
    }

    public HarbormasterPushCause(File pollingLog, String pusher) throws IOException {
        super(pollingLog);
        pushedBy = pusher;
    }

    @Override
    public String getShortDescription() {
        String pusher = pushedBy != null ? pushedBy : "";
        return "Started by Diffusion push by " + pusher;
    }
}
