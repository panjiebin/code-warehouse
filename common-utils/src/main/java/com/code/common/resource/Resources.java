package com.code.common.resource;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Pan Jiebin
 * @date 2021-03-03 13:39
 */
public class Resources {
    private final String resourceName;
    private final Set<String> versions;

    public Resources(String resourceName) {
        this(resourceName, new HashSet<>());
    }

    public Resources(String resourceName, Set<String> versions) {
        this.resourceName = resourceName;
        this.versions = versions;
    }

    public void addVersion(String version) {
        this.versions.add(version);
    }

    public String getResourceName() {
        return resourceName;
    }

    public Set<String> getVersions() {
        return versions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Resources that = (Resources) o;
        return Objects.equals(resourceName, that.resourceName) &&
                Objects.equals(versions, that.versions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceName, versions);
    }
}
