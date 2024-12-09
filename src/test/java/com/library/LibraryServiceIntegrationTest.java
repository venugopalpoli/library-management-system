package com.library;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LibraryServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Example book data
    private String exampleUserJson = """
                {
                    "userName": "test",
                    "password": "test"
                }
            """;

    private String exampleInvalidUser1 = """
                {
                    "userName": "venu",
                    "password": "test"
                }
            """;

    private String exampleInvalidUser2 = """
                {
                    "userName": "venu"
                }
            """;
    private String exampleBookJson = """
                {
                    "isbn": "12345",
                    "title": "TestBook",
                    "author": "TestAuthor",
                    "publicationYear": 2023,
                    "availableCopies": 10
                }
            """;
    private String exampleBookJson2 = """
                {
                    "isbn": "12346",
                    "title": "TestBook",
                    "author": "TestAuthor",
                    "publicationYear": 2023,
                    "availableCopies": 5
                }
            """;

    private String exampleBookJson3 = """
                {
                    "isbn": "12347",
                    "title": "TestBook",
                    "author": "TestAuthor",
                    "publicationYear": 2023,
                    "availableCopies": 1
                }
            """;

    private String exampleInvalidBook = """
                {
                    "isbn": "12347",
                    "author": "TestAuthor",
                    "publicationYear": 2023,
                    "availableCopies": 1
                }
            """;

    @Test
    void testAddBook() throws Exception {

        // Get the jwt token by passing valid user credentials
        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exampleUserJson));
        String token = resultActions.andReturn().getResponse().getContentAsString();

        //Adding Book to the library
        addBook(token);

        //Adding same Book to the library as expecting is already exist
        mockMvc.perform(post("/services/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(exampleBookJson))
                .andExpect(status().isConflict())
                .andExpect(content().string("Book already exists with this isbn : 12345"));

        //Remove book from the library
        removeBook(token);

        //Unhappypath # Adding same Book to the library
        mockMvc.perform(post("/services/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(exampleInvalidBook))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Book must have a valid ISBN, title, and author."));
    }

    @Test
    void testFindBookByISBN() throws Exception {

        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exampleUserJson));
        String token = resultActions.andReturn().getResponse().getContentAsString();

        //Unhappypath # Finding Book by ISBN from the library which is not exist
        mockMvc.perform(get("/services/library/books/book/12345")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No book is available with this isbn : 12345"));

        //Adding Book to the library
        addBook(token);

        //Finding Book by ISBN from the library
        mockMvc.perform(get("/services/library/books/book/12345")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"isbn\":\"12345\",\"title\":\"TestBook\",\"author\":\"TestAuthor\",\"publicationYear\":2023,\"availableCopies\":10}"));

        //Remove book from the library
        removeBook(token);

        //Unhappypath # Finding Book by empty ISBN from the library
        mockMvc.perform(get("/services/library/books/book/ ")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ISBN must not be empty or null."));
    }

    @Test
    void testFindBookByAuthor() throws Exception {

        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exampleUserJson));
        String token = resultActions.andReturn().getResponse().getContentAsString();

        //Unhappypath # Finding Book by author from the library which is not exist
        mockMvc.perform(get("/services/library/books/TestAuthor")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No book is available with this author : TestAuthor"));

        //Unhappypath # Finding Book by empty author from the library
        mockMvc.perform(get("/services/library/books/ ")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Author must not be empty or null."));

        //Adding Book to the library
        addBook(token);

        //Finding Book by author from the library where is only one exist
        mockMvc.perform(get("/services/library/books/TestAuthor")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"isbn\":\"12345\",\"title\":\"TestBook\",\"author\":\"TestAuthor\",\"publicationYear\":2023,\"availableCopies\":10}]"));

        //Adding another Book to the library with the same author
        mockMvc.perform(post("/services/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(exampleBookJson2))
                .andExpect(status().isCreated())
                .andExpect(content().string("{\"isbn\":\"12346\"}"));

        //Finding Book by author from the library where it does have two books exist with the author
        mockMvc.perform(get("/services/library/books/TestAuthor")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"isbn\":\"12345\",\"title\":\"TestBook\",\"author\":\"TestAuthor\",\"publicationYear\":2023,\"availableCopies\":10},{\"isbn\":\"12346\",\"title\":\"TestBook\",\"author\":\"TestAuthor\",\"publicationYear\":2023,\"availableCopies\":5}]"));

        //Remove book from the library
        removeBook(token);
    }

    @Test
    void testRemoveBook() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exampleUserJson));
        String token = resultActions.andReturn().getResponse().getContentAsString();

        // As other tests are covering remove book, here try to remove book which is not found
        mockMvc.perform(delete("/services/library/books/remove/12345")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No book is available with this isbn : 12345"));

        //Unhappypath # remove Book by empty ISBN from the library
        mockMvc.perform(delete("/services/library/books/remove/ ")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ISBN must not be empty or null."));
    }

    @Test
    void testBorrowBook() throws Exception {

        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exampleUserJson));
        String token = resultActions.andReturn().getResponse().getContentAsString();

        //Adding Book to the library
        addBook(token);

        // Now, borrow the book
        mockMvc.perform(patch("/services/library/books/borrow/12345")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"isbn\":\"12345\"}"));

        //Finding Book by ISBN from the library and expecting available copies are 9
        mockMvc.perform(get("/services/library/books/book/12345")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"isbn\":\"12345\",\"title\":\"TestBook\",\"author\":\"TestAuthor\",\"publicationYear\":2023,\"availableCopies\":9}"));

        // Remove book from library
        removeBook(token);

        //Unhappypath # Add book with available copies 1
        mockMvc.perform(post("/services/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(exampleBookJson3))
                .andExpect(status().isCreated())
                .andExpect(content().string("{\"isbn\":\"12347\"}"));

        // Now, borrow the book
        mockMvc.perform(patch("/services/library/books/borrow/12347")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"isbn\":\"12347\"}"));

        //Again, borrow the book
        mockMvc.perform(patch("/services/library/books/borrow/12347")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No copies of the book is available"));

        // Remove book from library
        mockMvc.perform(delete("/services/library/books/remove/12347")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"isbn\":\"12347\"}"));

        //Unhappypath # try to borrow book which is not exist
        mockMvc.perform(patch("/services/library/books/borrow/12347")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No book is available with this isbn : 12347"));

        //Unhappypath # borrow Book by empty ISBN from the library
        mockMvc.perform(patch("/services/library/books/borrow/ ")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ISBN must not be empty or null."));

    }

    @Test
    void testReturnBook() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exampleUserJson));
        String token = resultActions.andReturn().getResponse().getContentAsString();

        //Adding Book to the library
        addBook(token);

        // Now, return the book
        mockMvc.perform(patch("/services/library/books/return/12345")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"isbn\":\"12345\"}"));

        //Finding Book by ISBN from the library and expecting available copies are 11
        findBook(token);
        // Remove book from library
        removeBook(token);

        // UnhappyPath # Now, return the book which is not exist
        mockMvc.perform(patch("/services/library/books/return/12345")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No book is available with this isbn : 12345"));

        //Unhappypath # borrow Book by empty ISBN from the library
        mockMvc.perform(patch("/services/library/books/return/ ")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ISBN must not be empty or null."));
    }

    @Test
    void testInvalidUser() throws Exception {
        // Unhappypath # Get the jwt token by passing invalid user credentials
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exampleInvalidUser1))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid Credentials"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(exampleInvalidUser2))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Please provide user credentials"));
    }

    private void addBook(String token) throws Exception {
        mockMvc.perform(post("/services/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(exampleBookJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("{\"isbn\":\"12345\"}"));
    }

    private void findBook(String token) throws Exception {
        mockMvc.perform(get("/services/library/books/book/12345")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"isbn\":\"12345\",\"title\":\"TestBook\",\"author\":\"TestAuthor\",\"publicationYear\":2023,\"availableCopies\":11}"));

    }

    private void removeBook(String token) throws Exception {
        // Remove book from library
        mockMvc.perform(delete("/services/library/books/remove/12345")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"isbn\":\"12345\"}"));
    }
}
