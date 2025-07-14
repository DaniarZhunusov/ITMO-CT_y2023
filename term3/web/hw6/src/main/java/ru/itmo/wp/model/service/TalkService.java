package ru.itmo.wp.model.service;

import com.google.common.base.Strings;
import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.repository.TalkRepository;
import ru.itmo.wp.model.repository.UserRepository;
import ru.itmo.wp.model.repository.impl.TalkRepositoryImpl;
import ru.itmo.wp.model.repository.impl.UserRepositoryImpl;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class TalkService {
    private final TalkRepository talkRepository = new TalkRepositoryImpl();
    private final UserRepository userRepository = new UserRepositoryImpl();

    public void save(Talk talk) {
        talkRepository.save(talk);
    }

    public void validateText(String text) throws ValidationException {
        if (Strings.isNullOrEmpty(text)) {
            throw new ValidationException("Text is required");
        }
        if (text.length() > 255) {
            throw new ValidationException("Email can't be longer than 255 letters");
        }
    }

    public List<Talk> findAllById(long id) {
        List<Talk> talks = talkRepository.findAllById(id);
        talks.forEach(this::addSourceAndTargetUsers);
        return talks;
    }

    private void addSourceAndTargetUsers(Talk talk) {
        talk.setSourceUser(userRepository.find(talk.getSourceUserId()));
        talk.setTargetUser(userRepository.find(talk.getTargetUserId()));
    }

    public void saveTalk(long sourceUserId, long targetUserId, String text) {
        Talk talk = new Talk();
        talk.setSourceUserId(sourceUserId);
        talk.setTargetUserId(targetUserId);
        talk.setText(text);
        save(talk);
    }

    public void validateUser(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("Unknown target");
        }
    }
}
