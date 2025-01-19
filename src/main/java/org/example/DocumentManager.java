package org.example;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

  private final List<Document> improvisedDb = new ArrayList<>();

  /**
   * Implementation of this method should upsert the document to your storage
   * And generate unique id if it does not exist, don't change [created] field
   *
   * @param document - document content and author data
   * @return saved document
   */
  public Document save(Document document) {
    validateDocument(document);
    final var existingDocument = findById(document.getId());

    existingDocument.ifPresentOrElse(
        doc -> improvisedDb.set(improvisedDb.indexOf(doc), document),
        () -> improvisedDb.add(document));

    return document;
  }

  private void validateDocument(Document document) {
    if (document.getId() == null) {
      document.setId(UUID.randomUUID().toString());
    }
    if (document.getCreated() == null) {
      document.setCreated(Instant.now());
    }
  }

  /**
   * Implementation this method should find documents which match with request
   *
   * @param request - search request, each field could be null
   * @return list matched documents
   */
  public List<Document> search(SearchRequest request) {
    return improvisedDb.stream()
        .filter(doc -> matchesTitlePrefixes(doc, request.getTitlePrefixes()))
        .filter(doc -> matchesContent(doc, request.getContainsContents()))
        .filter(doc -> request.getAuthorIds().contains(doc.getAuthor().getId()))
        .filter(doc -> doc.getCreated().isAfter(request.getCreatedFrom()) && doc.getCreated().isBefore(request.getCreatedTo()))
        .toList();
  }

  private boolean matchesTitlePrefixes(Document doc, List<String> prefixes) {
    return prefixes.stream().allMatch(prefix -> doc.getTitle().contains(prefix));
  }

  private boolean matchesContent(Document doc, List<String> contents) {
    return contents.stream().allMatch(content -> doc.getContent().contains(content));
  }

  /**
   * Implementation this method should find document by id
   *
   * @param id - document id
   * @return optional document
   */
  public Optional<Document> findById(String id) {
    final var existingDocument = improvisedDb.stream().filter(doc -> id.equals(doc.getId())).findFirst();
    return existingDocument;
  }

  @Data
  @Builder
  public static class SearchRequest {
    private List<String> titlePrefixes;
    private List<String> containsContents;
    private List<String> authorIds;
    private Instant createdFrom;
    private Instant createdTo;
  }

  @Data
  @Builder
  public static class Document {
    private String id;
    private String title;
    private String content;
    private Author author;
    private Instant created;
  }

  @Data
  @Builder
  public static class Author {
    private String id;
    private String name;
  }
}