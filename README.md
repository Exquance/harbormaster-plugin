harbormaster-plugin
===================

Trigger builds via Phabricator Harbormaster.

Based on ideologically close [Bitbucket Plugin](https://wiki.jenkins-ci.org/display/JENKINS/BitBucket+Plugin) by Nicolas De Loof.

Conduit API from Phabricator-Differential-Plugin by Uber.


Harbormaster configuration:

Make HTTP Request
Method: POST
URI: see Urlencoded below
Mode: Wait for result

Generate proper payload using http://urlencode.org:

Source:
    payload={"repository":{"uri":"XXX","vcs":"YYY","callsign":"ZZZ"},"initiator":{"phid":"WWW"},"target":{"phid":"TTT"}}

Substitutions:
    XXX=${repository.uri}
    YYY=${repository.vcs}
    ZZZ=${repository.callsign}
    WWW=${initiator.phid}
    TTT=${target.phid}

Urlencoded:
    payload=%7b%22repository%22%3a%7b%22uri%22%3a%22${repository.uri}%22%2c%22vcs%22%3a%22${repository.vcs}%22%2c%22callsign%22%3a%22${repository.callsign}%22%7d%2c%22initiator%22%3a%7b%22phid%22%3a%22${initiator.phid}%22%7d%2c%22target%22%3a%7b%22phid%22%3a%22${target.phid}%22%7d%7d
