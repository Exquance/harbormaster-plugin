package com.exquance.jenkins.plugins;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HarbormasterPayloadProcessorTest {

    @Mock private HttpServletRequest request;
    @Mock private HarbormasterJobProbe probe;

    private HarbormasterPayloadProcessor payloadProcessor;

    @Before
    public void setUp() {
        payloadProcessor = new HarbormasterPayloadProcessor(probe);
    }

    @Test
    public void testProcessPayload() {
        String user = "PHID-USER-snovkf3y34d37i4e4p4t";
        String build = "PHID-BLD-snovkf3y34d37i4e4p4t";
        String url = "https://phabricator.example.com/test";

        JSONObject payload = new JSONObject()
            .element("initiator", new JSONObject()
                .element("phid", user))
            .element("target", new JSONObject()
                .element("phid", build))
            .element("repository", new JSONObject()
                .element("uri", url)
                .element("vcs", "git")
                .element("callsign", "TEST"));

        payloadProcessor.processPayload(payload, request);

        verify(probe).triggerMatchingJobs(user, url, "git");
    }
}
