package edu.ucsb.cs156.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = UCSBOrganizationController.class)
@Import(TestConfig.class)
public class UCSBOrganizationControllerTests extends ControllerTestCase {

  @MockitoBean UCSBOrganizationRepository ucsbOrganizationRepository;

  @MockitoBean UserRepository userRepository;

  @Test
  public void logged_out_users_cannot_get_all() throws Exception {
    mockMvc.perform(get("/api/ucsborganization/all")).andExpect(status().is(403));
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_users_can_get_all() throws Exception {
    mockMvc.perform(get("/api/ucsborganization/all")).andExpect(status().is(200));
  }

  @Test
  public void logged_out_users_cannot_post() throws Exception {
    mockMvc
        .perform(
            post("/api/ucsborganization/post")
                .param("orgCode", "acm")
                .param("orgTranslationShort", "acm")
                .param("orgTranslation", "Association for Computing Machinery")
                .param("inactive", "false")
                .with(csrf()))
        .andExpect(status().is(403));
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_regular_users_cannot_post() throws Exception {
    mockMvc
        .perform(
            post("/api/ucsborganization/post")
                .param("orgCode", "acm")
                .param("orgTranslationShort", "acm")
                .param("orgTranslation", "Association for Computing Machinery")
                .param("inactive", "false")
                .with(csrf()))
        .andExpect(status().is(403));
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_user_can_get_all_organizations() throws Exception {
    // arrange
    UCSBOrganization acm =
        UCSBOrganization.builder()
            .orgCode("acm")
            .orgTranslationShort("acm")
            .orgTranslation("Association for Computing Machinery")
            .inactive(false)
            .build();

    UCSBOrganization ieee =
        UCSBOrganization.builder()
            .orgCode("IEEE")
            .orgTranslationShort("IEEE")
            .orgTranslation("Institute of Electrical and Electronics Engineers")
            .inactive(false)
            .build();

    ArrayList<UCSBOrganization> expectedOrganizations = new ArrayList<>();
    expectedOrganizations.addAll(Arrays.asList(acm, ieee));

    when(ucsbOrganizationRepository.findAll()).thenReturn(expectedOrganizations);

    // act
    MvcResult response =
        mockMvc.perform(get("/api/ucsborganization/all")).andExpect(status().isOk()).andReturn();

    // assert
    verify(ucsbOrganizationRepository, times(1)).findAll();
    String expectedJson = mapper.writeValueAsString(expectedOrganizations);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }

  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void an_admin_user_can_post_a_new_organization() throws Exception {
    // arrange
    UCSBOrganization acm =
        UCSBOrganization.builder()
            .orgCode("acm")
            .orgTranslationShort("acm")
            .orgTranslation("Association for Computing Machinery")
            .inactive(true)
            .build();

    when(ucsbOrganizationRepository.save(acm)).thenReturn(acm);

    // act
    MvcResult response =
        mockMvc
            .perform(
                post("/api/ucsborganization/post")
                    .param("orgCode", "acm")
                    .param("orgTranslationShort", "acm")
                    .param("orgTranslation", "Association for Computing Machinery")
                    .param("inactive", "true")
                    .with(csrf()))
            .andExpect(status().isOk())
            .andReturn();

    // assert
    verify(ucsbOrganizationRepository, times(1)).save(acm);
    String expectedJson = mapper.writeValueAsString(acm);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }

  @Test
  public void logged_out_users_cannot_get_by_id() throws Exception {
    mockMvc
        .perform(get("/api/ucsborganization").param("orgCode", "acm"))
        .andExpect(status().is(403));
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {
    UCSBOrganization acm =
        UCSBOrganization.builder()
            .orgCode("acm")
            .orgTranslationShort("acm")
            .orgTranslation("Association for Computing Machinery")
            .inactive(false)
            .build();

    when(ucsbOrganizationRepository.findById(eq("acm"))).thenReturn(Optional.of(acm));

    MvcResult response =
        mockMvc
            .perform(get("/api/ucsborganization").param("orgCode", "acm"))
            .andExpect(status().isOk())
            .andReturn();

    verify(ucsbOrganizationRepository, times(1)).findById(eq("acm"));
    String expectedJson = mapper.writeValueAsString(acm);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {
    when(ucsbOrganizationRepository.findById(eq("NOPE"))).thenReturn(Optional.empty());

    MvcResult response =
        mockMvc
            .perform(get("/api/ucsborganization").param("orgCode", "NOPE"))
            .andExpect(status().isNotFound())
            .andReturn();

    verify(ucsbOrganizationRepository, times(1)).findById(eq("NOPE"));
    Map<String, Object> json = responseToJson(response);
    assertEquals("EntityNotFoundException", json.get("type"));
    assertEquals("UCSBOrganization with id NOPE not found", json.get("message"));
  }

  // TESTING FOR PUT

  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void admin_can_edit_an_existing_organization() throws Exception {
    // arrange

    UCSBOrganization acmOrig =
        UCSBOrganization.builder()
            .orgCode("acm")
            .orgTranslationShort("acm")
            .orgTranslation("Association of Computing Machinery")
            .inactive(false)
            .build();

    //             organization.setOrgCode(incoming.getOrgCode());
    // organization.setOrgTranslationShort(incoming.getOrgTranslationShort());
    // organization.setOrgTranslation(incoming.getOrgTranslation());
    // organization.setInactive(incoming.getInactive());

    UCSBOrganization acmEdited =
        UCSBOrganization.builder()
            .orgCode("acm")
            .orgTranslationShort("acm organization")
            .orgTranslation("Association of Computing Machinery Industry")
            .inactive(true)
            .build();

    String requestBody = mapper.writeValueAsString(acmEdited);

    when(ucsbOrganizationRepository.findById(eq("acm"))).thenReturn(Optional.of(acmOrig));

    // act
    MvcResult response =
        mockMvc
            .perform(
                put("/api/ucsborganization")
                    .param("orgCode", "acm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestBody)
                    .with(csrf()))
            .andExpect(status().isOk())
            .andReturn();

    // assert
    verify(ucsbOrganizationRepository, times(1)).findById("acm");
    verify(ucsbOrganizationRepository, times(1))
        .save(acmEdited); // should be saved with updated info
    String responseString = response.getResponse().getContentAsString();
    assertEquals(requestBody, responseString);
  }

  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void admin_cannot_edit_organization_that_does_not_exist() throws Exception {
    // arrange

    UCSBOrganization editedorganization =
        UCSBOrganization.builder()
            .orgCode("sbhacks")
            .orgTranslationShort("SHhacks")
            .orgTranslation("Santa Barbara Hacks")
            .inactive(false)
            .build();

    String requestBody = mapper.writeValueAsString(editedorganization);

    when(ucsbOrganizationRepository.findById(eq("sbhacks"))).thenReturn(Optional.empty());

    // act
    MvcResult response =
        mockMvc
            .perform(
                put("/api/ucsborganization")
                    .param("orgCode", "sbhacks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestBody)
                    .with(csrf()))
            .andExpect(status().isNotFound())
            .andReturn();

    // assert
    verify(ucsbOrganizationRepository, times(1)).findById("sbhacks");
    Map<String, Object> json = responseToJson(response);
    assertEquals("UCSBOrganization with id sbhacks not found", json.get("message"));
  }
}
