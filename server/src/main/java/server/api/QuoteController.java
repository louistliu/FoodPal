/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package server.api;

import commons.Quote;
import java.util.List;
import java.util.Random;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.QuoteRepository;

/**
 * Defines Spring Boot REST api Controller handling quote endpoints.
 */
@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    private final Random random;
    private final QuoteRepository repo;

    /**
     * Creates a new Quote Controller and injects dependencies into it.
     *
     * @param random random number generator singleton
     * @param repo   database for persistent storage
     */
    public QuoteController(Random random, QuoteRepository repo) {
        this.random = random;
        this.repo = repo;
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    @GetMapping(path = {"", "/"})
    public List<Quote> getAll() {
        return repo.findAll();
    }

    /**
     * Method for retrieving a quote,
     * if it is present in the database, and the ID is of a valid format.
     *
     * @param id id of a quote
     * @return returns a {@link ResponseEntity} of a quote or bad request if a quote is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Quote> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * adds a new quote to the db if it is valid or returns an error.
     *
     * @param quote a json object {@link RequestBody} of a quote
     * @return returns a {@link ResponseEntity} of a quote and an HTTP status code 200
     *      or bad request
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Quote> add(@RequestBody Quote quote) {

        if (quote.person == null || isNullOrEmpty(quote.person.firstName)
              || isNullOrEmpty(quote.person.lastName)
              || isNullOrEmpty(quote.quote)) {
            return ResponseEntity.badRequest().build();
        }

        Quote saved = repo.save(quote);
        return ResponseEntity.ok(saved);
    }

    /**
     * Picks a random quote form the db.
     *
     * @return returns a random quote as a {@link ResponseEntity}
     */
    @GetMapping("rnd")
    public ResponseEntity<Quote> getRandom() {
        var quotes = repo.findAll();
        var idx = random.nextInt((int) repo.count());
        return ResponseEntity.ok(quotes.get(idx));
    }
}