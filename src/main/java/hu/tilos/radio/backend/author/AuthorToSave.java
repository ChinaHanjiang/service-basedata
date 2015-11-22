package hu.tilos.radio.backend.author;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


public class AuthorToSave {

    @NotNull
    @Pattern(regexp = "[\\-\\w]+")
    private String name;

    @NotNull
    @Pattern(regexp = "[\\-\\w]+")
    private String alias;

    private String introduction;

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
