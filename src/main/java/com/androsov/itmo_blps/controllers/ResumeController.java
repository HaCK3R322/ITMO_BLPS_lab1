package com.androsov.itmo_blps.controllers;

import com.androsov.itmo_blps.annotations.FailOnGetParams;
import com.androsov.itmo_blps.dto.EducationDto;
import com.androsov.itmo_blps.dto.ResumeDto;
import com.androsov.itmo_blps.dto.converters.EducationDtoToEducationConverter;
import com.androsov.itmo_blps.dto.converters.EducationToEducationDtoConverter;
import com.androsov.itmo_blps.dto.converters.ResumeDtoToResumeConverter;
import com.androsov.itmo_blps.dto.converters.ResumeToResumeDtoConverter;
import com.androsov.itmo_blps.entities.Image;
import com.androsov.itmo_blps.entities.resume.Education;
import com.androsov.itmo_blps.entities.resume.Resume;
import com.androsov.itmo_blps.repositories.EducationRepository;
import com.androsov.itmo_blps.servicies.ImageService;
import com.androsov.itmo_blps.servicies.ResumeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@RestController
@CrossOrigin
@AllArgsConstructor
@Validated
public class ResumeController {

    ResumeService resumeService;
    ImageService imageService;
    ResumeDtoToResumeConverter resumeDtoToResumeConverter;
    ResumeToResumeDtoConverter resumeToResumeDtoConverter;

    EducationRepository educationRepository;
    EducationDtoToEducationConverter educationDtoToEducationConverter;
    EducationToEducationDtoConverter educationToEducationDtoConverter;

    @PostMapping("/resume")
    public ResponseEntity<?> create(@Valid @RequestBody ResumeDto resumeDto,
                                    Principal principal) {
        if (resumeDto.getUsername() == null || !resumeDto.getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Current user has no access to create resume for user " + resumeDto.getUsername());
        }

        Resume resume = resumeDtoToResumeConverter.convert(resumeDto);

        Resume savedResume = resumeService.saveResume(resume);

        return new ResponseEntity<>(resumeToResumeDtoConverter.convert(savedResume), HttpStatus.CREATED);
    }

    @GetMapping("/resume")
    @FailOnGetParams
    public ResponseEntity<List<ResumeDto>> getAll(Principal principal, HttpServletRequest request) {
        List<Resume> resumes = resumeService.getAllForCurrentPrincipal(principal);

        List<ResumeDto> resumeDtos = resumeToResumeDtoConverter.convert(resumes);

        return new ResponseEntity<>(resumeDtos, HttpStatus.OK);
    }

    @GetMapping("/resume/{id}")
    @FailOnGetParams
    public ResponseEntity<?> getById(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        Resume resume = resumeService.getById(id);

        if (resume.getUser().getUsername() == null || !resume.getUser().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Current user has no access to this resume id");
        }

        ResumeDto resumeDto = resumeToResumeDtoConverter.convert(resume);

        return new ResponseEntity<>(resumeDto, HttpStatus.OK);
    }

    @PatchMapping("/resume/{resumeId}/attach/image/{imageId}")
    @FailOnGetParams
    public ResponseEntity<?> attachImage(@PathVariable Long resumeId, @PathVariable Long imageId, HttpServletRequest request) throws EntityNotFoundException {
        if (!resumeService.existsById(resumeId) || !imageService.existsById(imageId)) {
            return ResponseEntity.badRequest().body("Invalid resume or image ID (is null)");
        }

        Resume resume = resumeService.getById(resumeId);
        Image image = imageService.getImageById(imageId);

        resume.setResumeImage(image);

        resume = resumeService.saveResume(resume);

        return new ResponseEntity<>(resumeToResumeDtoConverter.convert(resume), HttpStatus.OK);
    }

    @PostMapping("/resume/{resumeId}/education")
    @FailOnGetParams
    public ResponseEntity<?> addEducation(@PathVariable Long resumeId, @RequestBody EducationDto educationDto, Principal principal) throws EntityNotFoundException {
        if (!resumeService.existsById(resumeId)) {
            return ResponseEntity.badRequest().body("Invalid resume ID)");
        }

        // TODO: rewrite it like [resumeService.currentUserHasAccessTo(Long resumeId)]
        Resume resume = resumeService.getById(educationDto.getResumeId());
        if (resume.getUser().getUsername() == null || !resume.getUser().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Current user has no access to this resume id");
        }

        Education education = educationRepository.save(educationDtoToEducationConverter.convert(educationDto));

        return ResponseEntity.status(HttpStatus.CREATED).body(educationToEducationDtoConverter.convert(education));
    }
}
