package ru.itmo.web.hw4.util;

import ru.itmo.web.hw4.model.Post;
import ru.itmo.web.hw4.model.User;
import ru.itmo.web.hw4.model.UserColor;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class DataUtil {
    private static final String TEXT = "We're no strangers to love\n" +
            "You know the rules and so do I\n" +
            "A full commitment's what I'm thinking of\n" +
            "You wouldn't get this from any other guy\n" +
            "\n" +
            "I just want to tell you how I'm feeling\n" +
            "Gotta make you understand\n" +
            "\n" +
            "Never gonna give you up, never gonna let you down\n" +
            "Never gonna run around and desert you\n" +
            "Never gonna make you cry, never gonna say goodbye\n" +
            "Never gonna tell a lie and hurt you\n" +
            "\n" +
            "We've known each other for so long\n" +
            "Your heart's been aching but you're too shy to say it\n" +
            "Inside we both know what's been going on\n" +
            "We know the game and we're gonna play it\n" +
            "\n" +
            "And if you ask me how I'm feeling\n" +
            "Don't tell me you're too blind to see\n" +
            "\n" +
            "Never gonna give you up, never gonna let you down\n" +
            "Never gonna run around and desert you\n" +
            "Never gonna make you cry, never gonna say goodbye\n" +
            "Never gonna tell a lie and hurt you\n" +
            "\n" +
            "Never gonna give you up, never gonna let you down\n" +
            "Never gonna run around and desert you\n" +
            "Never gonna make you cry, never gonna say goodbye\n" +
            "Never gonna tell a lie and hurt you\n" +
            "\n" +
            "(Ooh give you up)\n" +
            "(Ooh give you up)\n" +
            "(Ooh) Never gonna give, never gonna give (give you up)\n" +
            "(Ooh) Never gonna give, never gonna give (give you up)\n" +
            "\n" +
            "We've known each other for so long\n" +
            "Your heart's been aching but you're too shy to say it\n" +
            "Inside we both know what's been going on\n" +
            "We know the game and we're gonna play it\n" +
            "\n" +
            "I just want to tell you how I'm feeling\n" +
            "Gotta make you understand\n" +
            "\n" +
            "Never gonna give you up, never gonna let you down\n" +
            "Never gonna run around and desert you\n" +
            "Never gonna make you cry, never gonna say goodbye\n" +
            "Never gonna tell a lie and hurt you\n";

    private static final List<User> USERS = Arrays.asList(
            new User(1, "MikeMirzayanov", "Mike Mirzayanov", UserColor.BLUE),
            new User(6, "pashka", "Pavel Mavrin", UserColor.RED),
            new User(9, "geranazavr555", "Georgiy Nazarov", UserColor.GREEN),
            new User(11, "tourist", "Gennady Korotkevich", UserColor.BLUE)
    );

    private static final List<Post> POSTS = Arrays.asList(
            new Post(11, "Never gonna tell a lie and hurt you", TEXT, 1),
            new Post(16, "Never gonna make you cry, never gonna say goodbye", TEXT, 6),
            new Post(19, "Never gonna run around and desert you", TEXT, 9),
            new Post(111, "Never gonna give you up, never gonna let you down", TEXT, 11),
            new Post(21, "Let's sing!", "Rick Astley - Never Gonna Give You Up", 1)
    );

    private static final List<UserColor> userColors = Arrays.asList(
            UserColor.GREEN, UserColor.BLUE, UserColor.RED
    );

    public static void addData(HttpServletRequest request, Map<String, Object> data) {
        data.put("posts", POSTS);
        data.put("users", USERS);
        data.put("userColors", userColors);

        for (User user : USERS) {
            if (Long.toString(user.getId()).equals(request.getParameter("logged_user_id"))) {
                data.put("user", user);
            }
        }
    }
}