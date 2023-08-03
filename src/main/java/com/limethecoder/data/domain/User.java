package com.limethecoder.data.domain;

import com.limethecoder.util.DisplayUtil;
import com.limethecoder.util.validation.PasswordMatches;
import com.limethecoder.util.validation.ValidImage;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@PasswordMatches
public class User {
    @Id
    @NotNull
    @NotEmpty
    @Size(min = 4, max=50)
    private String login;

    @Column(length = 60)
    @NotNull
    @NotEmpty
    @Size(min = 6, max = 60)
    private String password;

    @NotNull
    @NotEmpty
    @Size(max=50)
    private String name;

    @NotNull
    @NotEmpty
    @Size(max = 50)
    private String surname;

    @NotNull
    @NotEmpty
    @Size(max = 50)
    private String city;

    @Column(name="photo_url")
    private String photoUrl;

    @Transient
    private String matchingPassword;

    @Transient
    @ValidImage
    private MultipartFile photo;

    private boolean enabled;


    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "login"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "name"))
    private List<Role> roles;

    @Override
    public String toString() {
        return login;
    }

    public String printRoles() {
        return DisplayUtil.printList(roles);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public MultipartFile getPhoto() {
        return photo;
    }

    public void setPhoto(MultipartFile photo) {
        this.photo = photo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return login.equals(user.login);
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }
}
