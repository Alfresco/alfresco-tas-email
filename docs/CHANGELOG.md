:paw_prints:  Back to EMAIL [README](README.md).

---
# Change Log
All notable changes to this project will be documented in this file. 

Each tag bellow has a corresponded version released in [Nexus](https://nexus.alfresco.com/nexus/#welcome).
(if you need to update/polish tests please branch from the release tags)

## [[v5.2.0-1] - 2017-02-16](/tas/alfresco-tas-email-test/commits/v5.2.0-1)
- added Full phase tests
- removed unused imports
- using Utility v1.0.11

## [[v5.2.0-0] - 2016-12-22](/tas/alfresco-tas-email-test/commits/v5.2.0-0)
- works with Alfresco 5.2
- using Utility v1.0.7
- mark tests with sanity group
- fixed minor issue on Jolokia agent (calling write method)
- fixed minor issue with IMAP delete folder method
- moved test groups from class level to method level
- updated default.properties
- updated STEP messages from JmxUtil
- updated unit tests to run on Docker environments
- updated ServerConfiguration.saveConfiguration to read the properties only once
- updated ServerConfiguration.restoreServerConfiguration to only restore modified properties
- updated resetServerConfiguration to run after every SMTP test in order to simplify the tests logic
- added 100 % Sanity tests and 100 % Core tests for EMAIL that assures 50% test coverage for EMAIL
