package warehouse.com.auditservice.controller;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import warehouse.com.auditservice.model.dto.ActivityEventDto;
import warehouse.com.auditservice.model.dto.ActivityEventRequest;
import warehouse.com.auditservice.model.dto.BodyActivityEventRequest;
import warehouse.com.auditservice.service.ActivityEventService;

@Tag(name = "Audit event controller")
@RestController
@RequestMapping(path = {"/v1/audit-events", "/v1/events"},
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ActivityEventDtoController {

  private final ActivityEventService activityEventService;

  private final String[] allowedParamBindingsGet = {
      "actionSearch",
      "entityType",
      "entityId",
      "to",
      "from"
  };

  @InitBinder
  public void bindForm(HttpServletRequest request, WebDataBinder binder) {
    if (request.getMethod().equalsIgnoreCase(RequestMethod.GET.toString())) {
      binder.setAllowedFields(allowedParamBindingsGet);
    }
  }

  @Operation(summary = "Endpoint allows to get a list of events filtered by activity event request")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Received events"),
      @ApiResponse(responseCode = "404", description = "Latest events isn't found", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  @PageableAsQueryParam
  @GetMapping
  public Page<ActivityEventDto> getAllEvents(
      @Parameter ActivityEventRequest event,
      @Parameter(hidden = true) @PageableDefault(sort = {
          "timestamp"}, direction = Sort.Direction.DESC) Pageable pageable) {
    return activityEventService.getActivityEvents(event, pageable);
  }

  @Operation(summary = "Endpoint allows to export events in format of CSV file")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Events exported", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  @GetMapping(value = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public void exportEvents(
      @Parameter ActivityEventRequest event, HttpServletResponse response,
      @Parameter(hidden = true) @SortDefault(sort = {
          "timestamp"}, direction = Sort.Direction.DESC) Sort sort)
      throws IOException {
    response.setHeader(CONTENT_DISPOSITION, "inline; filename=\"Events_list.csv\"");
    response.setStatus(200);
    Writer writer = response.getWriter();
    activityEventService.exportCsv(writer, event, null, sort);
    writer.flush();
    writer.close();
  }

  @Operation(summary = "Endpoint allows to export events by ids in format of CSV file")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Events exported", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  @PostMapping(value = "/export", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public void exportEvents(
      @RequestBody(required = false) BodyActivityEventRequest bodyActivityEventRequest,
      HttpServletResponse response) throws IOException {
    response.setHeader(CONTENT_DISPOSITION, "inline; filename=\"Events_list.csv\"");
    response.setStatus(200);
    Writer writer = response.getWriter();
    activityEventService.exportCsv(writer, bodyActivityEventRequest.getFilter(),
        bodyActivityEventRequest, null);
    writer.flush();
    writer.close();
  }
}
