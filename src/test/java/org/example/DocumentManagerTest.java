package org.example;

import org.example.DocumentManager.Author;
import org.example.DocumentManager.Document;
import org.example.DocumentManager.SearchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

  private DocumentManager documentManager;

  @BeforeEach
  void setUp() {
    documentManager = new DocumentManager();
  }

  @Test
  void testSaveNewDocument() {
    Document doc = Document.builder()
        .title("Title 1")
        .content("Content 1")
        .author(Author.builder().id("1").name("Author 1").build())
        .build();

    Document savedDoc = documentManager.save(doc);

    assertNotNull(savedDoc.getId());
    assertNotNull(savedDoc.getCreated());
    assertEquals("Title 1", savedDoc.getTitle());
    assertEquals("Content 1", savedDoc.getContent());
  }

  @Test
  void testSaveExistingDocument() {
    Document doc = Document.builder()
        .id("123")
        .title("Old Title")
        .content("Old Content")
        .author(Author.builder().id("1").name("Author 1").build())
        .created(Instant.now())
        .build();

    documentManager.save(doc);

    Document updatedDoc = Document.builder()
        .id("123")
        .title("New Title")
        .content("New Content")
        .author(Author.builder().id("1").name("Author 1").build())
        .created(doc.getCreated())
        .build();

    documentManager.save(updatedDoc);

    Optional<Document> foundDoc = documentManager.findById("123");
    assertTrue(foundDoc.isPresent());
    assertEquals("New Title", foundDoc.get().getTitle());
  }

  @Test
  void testFindById() {
    Document doc = Document.builder()
        .id("456")
        .title("Unique Title")
        .content("Special Content")
        .author(Author.builder().id("2").name("Author 2").build())
        .created(Instant.now())
        .build();

    documentManager.save(doc);

    Optional<Document> foundDoc = documentManager.findById("456");
    assertTrue(foundDoc.isPresent());
    assertEquals("Unique Title", foundDoc.get().getTitle());
  }

  @Test
  void testFindByIdNotFound() {
    Optional<Document> foundDoc = documentManager.findById("nonexistent-id");
    assertFalse(foundDoc.isPresent());
  }

  @Test
  void testSearchDocuments() {
    Document doc1 = Document.builder()
        .title("Learn Java")
        .content("Java Basics")
        .author(Author.builder().id("1").name("Author A").build())
        .created(Instant.now().minusSeconds(3600))
        .build();

    Document doc2 = Document.builder()
        .title("Advanced Java")
        .content("Deep Dive into Java")
        .author(Author.builder().id("2").name("Author B").build())
        .created(Instant.now().minusSeconds(1800))
        .build();

    documentManager.save(doc1);
    documentManager.save(doc2);

    SearchRequest request = SearchRequest.builder()
        .titlePrefixes(List.of("Learn"))
        .containsContents(List.of("Basics"))
        .authorIds(List.of("1"))
        .createdFrom(Instant.now().minusSeconds(7200))
        .createdTo(Instant.now())
        .build();

    List<Document> result = documentManager.search(request);

    assertEquals(1, result.size());
    assertEquals("Learn Java", result.get(0).getTitle());
  }

  @Test
  void testSearchNoMatches() {
    Document doc = Document.builder()
        .title("Python Guide")
        .content("Learn Python")
        .author(Author.builder().id("1").name("Author A").build())
        .created(Instant.now().minusSeconds(3600))
        .build();

    documentManager.save(doc);

    SearchRequest request = SearchRequest.builder()
        .titlePrefixes(List.of("Java"))
        .containsContents(List.of("Basics"))
        .authorIds(List.of("1"))
        .createdFrom(Instant.now().minusSeconds(7200))
        .createdTo(Instant.now())
        .build();

    List<Document> result = documentManager.search(request);

    assertTrue(result.isEmpty());
  }
}
