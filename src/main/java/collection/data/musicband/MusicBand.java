package collection.data.musicband;

import collection.AbstractAdapter;
import collection.CollectionItem;
import collection.data.exceptions.InvalidFieldException;
import database.DBRequest;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;

public class MusicBand extends AbstractAdapter  {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long numberOfParticipants; //Поле не может быть null, Значение поля должно быть больше 0
    private String description; //Поле может быть null
    private LocalDateTime establishmentDate; //Поле не может быть null
    private MusicGenre genre; //Поле не может быть null
    private Person frontMan; //Поле не может быть null

    {
        creationDate = ZonedDateTime.now();
        coordinates = new Coordinates();
        frontMan = new Person();

        formatMap.put("name", " (not empty)");
        formatMap.put("x", " (< 38)");
        formatMap.put("y", " (< 627)");
        formatMap.put("numberOfParticipants", " (>0)");
        formatMap.put("establishmentDate", " (format: YYYY-MM-DDTHH:MM:SS)");
        formatMap.put("genre", " (PROGRESSIVE_ROCK/PSYCHEDELIC_ROCK/HIP-HOP/POST_PUNK)");
        formatMap.put("frontManBirthday", " (format: YYYY-MM-DD)");
        formatMap.put("frontManPassportID", " (length of the string should be from 6 to 49)");
        formatMap.put("frontManEyeColor", " (GREEN/RED/BLACK/BLUE/WHITE)");
    }

