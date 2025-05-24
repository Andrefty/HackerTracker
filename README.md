## HackerTracker
"HackerTracker" is a simulated Android fitness application designed to appear as a simple step tracking app. Its core purpose is to serve as an educational tool, demonstrating common mobile application security flaws. This application is ideal for students, security enthusiasts, and professionals looking for a practical sandbox to learn and practice static and dynamic analysis of Android applications.

This app features the following intentionally implemented vulnerabilities:

1.  **Hardcoded Admin Credentials:** Discover embedded administrative credentials offering a backdoor for privileged access.
2.  **Step Count Tampering via Broadcast Injection:** Exploit an insecure `BroadcastReceiver` to inject fake step data.
3.  **Exported Activity with Sensitive Actions (No Authentication):** Directly launch and interact with an unprotected administrative `Activity`.
4.  **Clipboard Snooping for Sensitive Data:** Observe how the app can improperly access and log data copied to the clipboard (demonstrated on Android 7.0).
5.  **Insecure Feature Flags via System Properties:** Manipulate system properties to unlock "premium" features without authorization (demonstrated on Android 13).

**Project Details:**
*   **Team Name:** 0xDEADCODE
*   **Team Members:** Tudor-Andrei FĂRCĂȘANU, Alexandru TOADER
