package edu.ucsb.cs156.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.ucsb.cs156.example.entities.MenuItemReview;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.MenuItemReviewRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** This is a REST controller for MenuItemReviews */
@Tag(name = "MenuItemReviews")
@RequestMapping("/api/menuitemreviews")
@RestController
@Slf4j
public class MenuItemReviewController extends ApiController {

  @Autowired MenuItemReviewRepository menuItemReviewRepository;

  /**
   * List all MenuItemReviews
   *
   * @return an iterable of MenuItemReview
   */
  @Operation(summary = "List all menuitemreviews")
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("/all")
  public Iterable<MenuItemReview> allMenuItemReviews() {
    Iterable<MenuItemReview> menuitemreviews = menuItemReviewRepository.findAll();
    return menuitemreviews;
  }

  /**
   * Create a new MenuItemReview
   *
   * @param itemId id of review
   * @param reviewerEmail author of review
   * @param stars rating in stars
   * @param dateReviewed the date of review
   * @param comments comments left on review
   * @return the saved MenuItemReview
   */
  @Operation(summary = "Create a new menuitemreview")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping("/post")
  public MenuItemReview postMenuItemReviews(
      @Parameter(name = "itemId") @RequestParam long itemId,
      @Parameter(name = "reviewerEmail") @RequestParam String reviewerEmail,
      @Parameter(name = "stars") @RequestParam int stars,
      @Parameter(
              name = "dateReviewed",
              description =
                  "date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)")
          @RequestParam("dateReviewed")
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime dateReviewed,
      @Parameter(name = "comments") @RequestParam String comments)
      throws JsonProcessingException {

    // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    // See: https://www.baeldung.com/spring-date-parameters

    log.info("dateReviwed={}", dateReviewed);

    MenuItemReview menuItemReview = new MenuItemReview();
    menuItemReview.setItemId(itemId);
    menuItemReview.setReviewerEmail(reviewerEmail);
    menuItemReview.setStars(stars);
    menuItemReview.setDateReviewed(dateReviewed);
    menuItemReview.setComments(comments);

    MenuItemReview savedMenuItemReview = menuItemReviewRepository.save(menuItemReview);
    return savedMenuItemReview;
  }

  /**
   * Get a single MenuItemReview by id
   *
   * @param id the id of the MenuItemReview
   * @return a MenuItemReview
   */
  @Operation(summary = "Get a single menuitemreview")
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("")
  public MenuItemReview getById(@Parameter(name = "id") @RequestParam Long id) {
    MenuItemReview menuItemReview =
        menuItemReviewRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, id));

    return menuItemReview;
  }

  /**
   * Update a single menu item review
   *
   * @param id id of the menu item review to update
   * @param incoming the new menu item review
   * @return the updated MenuItemReview object
   */
  @Operation(summary = "Update a single menuitemreview")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PutMapping("")
  public MenuItemReview updateMenuItemReview(
      @Parameter(name = "id") @RequestParam Long id, @RequestBody @Valid MenuItemReview incoming) {

    MenuItemReview menuItemReview =
        menuItemReviewRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, id));

    // There would also be an incoming.getId, since the RequestBody includes that, but we use the
    // Long id parameter instead
    menuItemReview.setItemId(incoming.getItemId());
    menuItemReview.setReviewerEmail(incoming.getReviewerEmail());
    menuItemReview.setStars(incoming.getStars());
    menuItemReview.setDateReviewed(incoming.getDateReviewed());
    menuItemReview.setComments(incoming.getComments());

    menuItemReviewRepository.save(menuItemReview);

    return menuItemReview;
  }
}
