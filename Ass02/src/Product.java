import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private String description;
    private String ID;
    private double cost;

    private static final int NAME_LENGTH = 35;
    private static final int DESCRIPTION_LENGTH = 75;
    private static final int ID_LENGTH = 6;

    public Product(String name, String description, String ID, double cost) {
        this.name = name;
        this.description = description;
        this.ID = ID;
        this.cost = cost;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getID() { return ID; }
    public double getCost() { return cost; }

    // Setters (except for ID)
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCost(double cost) { this.cost = cost; }

    public String getFormattedName() {
        return String.format("%-" + NAME_LENGTH + "s", name);
    }

    public String getFormattedDescription() {
        return String.format("%-" + DESCRIPTION_LENGTH + "s", description);
    }

    public String getFormattedID() {
        return String.format("%-" + ID_LENGTH + "s", ID);
    }

    public String toFormattedString() {
        return String.format("%s%s%s%.2f", getFormattedName(), getFormattedDescription(), getFormattedID(), cost);
    }

    public static Product fromFormattedString(String formattedString) {
        String name = formattedString.substring(0, NAME_LENGTH).trim();
        String description = formattedString.substring(NAME_LENGTH, NAME_LENGTH + DESCRIPTION_LENGTH).trim();
        String ID = formattedString.substring(NAME_LENGTH + DESCRIPTION_LENGTH, NAME_LENGTH + DESCRIPTION_LENGTH + ID_LENGTH).trim();
        double cost = Double.parseDouble(formattedString.substring(NAME_LENGTH + DESCRIPTION_LENGTH + ID_LENGTH).trim());
        return new Product(name, description, ID, cost);
    }

    @Override
    public String toString() {
        return String.format("Product{ID='%s', name='%s', description='%s', cost=%.2f}",
                ID, name, description, cost);
    }
}