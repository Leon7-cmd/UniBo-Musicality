package it.unibo.musicality.model;

public class MultimediaContent {
    private String id;
    private String name;
    private String type; // e.g., "song", "podcast"
    private String description;
    private String email;
    private String file; // path or URL to the media file

    public MultimediaContent(String id, String name, String type, String description, String email, String file) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.email = email;
        this.file = file;
    }

    public MultimediaContent(){}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultimediaContent)) return false;
        MultimediaContent that = (MultimediaContent) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}