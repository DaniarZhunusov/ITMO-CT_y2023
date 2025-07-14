package ru.itmo.wp.web.page;

import ru.itmo.wp.model.State;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class TicTacToePage {
    private State state = new State();

    private void newGame(Map<String, Object> view, HttpServletRequest request) {
        resetGameState(request);
        view.put("state", state);
    }

    private void onMove(Map<String, Object> view, HttpServletRequest request) {
        initializeSessionState(request);
        processMove(request);
        request.getSession().setAttribute("state", state);
        view.put("state", state);
    }

    private void action(Map<String, Object> view, HttpServletRequest request) {
        initializeSessionState(request);
        view.put("state", state);
    }

    private void initializeSessionState(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("state") == null) {
            session.setAttribute("state", state);
        } else {
            state = (State) session.getAttribute("state");
        }
    }

    private void processMove(HttpServletRequest request) {
        for (String param : request.getParameterMap().keySet()) {
            if (param.matches("cell_[0-2]{2}")) {
                int row = Character.getNumericValue(param.charAt(5));
                int col = Character.getNumericValue(param.charAt(6));
                if (state.isValid(row, col)) {
                    state.makeMove(row, col);
                }
            }
        }
    }

    private void resetGameState(HttpServletRequest request) {
        state = new State();
        request.getSession().setAttribute("state", state);
    }

    public State getState() {
        return state;
    }
}