    public MusicBand(Long id) {
        this.id = id;
        gettersMap.put("name", this::getName);
        gettersMap.put("x", ()-> String.valueOf(this.getCoordinates().getX()));
        gettersMap.put("y", () -> this.getCoordinates().getY().toString());
        gettersMap.put("creationDate", () -> this.getCreationDate().toString());
        gettersMap.put("numberOfParticipants", () -> this.getNumberOfParticipants().toString());
        gettersMap.put("description", this::getDescription);
        gettersMap.put("establishmentDate", () -> this.getEstablishmentDate().toString());
        gettersMap.put("genre", () -> this.getGenre().toString());
        gettersMap.put("frontManName", () -> this.getFrontMan().getName());
        gettersMap.put("frontManBirthday", () -> this.getFrontMan().getBirthday().toString());
        gettersMap.put("frontManPassportID", () -> this.getFrontMan().getPassportID());
        gettersMap.put("frontManEyeColor", () -> this.getFrontMan().getEyeColor().toString());
        gettersMap.put("frontManLocationX", () -> this.getFrontMan().getLocation().getX().toString());
        gettersMap.put("frontManLocationY", () -> String.valueOf(this.getFrontMan().getLocation().getY()));
        gettersMap.put("frontManLocationZ", () -> String.valueOf(this.getFrontMan().getLocation().getZ()));

        bannedSettersList.add("creationDate");

        settersMap.put("name", this::setName);
        settersMap.put("x", x -> this.getCoordinates().setX(Double.parseDouble(x)));
        settersMap.put("y", y -> this.getCoordinates().setY(Integer.valueOf(y)));
        settersMap.put("creationDate", str -> setCreationDate(ZonedDateTime.parse(str)));
        settersMap.put("numberOfParticipants", num -> this.setNumberOfParticipants(Long.valueOf(num)));
        settersMap.put("description", this::setDescription);
        settersMap.put("establishmentDate", date -> this.setEstablishmentDate(LocalDateTime.parse(date)));
        settersMap.put("genre", genre -> this.setGenre(MusicGenre.valueOf(genre)));
        settersMap.put("frontManName", name -> this.getFrontMan().setName(name));
        settersMap.put("frontManBirthday", date -> this.getFrontMan().setBirthday(Date.valueOf(date)));
        settersMap.put("frontManPassportID", ID -> this.getFrontMan().setPassportID(ID));
        settersMap.put("frontManEyeColor", color -> this.getFrontMan().setEyeColor(Color.valueOf(color)));
        settersMap.put("frontManLocationX", x -> this.getFrontMan().getLocation().setX(Integer.valueOf(x)));
        settersMap.put("frontManLocationY", y -> this.getFrontMan().getLocation().setY(Double.parseDouble(y)));
        settersMap.put("frontManLocationZ", z -> this.getFrontMan().getLocation().setZ(Float.parseFloat(z)));
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        if(id==null) throw new InvalidFieldException("id", "null");
        if(id <= 0) throw new InvalidFieldException("id", id.toString());
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name==null) throw new InvalidFieldException("name", "null");
        if(name.equals("")) throw new InvalidFieldException("name", name);
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        if(coordinates==null) throw new InvalidFieldException("coordinates", "null");
        this.coordinates = coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        if(creationDate==null) throw new InvalidFieldException("creationDate", "null");
        this.creationDate = creationDate;
    }

    public Long getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(Long numberOfParticipants) {
        if(numberOfParticipants==null) throw new InvalidFieldException("numberOfParticipants", "null");
        if(numberOfParticipants <= 0) throw new InvalidFieldException("id", numberOfParticipants.toString());
        this.numberOfParticipants = numberOfParticipants;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(description==null) throw new InvalidFieldException("description", "null");
        this.description = description;
    }

    public LocalDateTime getEstablishmentDate() {
        return establishmentDate;
    }

    public void setEstablishmentDate(LocalDateTime establishmentDate) {
        if(establishmentDate==null) throw new InvalidFieldException("establishmentDate", "null");
        this.establishmentDate = establishmentDate;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    public void setGenre(MusicGenre genre) {
        if(genre==null) throw new InvalidFieldException("genre", "null");
        this.genre = genre;
    }

    public Person getFrontMan() {
        return frontMan;
    }

    public void setFrontMan(Person frontMan) {
        if(frontMan==null) throw new InvalidFieldException("frontMan", "null");
        this.frontMan = frontMan;
    }

    @Override
    public int compareTo(CollectionItem o) {
        return numberOfParticipants.compareTo(((MusicBand) o).getNumberOfParticipants());
    }

    @Override
    protected String getDbName() {
        return "\"musicBands\"";
    }

    @Override
    public DBRequest insert() {
        String questionMarks = String.join(", ", Collections.nCopies(16, "?"));
        String sql = String.format("INSERT INTO \"musicBands\" VALUES(nextVal('nextVal'), %s)", questionMarks);
        return new DBRequest(sql,
                getUser(),
                getName(),
                getCoordinates().getX(),
                getCoordinates().getY(),
                getCreationDate().toString(),
                getNumberOfParticipants(),
                getDescription(),
                getEstablishmentDate().toString(),
                getGenre(),
                getFrontMan().getName(),
                getFrontMan().getBirthday(),
                getFrontMan().getPassportID(),
                getFrontMan().getEyeColor(),
                getFrontMan().getLocation().getX(),
                getFrontMan().getLocation().getY(),
                getFrontMan().getLocation().getZ());
    }

    @Override
    public void parse(ResultSet set) throws SQLException {
        super.parse(set);
        setName(set.getString("name"));
        coordinates.setX(set.getDouble("coordinateX"));
        coordinates.setY(set.getInt("coordinateY"));
        setCreationDate(ZonedDateTime.parse(set.getString("creationDate")));
        setNumberOfParticipants(set.getLong("numberOfParticipants"));
        setDescription(set.getString("description"));
        setEstablishmentDate(LocalDateTime.parse(set.getString("establishmentDate")));
        setGenre(MusicGenre.valueOf(set.getString("genre")));
        frontMan.setName(set.getString("frontManName"));
        frontMan.setBirthday(set.getDate("frontManBirthday"));
        frontMan.setPassportID(set.getString("frontManPassportID"));
        frontMan.setEyeColor(Color.valueOf(set.getString("frontManEyeColor")));
        frontMan.getLocation().setX(set.getInt("frontManX"));
        frontMan.getLocation().setY(set.getDouble("frontManY"));
        frontMan.getLocation().setZ(set.getFloat("frontManZ"));
    }
}
