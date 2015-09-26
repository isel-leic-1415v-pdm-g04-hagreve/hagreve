package pt.isel.pdm.g04.se2_1.serverside.bags;

/**
 * Project SE2-1, created on 2015/03/18.
 */
public class Submitter implements Comparable<Submitter> {
    public String first_name; // Primeiro nome do autor do aviso ("José");
    public String last_name; // Último nome do autor do aviso ("Silva");

    public Submitter(String first_name, String last_name) {
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public Submitter(Builder builder) {
        this(builder.first_name, builder.last_name);
    }

    public static Builder builder() {
        return new Submitter.Builder();
    }

    @Override
    public int compareTo(Submitter another) {
        int _first_name_comparison = first_name.compareTo(another.first_name);
        if (_first_name_comparison != 0) return _first_name_comparison;
        return last_name.compareTo(another.last_name);
    }

    // region Comparable

    public static class Builder {
        private String first_name;
        private String last_name;

        public Builder first_name(String first_name) {
            this.first_name = first_name;
            return this;
        }

        public Builder last_name(String last_name) {
            this.last_name = last_name;
            return this;
        }

        public Submitter build() {
            return new Submitter(this);
        }
    }

    // endregion Comparable

}