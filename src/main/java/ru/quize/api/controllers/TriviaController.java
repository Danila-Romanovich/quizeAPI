package ru.quize.api.controllers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.quize.api.models.Question;
import ru.quize.api.models.QuestionResponse;
import ru.quize.api.params.GameParam;

import java.util.ArrayList;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://site")
@RequestMapping("api/trivia")
public class TriviaController {
    RestTemplate restTemplate = new RestTemplate();
    GameParam game = new GameParam(21);

    @GetMapping("/question") //Рандомный вопрос по любой теме с любой сложностью
    public List<Question> getRandomQuestion(
            @RequestParam(name = "category", required = false) Integer numCategory,
            @RequestParam(name = "difficulty", required = false) String difficulty,
            @RequestParam(name = "type", required = false) String answerType
    ) {
        if (numCategory != null || numCategory != 0) {
            game.setNumCategory(numCategory + 8);
        } else {
            game.setNumCategory(0);
        }
        if (difficulty != null) {
            game.setDifficulty(difficulty);
        } else {
            game.setDifficulty("all");
        }
        if (answerType != null) {
            game.setAnswerType(answerType);
        } else {
            game.setAnswerType("all");
        }
        System.out.println(game.getUrl());
        String requestResult = restTemplate.getForObject(game.getUrl(), String.class);
        ObjectMapper objectMapper = new ObjectMapper();

        List<Question> correctedQuestions = new ArrayList<>();

        try {
            // Преобразование JSON в объект
            QuestionResponse questionResponse = objectMapper.readValue(requestResult, QuestionResponse.class);

            // Теперь объект questionResponse, который содержит список вопросов
            List<Question> questions = questionResponse.getResults();
            decoding(questions);
            // Пример использования данных
            for (Question question : questions) {
                System.out.println("Category: " + question.getCategory());
                System.out.println("Difficulty: " + question.getDifficulty());
                System.out.println("Type: " + question.getType());
                System.out.println("Question: " + question.getQuestion());
                System.out.println("Correct Answer: " + question.getCorrectAnswer());
                System.out.println("Incorrect Answers: " + question.getIncorrectAnswers());
                System.out.println("------------------------------");

                correctedQuestions.add(question);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(correctedQuestions);

        return correctedQuestions;
    }

    public void decoding (List<Question> questions) {
        for (Question question : questions) {
            question.setQuestion(StringEscapeUtils.unescapeHtml4(question.getQuestion()));
            question.setCategory(StringEscapeUtils.unescapeHtml4(question.getCategory()));
            question.setCorrectAnswer(StringEscapeUtils.unescapeHtml4(question.getCorrectAnswer()));

            List<String> decodedIncorrectAnswers = new ArrayList<>();
            for (String incorrectAnswer : question.getIncorrectAnswers()) {
                decodedIncorrectAnswers.add(StringEscapeUtils.unescapeHtml4(incorrectAnswer));
            }
            question.setIncorrectAnswers(decodedIncorrectAnswers);

        }
    }

}
/*      1 - General Knowledge
        2 - Books
        3 - Film
        4 - Music
        5 - Musicals & Theatres
        6 - Television
        7 - Video Games
        8 - Board Games
        9 - Science & Nature
        10 - Computers
        11 - Mathematics
        12 - Mythology
        13 - Sports
        14 - Geography
        15 - History
        16 - Politics
        17 - Art
        18 - Celebrities
        19 - Animals
        20 - Vehicles
        21 - Comics
        22 - Gadgets
        23 - Japanese Anime & Manga
        24 - Cartoon & Animations
*/

/*
        easy
        medium
        hard
 */

/*
        multiple
        boolean
 */
