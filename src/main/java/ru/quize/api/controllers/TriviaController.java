package ru.quize.api.controllers;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.quize.api.params.GameParam;


@RestController
@RequestMapping("api/trivia")
public class TriviaController {
    RestTemplate restTemplate = new RestTemplate();
    GameParam game = new GameParam(21);

    @GetMapping("/question") //Рандомный вопрос по любой теме с любой сложностью
    public String getRandomQuestion(
            @RequestParam(name = "category", required = false) Integer numCategory,
            @RequestParam(name = "difficulty", required = false) String difficulty,
            @RequestParam(name = "type", required = false) String answerType
    ) {
        if (numCategory != null) {
            game.setNumCategory(numCategory+8);
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
        return restTemplate.getForObject(game.getUrl(), String.class);
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

}