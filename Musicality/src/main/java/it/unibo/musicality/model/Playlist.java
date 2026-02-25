package it.unibo.musicality.model;

public class Playlist {
    private int id;
    private String name;
    private String description;
    private boolean visibility;
    private String owner;

    public Playlist(int id, String name, String description, boolean visibility, String owner){
        this.id = id;
        this.name = name;
        this.description = description;
        this.visibility = visibility;
        this.owner = owner;
    }

    public int getId(){ return id; }
    public void setId(int id){ this.id = id; }
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }
    public boolean getVisibility(){ return visibility; }
    public void setVisibility(boolean visibility){ this.visibility = visibility; }
    public String getOwner(){ return owner; }
    public void setOwner(String owner){ this.owner = owner; }
}