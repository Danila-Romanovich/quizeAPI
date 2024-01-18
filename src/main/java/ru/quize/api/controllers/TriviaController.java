package ru.quize.api.controllers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.quize.api.models.Question;
import ru.quize.api.models.QuestionResponse;
import ru.quize.api.params.GameParam;


import java.util.*;


@RestController
@CrossOrigin(origins = "*")
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
                translate(question);
                System.out.println("Category: " + question.getCategory());
                System.out.println("Difficulty: " + question.getDifficulty());
                System.out.println("Type: " + question.getType());
                System.out.println("Вопрос: " + question.getQuestion());
                System.out.println("Правильный ответ: " + question.getCorrectAnswer());
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

    private void translate(Question question) throws JsonProcessingException {
        String url = "https://translate.api.cloud.yandex.net/translate/v2/translate";
        String token = "t1.9euelZqNx5OVy4mdjMmQi8ucy4yXne3rnpWansiQk8iJzsmLks-ckpXIlJTl9Pd5NF1S-e8aSx-a3fT3OWNaUvnvGksfms3n9euelZqNnsuenJGek57NyJGJnp3Jlu_8xeuelZqNnsuenJGek57NyJGJnp3Jlg.ITU0QAmxFLG82e7tyeWoLM4OqQg8dtZTof717Yo7Pb5V82hf9Q09HuBigGVdiB7tOVsiHSk11yr-AD4kcVJTAg";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        String textForTranslate = mergeText(question);

        Map<String, String> jsonData = new HashMap<>();
        jsonData.put("folderId","b1gd2us8uvnukiqe937m");
        jsonData.put("targetLanguageCode", "ru");
        jsonData.put("texts", textForTranslate);

        HttpEntity<Map<String,String>> request = new HttpEntity<>(jsonData, headers);
        String response = restTemplate.postForObject(url, request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode obj = mapper.readTree(response);

        String newText = String.valueOf(obj.get("translations").get(0).get("text").asText());
        String delimiter = "\\|";
        String[] resultArray = newText.split(delimiter);

        List<String> incorrectAnswer = new ArrayList<>();

        for (int i=0; i<resultArray.length; i++) {
            if (i==0){
                question.setQuestion(resultArray[i]);
            } else if (i==1){
                question.setCorrectAnswer(resultArray[i]);
            } else {
                incorrectAnswer.add(resultArray[i]);
            }
        }
        question.setIncorrectAnswers(incorrectAnswer);
    }

    private String mergeText(Question question) {
        List<String> textForTranslate = new ArrayList<>();
        textForTranslate.add(question.getQuestion());
        textForTranslate.add(question.getCorrectAnswer());
        for (String incorrectAnswer : question.getIncorrectAnswers()) {
            textForTranslate.add(StringEscapeUtils.unescapeHtml4(incorrectAnswer));
        }
        // Используем StringBuilder для склеивания строк с разделителем
        StringBuilder result = new StringBuilder();
        String delimiter = "| ";
        for (String str : textForTranslate) {
            result.append(str).append(delimiter);
        }
        // Удаляем последний добавленный разделитель, если список не пуст
        if (!textForTranslate.isEmpty()) {
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
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
