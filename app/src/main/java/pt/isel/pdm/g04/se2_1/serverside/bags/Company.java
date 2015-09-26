package pt.isel.pdm.g04.se2_1.serverside.bags;

/**
 * Project SE2-1, created on 2015/03/18.
 */
public class Company implements HasId, Comparable<Company> {
    public final int id;
    public final String name; // Nome da empresa ("Empresa A")

    public Company(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Company(Builder builder) {
        this(builder.id, builder.name);
    }

    public static Builder builder() {
        return new Builder();
    }

    // region HasId

    @Override
    public int getId() {
        return id;
    }

    // endregion HasId

    // region Comparable

    @Override
    public int compareTo(Company another) {
        return id != another.id ? id - another.id : name.compareTo(another.name) == 0 ? 0 :
                name.compareTo(another.name + " (" + String.valueOf(another.id) + ")");
    }

    // endregion Comparable

    // region Builder

    public static class Builder {
        private int id;
        private String name;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Company build() {
            return new Company(this);
        }
    }

    // endregion Builder

}