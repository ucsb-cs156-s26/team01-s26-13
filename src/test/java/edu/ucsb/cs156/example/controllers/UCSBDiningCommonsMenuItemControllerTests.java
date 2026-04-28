package edu.ucsb.cs156.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItem;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = UCSBDiningCommonsMenuItemController.class)
@Import(TestConfig.class)
public class UCSBDiningCommonsMenuItemControllerTests extends ControllerTestCase {

  @MockitoBean UCSBDiningCommonsMenuItemRepository ucsbDiningCommonsMenuItemRepository;

  @MockitoBean UserRepository userRepository;

  // Authorization tests for /api/ucsbdiningcommonsmenuitem/post
  // (Perhaps should also have these for put and delete)

  @Test
  public void logged_out_users_cannot_post() throws Exception {
    mockMvc
        .perform(
            post("/api/ucsbdiningcommonsmenuitem/post")
                .param("name", "spaghetti")
                .param("diningCommonsCode", "carillo")
                .param("station", "pasta")
                .with(csrf()))
        .andExpect(status().is(403));
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_regular_users_cannot_post() throws Exception {
    mockMvc
        .perform(
            post("/api/ucsbdiningcommonsmenuitem/post")
                .param("name", "spaghetti")
                .param("diningCommonsCode", "carillo")
                .param("station", "pasta")
                .with(csrf()))
        .andExpect(status().is(403)); // only admins can post
  }

  // // Tests with mocks for database actions

  // @WithMockUser(roles = {"USER"})
  // @Test
  // public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {
  //
  //
  //   UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItem =
  //       UCSBDiningCommonsMenuItem.builder()
  //           .name("spaghetti")
  //           .diningCommonsCode("carillo")
  //           .station("pasta")
  //           .build();
  //
  //
  // when(ucsbDiningCommonsMenuItemRepository.findById(eq(7L))).thenReturn(Optional.of(ucsbDiningCommonsMenuItem));
  //
  //   // act
  //   MvcResult response =
  //       mockMvc
  //           .perform(get("/api/ucsbdiningcommonsmenuitem").param("id", "7"))
  //           .andExpect(status().isOk())
  //           .andReturn();
  //
  //   // assert
  //
  //   verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(eq(7L));
  //   String expectedJson = mapper.writeValueAsString(ucsbDiningCommonsMenuItem);
  //   String responseString = response.getResponse().getContentAsString();
  //
  //   assertEquals(expectedJson, responseString);
  // }

  // @WithMockUser(roles = {"USER"})
  // @Test
  // public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws
  // Exception {
  //
  //   // arrange
  //
  //   when(ucsbDiningCommonsMenuItemRepository.findById(eq(7L))).thenReturn(Optional.empty());
  //
  //   // act
  //   MvcResult response =
  //       mockMvc
  //           .perform(get("/api/ucsbdiningcommonsmenuitem").param("id", "7"))
  //           .andExpect(status().isNotFound())
  //           .andReturn();
  //
  //   // assert
  //
  //   verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(eq(7L));
  //   Map<String, Object> json = responseToJson(response);
  //   assertEquals("EntityNotFoundException", json.get("type"));
  //   assertEquals("UCSBDiningCommonsMenuItem with id 7 not found", json.get("message"));
  // }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_user_can_get_all_ucsbdiningcommonsmenuitems() throws Exception {

    UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItem1 =
        UCSBDiningCommonsMenuItem.builder()
            .name("tacos")
            .diningCommonsCode("dlg")
            .station("mexican")
            .build();

    UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItem2 =
        UCSBDiningCommonsMenuItem.builder()
            .name("pasta")
            .diningCommonsCode("ortega")
            .station("italian")
            .build();

    ArrayList<UCSBDiningCommonsMenuItem> expectedItems = new ArrayList<>();
    expectedItems.addAll(Arrays.asList(ucsbDiningCommonsMenuItem1, ucsbDiningCommonsMenuItem2));

    when(ucsbDiningCommonsMenuItemRepository.findAll()).thenReturn(expectedItems);

    // act
    MvcResult response =
        mockMvc
            .perform(get("/api/ucsbdiningcommonsmenuitem/all"))
            .andExpect(status().isOk())
            .andReturn();

    // assert

    verify(ucsbDiningCommonsMenuItemRepository, times(1)).findAll();
    String expectedJson = mapper.writeValueAsString(expectedItems);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }

  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void an_admin_user_can_post_a_new_ucsbdiningcommonsmenuitem() throws Exception {
    // arrange

    UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItem1 =
        UCSBDiningCommonsMenuItem.builder()
            .name("tacos")
            .diningCommonsCode("dlg")
            .station("pasta")
            .build();

    when(ucsbDiningCommonsMenuItemRepository.save(eq(ucsbDiningCommonsMenuItem1)))
        .thenReturn(ucsbDiningCommonsMenuItem1);

    // act
    MvcResult response =
        mockMvc
            .perform(
                post("/api/ucsbdiningcommonsmenuitem/post")
                    .param("name", "tacos")
                    .param("diningCommonsCode", "dlg")
                    .param("station", "pasta")
                    .with(csrf()))
            .andExpect(status().isOk())
            .andReturn();

    // assert
    verify(ucsbDiningCommonsMenuItemRepository, times(1)).save(ucsbDiningCommonsMenuItem1);
    String expectedJson = mapper.writeValueAsString(ucsbDiningCommonsMenuItem1);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }
}
