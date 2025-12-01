/*
 * I have not discussed the Java language code in my program with anyone other than my instructor or the teaching assistants assigned to this course.
 * I have not used Java language code obtained from another subject, or any other unauthorized source, either modified or unmodified.
 * If any Java language code or documentation used in my program was obtained from another source, such as a textbook or course notes, that has been clearly noted with a proper citation in the comments of my program.
*/

package ateneo.food.review;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    private String username;
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
