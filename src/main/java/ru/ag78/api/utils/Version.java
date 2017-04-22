package ru.ag78.api.utils;

/**
 * Represent a version number.
 * Version number is made by classic scenario: 'major.minor.build'.
 * @author Alexey Gusev
 *
 */
public class Version implements Comparable<Version> {

    /**
     * Major
     */
    private int major;

    /**
     * Minor
     */
    private int minor;

    /**
     * Build-number
     */
    private int build;

    /**
     * Default ctor.
     * All versions are set into 0.
     */
    public Version() {

    }

    /**
     * Ctor with parsing version from string.
     * @param source
     */
    public Version(String source) {

        parse(source);
    }

    /**
     * Split source version into tokens. If token is absent it will be complemented.
     * An Exception will be never thrown. 
     * @param source
     */
    public void parse(String source) {

        source = SafeTypes.getSafeString(source);

        String[] tokens = source.split("\\.");

        major = getToken(tokens, 0);
        minor = getToken(tokens, 1);
        build = getToken(tokens, 2);
    }

    /**
     * Safely returns integer value of the token. 
     * 0 will be returned if token is absent or incorrect.
     * @param tokens
     * @param index
     * @return
     */
    private int getToken(String[] tokens, int index) {

        return SafeTypes.parseSafeInt(SafeArrays.getSafeItem(tokens, index), 0);
    }

    /**
     * Equals implementation.
     */
    @Override
    public boolean equals(Object o) {

        if (!(o instanceof Version)) {
            return false;
        }

        Version v = (Version) o;
        return v.getMajor() == getMajor() && v.getMinor() == getMinor() && v.getBuild() == getBuild();
    }

    /**
     * hashCode implementation
     */
    @Override
    public int hashCode() {

        return this.toString().hashCode();
    }

    /**
     * toString implementation
     */
    @Override
    public String toString() {

        return String.format("%d.%d.%d", major, minor, build);
    }

    /**
     * Compare method.
     */
    @Override
    public int compareTo(Version o) {

        if (major != o.major) {
            return major - o.major;
        }

        if (minor != o.minor) {
            return minor - o.minor;
        }

        return build - o.build;
    }

    public int getMajor() {

        return major;
    }

    public void setMajor(int major) {

        this.major = major;
    }

    public int getMinor() {

        return minor;
    }

    public void setMinor(int minor) {

        this.minor = minor;
    }

    public int getBuild() {

        return build;
    }

    public void setBuild(int build) {

        this.build = build;
    }
}
