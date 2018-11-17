package net.jcip.examples;

import java.util.*;

/**
 * Publishing an object
 */
class Secrets {
    public static Set<Secret> knownSecrets;

    public void initialize() {
        knownSecrets = new HashSet<Secret>();
    }
}


class Secret {
}
