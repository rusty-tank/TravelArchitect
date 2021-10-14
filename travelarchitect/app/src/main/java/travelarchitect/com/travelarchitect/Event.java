package travelarchitect.com.travelarchitect;

/**
 * Created by Kok Siang Tee on 4/20/2017.
 */

public class Event {
    private String name;
    private String date;
    private String category;
    private String description;
    private String imageName;

    public Event(String name, String date, String category, String description, String imageName) {
        this.name = name;
        this.date = date;
        this.category = category;
        this.description = description;
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getImageName() {
        return imageName;
    }
}
